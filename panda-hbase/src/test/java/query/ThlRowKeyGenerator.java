package query;

import org.apache.hadoop.hbase.util.Bytes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ThlRowKeyGenerator {
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

    //TODO Be cautious,this is only for test query.
    public byte[] generateRowKey(long timeStamp, String schemaName, String tableName, short dmlType,
                                 short nodeIndex, long seqNo, short fragNo)
            throws NoSuchAlgorithmException {

        String table = tableName + rowKeySplit + schemaName;
        byte[] reverseTS = Bytes.toBytes(Long.MAX_VALUE - timeStamp);
//        byte[] reverseTS = Bytes.toBytes(timeStamp);
        //        byte[] keySuffix = Bytes.add(dml, node, seq);
        //        return Bytes.add(reverseTS, md5Digest.digest(Bytes.toBytes(table)), keySuffix);
        byte[] keyPrefix = Bytes.add(Bytes.toBytes(dmlType), md5Digest.digest(Bytes.toBytes
                (table)), reverseTS);
        byte[] keySuffix = Bytes.add(Bytes.toBytes(nodeIndex), Bytes.toBytes(seqNo), Bytes
                .toBytes(fragNo));
        System.out.println("rowkey String: " + table);
        System.out.println("timeStamp: " + timeStamp);
        return Bytes.add(keyPrefix, keySuffix);
    }

    public String getRowKeySplit() {
        return rowKeySplit;
    }
}
