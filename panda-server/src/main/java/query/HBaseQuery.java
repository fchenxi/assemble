package query;

import cn.panda.hbase.distributor.AbstractRowKeyDistributor;
import cn.panda.hbase.distributor.DistributedScanner;
import cn.panda.hbase.distributor.RowKeyDistributorByHashPrefix;
import cn.panda.utils.PropertyFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;


public class HBaseQuery {

    private Logger logger = LoggerFactory.getLogger(HBaseQuery.class);

    private static Configuration conf = null;
    private String PRINCIPAL;
    private String KEYTAB_PATH;
    private final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("R");
    private final byte[] COLUMN_NAME = Bytes.toBytes("id");
    private final long BATCH_SIZE_MILLION = 1000000;
    private final byte BUCKET_SIZE = (byte)5;
    private final String[] SCHEMA_NAME = {"retail_gms::auto_close_account_src","retail_gms::account_bill_pool_sp",
            "retail_gms::bill_account_control_src","gyl_gms::authority_user_category_rt","retail_gos::inventory_book"};
    private final byte[] lowerPrefix = new byte[] { 0x0 };
    private final byte[] upperPrefix = new byte[] { 0x0 };

    private Connection conn;
    private static ThlRowKeyGenerator rowKeyGenerator;

    static {
        rowKeyGenerator = ThlRowKeyGenerator.getInstance();
    }

    public HBaseQuery() {
        init();
        userLogin();
    }

    public void batch(String tableName, String schema, String queryTable, short dmlType, short nodeIndex,
                      long startTS, long seqNo, short fragNo) {

        Table table = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf(tableName));
            for (int i = 0; i < BATCH_SIZE_MILLION; i++) {
                dmlType = (short) new Random().nextInt(3);
                int schemaRandomIndex = new Random().nextInt(5);
                startTS += i * 10000;
                byte[] oriRowKey = new byte[0];
                try {
                    oriRowKey = rowKeyGenerator.generateRowKey(startTS, SCHEMA_NAME[schemaRandomIndex],
                            dmlType);
                    // Hash prefix with 64 buckets
//                    AbstractRowKeyDistributor keyDistributor =
//                            new RowKeyDistributorByHashPrefix(
//                                    new RowKeyDistributorByHashPrefix.OneByteSimpleHash(BUCKET_SIZE));
//
//                    byte[] bucketRowKey = keyDistributor.getDistributedKey(oriRowKey);
//                    Put put = new Put(bucketRowKey);
                    Put put = new Put(oriRowKey);
                    logger.info("bucketRowKey: " + Bytes.toStringBinary(oriRowKey));
                    put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(String.valueOf(i)));
                    table.put(put);

                } catch (NoSuchAlgorithmException e) {
                }
            }

        } catch (IOException e) {

        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void query(String tableName, String schema, short dmlType,
                          long startTS, long endTS)
            throws UnsupportedEncodingException {

        Table table = null;
        ResultScanner results = null;



        try {
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf(tableName));

            byte[] startRow = rowKeyGenerator.generateRowKey(startTS, schema, dmlType);
            byte[] endRow = rowKeyGenerator.generateRowKey(endTS, schema, dmlType);
//            startRow = Bytes.add(lowerPrefix, startRow);
//            endRow = Bytes.add(upperPrefix, endRow);
            Scan scan = new Scan(startRow, endRow);
            // Hash prefix with 64 buckets
            AbstractRowKeyDistributor keyDistributor =
                    new RowKeyDistributorByHashPrefix(
                            new RowKeyDistributorByHashPrefix.OneByteSimpleHash(BUCKET_SIZE));
            results = DistributedScanner.create(table, scan, keyDistributor);

            logger.info("------------------- query condition -------------------");
            logger.info("bucketStartRow: " + Bytes.toStringBinary(keyDistributor.getDistributedKey(startRow)));
            logger.info("bucketEndRow: " + Bytes.toStringBinary(keyDistributor.getDistributedKey(endRow)));

//            results = table.getScanner(scan);
            logger.info("------------------- query result -------------------");
//            Iterator<Result> iter = results.iterator();
//            for (int i = 0; iter.hasNext(); i++) {
//                Result result = iter.next();
//                logger.info("Cell: " + Bytes.toString(result.getRow()));
//            }
            for (Result result : results) {
                for (Cell cell : result.rawCells()) {
                    logger.info("Cell: " + cell + ", Value: " + Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(), cell.getValueLength()));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {

                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (results != null) {
                results.close();
            }
        }
    }

    private void userLogin() {
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(PRINCIPAL, KEYTAB_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void init() {
        Properties env = PropertyFile.getProprties("kerberos-env.properties");
        String HBASE_MASTER_PRINCIPAL = env.getProperty("dc.env.kerberos.hbase.master.principal");
        String HBASE_RS_PRINCIPAL = env.getProperty("dc.env.kerberos.hbase.region.principal");
        String ZOOKEEPER_QUORUM = env.getProperty("dc.env.kerberos.hbase.zookeeper.quorum");
        String ZOOKEEPER_CLIENTPORT = env.getProperty("dc.env.kerberos.zookeeper.property.clientPort");
        String ZNODE_PARENT = env.getProperty("dc.env.zookeeper.znode.parent");
        PRINCIPAL = env.getProperty("dc.env.kerberos.hbase.principal");
        KEYTAB_PATH = env.getProperty("dc.env.kerberos.hbase.keytab.path");
        //tableName = env.getProperty("dc.env.hbase.tableName");
        conf = HBaseConfiguration.create();
        conf.addResource("hbase-site.xml");

        conf.set("hbase.master.kerberos.principal", HBASE_MASTER_PRINCIPAL);
        conf.set("hbase.regionserver.kerberos.principal", HBASE_RS_PRINCIPAL);
        conf.set("kerberos.principal", PRINCIPAL);
        conf.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM);
        conf.set("hbase.zookeeper.property.clientPort", ZOOKEEPER_CLIENTPORT);
        conf.set("zookeeper.znode.parent", ZNODE_PARENT);
    }
    public static void main(String[] args){

    }

}
