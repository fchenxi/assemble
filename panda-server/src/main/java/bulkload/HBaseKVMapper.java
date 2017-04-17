package bulkload;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.mapred.AvroWrapper;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class HBaseKVMapper<IN> extends Mapper<AvroWrapper<IN>, NullWritable, ImmutableBytesWritable, KeyValue> {
    private final static byte[] COL_FAMILY = "cf".getBytes();

    private ImmutableBytesWritable hKey = new ImmutableBytesWritable();
    private KeyValue kv;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
    }

    @Override
    protected void map(AvroWrapper<IN> key, NullWritable value, Context context) throws IOException, InterruptedException {
        Record r = (Record)key.datum();
        ByteBuffer f1 = (ByteBuffer) r.get("f1");
        ByteBuffer f2 = (ByteBuffer) r.get("f2");
        hKey.set(f1.array()); // Use f1 as key
        kv = new KeyValue(hKey.get(), COL_FAMILY, HColumnEnum.COL_1.getColumnName(), f1.array());
        context.write(hKey, kv);
        kv = new KeyValue(hKey.get(), COL_FAMILY, HColumnEnum.COL_2.getColumnName(), f2.array());
        context.write(hKey, kv);
    }
}
