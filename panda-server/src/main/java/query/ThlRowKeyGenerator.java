package query;

import cn.panda.utils.TimeUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ThlRowKeyGenerator {

    private Logger logger = LoggerFactory.getLogger(ThlRowKeyGenerator.class);

    private final String HASH_ALGORITHM_MD5 = "MD5";
    private final String rowKeySplit = "::";
    private static MessageDigest md5Digest;
    private final static ThlRowKeyGenerator generator = new ThlRowKeyGenerator();
    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {

        }
    }

    private ThlRowKeyGenerator() {
    }

    public static ThlRowKeyGenerator getInstance() {
        return generator;
    }

    /**
     * Use for generate rowkey.
     *
     * @param timeStamp
     * @param schemaName
     * @param tableName
     * @param dmlType
     * @param nodeIndex
     * @param seqNo
     * @param fragNo
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] generateRowKey(long timeStamp, String schemaName, String tableName,
                                 short dmlType, short nodeIndex, long seqNo, short fragNo)
            throws NoSuchAlgorithmException {
        String table = schemaName + rowKeySplit + tableName;
        byte[] reverseTS = Bytes.toBytes(TimeUtils.reverseTimeMillis(timeStamp));
//        byte[] keySuffix = Bytes.add(dml, node, seq);
//        return Bytes.add(reverseTS, md5Digest.digest(Bytes.toBytes(table)), keySuffix);
        byte[] keyPrefix = Bytes.add(Bytes.toBytes(dmlType), md5Digest.digest(Bytes.toBytes(table)), reverseTS);
        byte[] keySuffix = Bytes.add(Bytes.toBytes(nodeIndex), Bytes.toBytes(seqNo), Bytes.toBytes(fragNo));
        return Bytes.add(keyPrefix, keySuffix);
//        System.out.println("dmlType: " + Bytes.toStringBinary(Bytes.toBytes(dmlType)));
//        System.out.println("tableMD5Bytes: " + Bytes.toStringBinary(md5Digest.digest(Bytes.toBytes(table))));
//        System.out.println("table: " + table);
//        System.out.println("reverseTs: " + Bytes.toLong(reverseTS));
//        System.out.println("rowkey: " + Bytes.toStringBinary(keyPrefix));
//        System.out.println();
//        return keyPrefix;
    }

    /**
     * Test for batch region distribution.
     *
     * @param timeStamp
     * @param schemaName
     * @param dmlType
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] generateRowKey(long timeStamp, String schemaName, short dmlType)
            throws NoSuchAlgorithmException {

        byte[] reverseTS = Bytes.toBytes(TimeUtils.reverseTimeMillis(timeStamp));
        byte[] keySuffix = Bytes.add(Bytes.toBytes(dmlType),
                md5Digest.digest(Bytes.toBytes(schemaName)), reverseTS);
        logger.info("dmlType: " + Bytes.toStringBinary(Bytes.toBytes(dmlType)));
        logger.info("tableMD5Bytes: " + Bytes.toStringBinary(md5Digest.digest(Bytes.toBytes(schemaName))));
        logger.info("table: " + schemaName);
        logger.info("reverseTs: " + Bytes.toLong(reverseTS));
        logger.info("rowkey: " + Bytes.toStringBinary(keySuffix));
//        return keyPrefix;
        return Bytes.add(md5Digest.digest(keySuffix), keySuffix);
    }
    public String getRowKeySplit() {
        return rowKeySplit;
    }
    private int getSalt(long timeStamp, int regions) {
        return new Integer(new Long(timeStamp).hashCode()).shortValue() % regions;
    }

}
