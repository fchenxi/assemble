package cn.panda.hbase.service;

import cn.panda.common.utils.TimeUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import query.HBaseQueryV2;
import query.ThlRowKeyGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class HBaseQueryTestV2 {

    private ThlRowKeyGenerator thlRowKeyGenerator;
    @Before
    public void prepare() {
        System.setProperty("hadoop.home.dir", "d:\\winutil\\");
        thlRowKeyGenerator = ThlRowKeyGenerator.getInstance();
    }

    @Test
    public void testSchemaRangeQuery() throws UnsupportedEncodingException {

        HBaseQueryV2 client = new HBaseQueryV2();
        String HBASE_TABLE = "gms_period_balance";
        String schemaName = "ztl";
        String tableName = "gms_period_balance";
        short dmlType = 0;
//        long startTS = 1450889366;
//        long endTS = 1451963509;
//        long startTS = 1492593284;
//        long endTS = 1492593316;
        long startTS = 1392659100000L;
        long endTS = 1592659120000L;
        short nodeIndex = 0;
        short seqNo = 0;
        short fragNo = 0;
        try {
            // Be careful,the rowkey timestamp need reversed.
            byte[] startRowKey = thlRowKeyGenerator.generateRowKey(endTS, schemaName, tableName,
                    dmlType, nodeIndex, seqNo, fragNo);
            byte[] endRowKey = thlRowKeyGenerator.generateRowKey(startTS, schemaName, tableName,
                    dmlType, nodeIndex, seqNo, fragNo);
            client.hbaseCrud(HBASE_TABLE, startRowKey, endRowKey);
        } catch (NoSuchAlgorithmException e) {

        }


    }
    @Test
    public void testTSRangeQuery() throws UnsupportedEncodingException {

        HBaseQueryV2 client = new HBaseQueryV2();
        String HBASE_TABLE = "rowkey_range_test";
        String schemaName = "retail_gms";
        String tableName = "auto_close_account_src";
        short dmlType = 1;
        long startTS = 1677324366;
        long endTS = 1299999999;
        short nodeIndex = 0;
        short seqNo = 0;
        short fragNo = 0;
        try {
            byte[] startRowKey = thlRowKeyGenerator.generateRowKey(startTS, schemaName, tableName,
                    dmlType, nodeIndex, seqNo, fragNo);
            byte[] endRowKey = thlRowKeyGenerator.generateRowKey(endTS, schemaName, tableName,
                    dmlType, nodeIndex, seqNo, fragNo);

            client.hbaseCrud(HBASE_TABLE, startRowKey, endRowKey);
        } catch (NoSuchAlgorithmException e) {

        }


    }
    @Test
    public void testTSReversed() {
        String schemaName = "ztl";
        String tableName = "gms_period_balance";
        short dmlType = 0;

        long startTS = 1451963509;
        long endTS = 1450889366;
        short nodeIndex = 0;
        long seqNo = 0;
        short fragNo = 0;
        System.out.println("startTS byteBinary: " +
                Bytes.toStringBinary(Bytes.toBytes(TimeUtils.recoveryTimeMillis(startTS))));
        byte[] rowKey = null;
        try {
            rowKey = thlRowKeyGenerator.generateRowKey(startTS, schemaName,
                    tableName, dmlType, nodeIndex, seqNo, fragNo);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("startTs rowKey    :" + Bytes.toStringBinary(rowKey));

        System.out.println("startTS: " + startTS + ", reversed startTS: " + TimeUtils.recoveryTimeMillis(startTS));
        System.out.println("endTS: " + endTS + ", reversed endTS: " + TimeUtils.recoveryTimeMillis(endTS));
    }

    @Test
    public void testReceoverTS() {
        long startTS = System.currentTimeMillis();
        System.out.println(startTS);
        long endTS = 1492594339396L;
    }
}