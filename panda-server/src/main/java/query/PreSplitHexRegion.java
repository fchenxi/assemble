package query;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;

public class PreSplitHexRegion {

    private static Logger logger = LoggerFactory.getLogger(PreSplitHexRegion.class);

    public static boolean createTable(HBaseAdmin admin, HTableDescriptor table, byte[][] splits)
            throws IOException {
        try {
            admin.createTable( table, splits );
            return true;
        } catch (TableExistsException e) {
            logger.info("table " + table.getNameAsString() + " already exists");
            // the table already exists...
            return false;
        }
    }

    public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
        byte[][] splits = new byte[numRegions-1][];
        BigInteger lowestKey = new BigInteger(startKey, 16);
        BigInteger highestKey = new BigInteger(endKey, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
        lowestKey = lowestKey.add(regionIncrement);
        for (int i=0; i < numRegions-1;i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }
    public static void main(String[] args) {
        byte[][] splitRegions = getHexSplits("61", "63", 2);
        for (byte[] region : splitRegions) {
            System.out.println("region: " + Bytes.toStringBinary(region));
        }
        System.out.println(Bytes.toStringBinary(Bytes.toBytes("?")));
    }
}
