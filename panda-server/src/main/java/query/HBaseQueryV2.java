package query;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import cn.panda.common.utils.PropertyFile;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;


public class HBaseQueryV2 {
    private static Configuration conf = null;
    //private static String tableName = null;
    private String PRINCIPAL;
    private String KEYTAB_PATH;
    //    private static final byte[] TABLE_NAME = Bytes.toBytes("retail_test");
    private static final byte[] CF_META = Bytes.toBytes("CF_META");
    private static final byte[] CF_DATA = Bytes.toBytes("CF_DATA");
    private static final byte[] COLUMN_NAME = Bytes.toBytes("schema");

    private static final String INSERT_UPDATE_SCHEMA = "/pz.retail_gms.gms_period_balance.1.avsc";
    private static final String DELETE_SCHEMA = "/delete_binlog.1.avsc";
    public HBaseQueryV2() {
        init();
        userLogin();
    }

    public void insert() {

    }

    public void hbaseCrud(String tableName, byte[] startRowKey, byte[] endRowKey) throws UnsupportedEncodingException {
        Connection conn = null;
        Table table = null;
        ResultScanner results = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf(tableName));
//            Scan scan = new Scan(startRowKey, endRowKey);
            Scan scan = new Scan();
            System.out.println("startRowKey: " + Bytes.toStringBinary(startRowKey));
            System.out.println("endRowKey  : " + Bytes.toStringBinary(endRowKey));

            results = table.getScanner(scan);
            for (Result result : results) {
                byte[] input = result.getValue(CF_DATA, Bytes.toBytes("rows"));
                String schemaNa = new String(result.getValue(CF_META, Bytes.toBytes("schema")));
                String tableNa = new String(result.getValue(CF_META, Bytes.toBytes("table_name")));
                int rowSize = Bytes.toInt(result.getValue(CF_META, Bytes.toBytes("row_size")));
                short dmlType = Bytes.toShort(result.getValue(CF_META, Bytes.toBytes("dml_type")));

                System.out.println("timestamp: " +
                        Bytes.toLong(result.getValue(CF_META, Bytes.toBytes("binlog_timestamp"))));
                System.out.println("schemaNa: " + schemaNa + ",tableName:" + tableNa);
                System.out.println("rowkey: " + Bytes.toStringBinary(result.getRow()));
                System.out.println("rowSize: " + rowSize);
                Schema.Parser parser = new Schema.Parser();
                Schema schema = null;
                if (dmlType == DmlType.DELETE) {
                    schema = parser.parse(getClass().getResourceAsStream(this.DELETE_SCHEMA));

                } else {
                    schema = parser.parse(getClass().getResourceAsStream(this.INSERT_UPDATE_SCHEMA));
                }
                List<Record> records = this.decodeDataumAvro(input, schema, rowSize);
                if (records.size() > 0) {
                    System.out.println("printRows ##################################");
                    this.printRows(records, rowSize, schema);
                    System.out.println("records size: " + records.size());
                    System.out.println("printRows ################################## end");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (results != null) {
                results.close();
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

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }
    private void printRows(List<Record> records, int rowSize, Schema schema) {

        Schema.Parser parser = new Schema.Parser();
//            Schema schema = parser.parse(getClass().getResourceAsStream(this.INSERT_UPDATE_SCHEMA));
        List<Field> fields = schema.getFields();
        for (int i = 0; i < rowSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (Field field : fields) {
                Object value = records.get(i).get(field.name());
                sb.append(value).append(" ");
            }
            System.out.println("rows: " + sb.toString());
        }

    }
//    public Record decodeDataumAvro(byte[] input, Schema schema) {
//        ByteArrayInputStream bis = null;
//        GenericDatumReader<Record> reader;
//        try {
//            reader = new GenericDatumReader<Record>(schema);
//            bis = new ByteArrayInputStream(input);
//            Decoder decoder = DecoderFactory.get().directBinaryDecoder(bis, null);
//            return reader.read(null, decoder);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        } finally {
//            if (bis != null) {
//                try {
//                    bis.close();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//        }
//        return null;
//    }
    public List<Record> decodeDataumAvro(byte[] input, Schema schema, int rowSize) {
        ByteArrayInputStream bis = null;
        GenericDatumReader<Record> reader;
        List<Record> list = new ArrayList<>();
        try {
            reader = new GenericDatumReader<Record>(schema);
            bis = new ByteArrayInputStream(input);
            Decoder decoder = DecoderFactory.get().directBinaryDecoder(bis, null);
            try {
                for (int i = 0; i < rowSize; i++) {
                    list.add(reader.read(null, decoder));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return list;
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

//    public static void main(String[] args){
//        new HBaseQuery();
//        HashChoreWoker worker = new HashChoreWoker(1000000,10);
//        byte [][] splitKeys = worker.calcSplitKeys();
//        HBaseAdmin admin = null;
//        try {
//            admin = new HBaseAdmin(conf);
//            TableName tableName = TableName.valueOf("hash_split_table");
//            if (admin.tableExists(tableName)) {
//                try {
//                    admin.disableTable(tableName);
//                } catch (Exception e) {
//                    System.err.print(e.getMessage());
//                }
//                admin.deleteTable(tableName);
//            }
//            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
//            HColumnDescriptor columnDesc = new HColumnDescriptor(Bytes.toBytes("info"));
//            columnDesc.setMaxVersions(1);
//            tableDesc.addFamily(columnDesc);
//            admin.createTable(tableDesc ,splitKeys);
//        } catch (IOException e) {
//            System.err.print(e.getMessage());
//        } finally {
//            try {
//                admin.close();
//            } catch (IOException e) {
//                System.err.print(e.getMessage());
//            }
//        }
//
//
//    }

    public Connection getConnection() throws IOException {
        return ConnectionFactory.createConnection(conf);
    }
}
