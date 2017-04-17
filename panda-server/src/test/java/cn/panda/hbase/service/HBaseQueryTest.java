package cn.panda.hbase.service;

import cn.panda.hbase.distributor.AbstractRowKeyDistributor;
import cn.panda.hbase.distributor.RowKeyDistributorByHashPrefix;
import cn.panda.utils.TimeUtils;
import org.junit.Before;
import query.ThlRowKeyGenerator;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import query.HBaseQuery;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HBaseQueryTest {

    @Before
    public void prepare() {
        System.setProperty("hadoop.home.dir", "d:\\winutil\\");
    }


    @Test
    public void testQuery() throws UnsupportedEncodingException {

        HBaseQuery client = new HBaseQuery();
        String HBASE_TABLE = "rowkey_range_test";
        String schema = "retail_gms::auto_close_account_src";
        short dmlType = 1;
//        long startTS = 1598319566;
//        long endTS = 1207324366;
        long startTS = 1677324366;
        long endTS = 1299999999;
        client.query(HBASE_TABLE, schema, dmlType, startTS, endTS);

    }
    @Test
    public void testBatch()
            throws UnsupportedEncodingException {

        HBaseQuery query = new HBaseQuery();
        String HBASE_TABLE = "rowkey_range_test";
        String schema = "gyl_gms";
        String table = "authority_user_category_rt";
        short dmlType = 3;
        short nodeIndex = 12;
        long startTS = 1467324366;
        long endTS = 1478319566;
        long seqNo = 90;
        short fragNo = 6;
//        query.hbaseCrud(HBASE_TABLE, schema, table, dmlType, nodeIndex, startTS, endTS, seqNo);
        query.batch(HBASE_TABLE, schema, table, dmlType, nodeIndex, startTS, seqNo, fragNo);

    }

    @Test
    public void testRowKeyBytes() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ThlRowKeyGenerator generator = ThlRowKeyGenerator.getInstance();
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        short dmlType = 1;
        byte[] dmlTypeBytes = Bytes.toBytes(dmlType);
        System.out.println("dmlType bytesString: " + Bytes.toStringBinary(dmlTypeBytes));

        long ts = 1483221966;
        byte[] tsByte = Bytes.toBytes(ts);
        System.out.println("tsBytesString: " + Bytes.toStringBinary(tsByte));

        String table = "bill_account_control_src";
        byte[] prefixByte = Bytes.add(tsByte, md5Digest.digest(Bytes.toBytes(table)));
        System.out.println("prefixByte: " + Bytes.toStringBinary(prefixByte));

        String schema = "retail_gms";
        short nodeIndex = 12;
        long startTS = 1483221966;
        long endTS = 1488319566;
        long seqNo = 90;
        short fragNo = 6;
        byte[] rowkey = generator.generateRowKey(startTS, schema, table, dmlType, nodeIndex, seqNo, fragNo);

        System.out.println("rowkey byteString: " + Bytes.toStringBinary(rowkey));

        long startT = 1487324366;
        long endT = 1467400000;
        System.out.println("startT: " + String.valueOf(Long.MAX_VALUE - startT));
        System.out.println("endT: " +  String.valueOf(Long.MAX_VALUE - endT));

        Random dmlRandom = new Random();
        for (int i = 0; i < 10; i++) {
            short k = (short) dmlRandom.nextInt(3);
            System.out.println(i + " : " + k);
        }
    }
    @Test
    public void testSalteHash() {

        HBaseQuery client = new HBaseQuery();
        String HBASE_TABLE = "rowkey_range_test";
        String schema = "retail_gms::auto_close_account_src";
        short dmlType = 3;
        long startTS = 1887324366;
        long endTS = 1267400000;
        // Hash prefix with 64 buckets
        AbstractRowKeyDistributor keyDistributor =
                new RowKeyDistributorByHashPrefix(
                        new RowKeyDistributorByHashPrefix.OneByteSimpleHash(64));
        ThlRowKeyGenerator rowKeyGenerator = ThlRowKeyGenerator.getInstance();
        byte[] oriRowKey = new byte[0];
        try {
            oriRowKey = rowKeyGenerator.generateRowKey(startTS, schema, dmlType);
        } catch (NoSuchAlgorithmException e) {

        }
        byte[] bucketRowKey = keyDistributor.getDistributedKey(oriRowKey);
//        System.out.println("oriRowKey: " + Bytes.toStringBinary(oriRowKey));
//        System.out.println("bucketRowKey: " +
//                Bytes.toStringBinary(keyDistributor.getDistributedKey(bucketRowKey)));
        byte[][] allBucketRowKey = keyDistributor.getAllDistributedKeys(oriRowKey);
        for (int i = 0; i < 156; i++) {
//            System.out.println(" i: " + i + Bytes.toStringBinary(allBucketRowKey[i]));
            System.out.println("i: " + i + ", hashPrefix: " + Bytes.toStringBinary(keyDistributor.getDistributedKey(Bytes.toBytes(i))));
        }

//        System.out.print("lowerPrefix bianryString: " + Bytes.toStringBinary(lowerPrefix));
//        System.out.print("upperPrefix bianryString: " + Bytes.toStringBinary(upperPrefix));
        byte[] lowerPrefix = new byte[] { 0x0 };
        byte[] upperPrefix = new byte[] { 0x7F };
        System.out.println("upperByteString: " + Bytes.toStringBinary(lowerPrefix));
        System.out.println("upperByteString: " + Bytes.toStringBinary(upperPrefix));
    }
    @Test
    public void testReverseTS () {
        long startTS = 1299999999;
        long endTS = 1677324366;

        System.out.println("startTS: " + TimeUtils.reverseTimeMillis(endTS));
        System.out.println("endTS: " + TimeUtils.reverseTimeMillis(startTS));

        Map<Integer, String> map = new HashMap<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
             //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
             //entry.getKey() ;entry.getValue(); entry.setValue();
             //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
             System.out.println("key= " + entry.getKey() + " and value= "
                             + entry.getValue());
        }

    }

    @Test
    public void testComputeHashByte() {
        byte[] byteStr = new byte[] { 0x7F };
        int originalKey = hashBytes(byteStr);
        long hash = Math.abs(originalKey);
        byte[] prefixByte = {(byte) (hash % 64)};
        System.out.println(Bytes.toStringBinary(prefixByte));

        byte[] prefixByte2 = {(byte) 125};
        System.out.println(Bytes.toStringBinary(prefixByte2));
    }
    /** Compute hash for binary data. */
    private static int hashBytes(byte[] bytes) {
        int hash = 1;
        for (int i = 0; i < bytes.length; i++)
            hash = (31 * hash) + (int) bytes[i];
        return hash;
    }
}
