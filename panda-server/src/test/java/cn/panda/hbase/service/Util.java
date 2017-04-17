package cn.panda.hbase.service;

import query.CustomerProfile;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Util {

  public static byte[] getSerializedMessage(CustomerProfile user, Schema schema) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
    DatumWriter<CustomerProfile> writer = new SpecificDatumWriter<CustomerProfile>(CustomerProfile.getClassSchema());

    writer.write(user, encoder);
    encoder.flush();
    out.close();

    return out.toByteArray();
  }

}
