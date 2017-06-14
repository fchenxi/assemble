package cn.panda.hbase.service;

import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ThlRowKeyGenerator {
	private static final Logger logger = LoggerFactory.getLogger(ThlRowKeyGenerator.class);
	private final String HASH_ALGORITHM_MD5 = "MD5";
//	private final String rowKeySplit = "::";
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
	 * 
	 * @param timeStamp
	 * @param schemaName
	 * @param tableName
	 * @param dmlType
	 * @param nodeIndex
	 * @param seqNo
	 * @param fragNo
	 * @param fragSeqNo
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] generateRowKey(long timeStamp, String schemaName, String tableName, short dmlType, short nodeIndex,
			long seqNo, short fragNo, short fragSeqNo) throws NoSuchAlgorithmException {
		StringBuilder str = new StringBuilder("rowKey值为:");
		str.append("schemaName=").append(schemaName).append("&tableName=").append(tableName).append("&dmlType=").append(dmlType);
		str.append("&timeStamp=").append(timeStamp).append("&nodeIndex=").append(nodeIndex).append("&seqNo=").append(seqNo);
		str.append("&fragNo").append(fragNo);
		logger.info(str.toString());
		String dbObjectName = tableName + StringConstants.ROW_KEY_SEPARATOR + schemaName;
		byte[] reverseTS = Bytes.toBytes(Long.MAX_VALUE - timeStamp);
		byte[] keyPrefix = Bytes.add(Bytes.toBytes(dmlType), md5Digest.digest(Bytes.toBytes(dbObjectName)), reverseTS);
		byte[] keySuffix = Bytes.add(Bytes.toBytes(nodeIndex), Bytes.toBytes(seqNo), Bytes.toBytes(fragNo));
		keySuffix = Bytes.add(keySuffix, Bytes.toBytes(fragSeqNo));
		return Bytes.add(keyPrefix, keySuffix);
	}

//	public String getRowKeySplit() {
//		return rowKeySplit;
//	}
}
