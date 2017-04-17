package query;

import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.IOException;

public  class CustomerProfileFormatter {

  public static String format(byte[] bytes) throws IOException {

    return deserializeFromAvro(bytes).toString();

  }

  public static CustomerProfile deserializeFromAvro(byte[] bytes) throws IOException {

    SpecificDatumReader<CustomerProfile> reader = new SpecificDatumReader<CustomerProfile>(CustomerProfile.getClassSchema());
    Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
    CustomerProfile customerProfile = reader.read(null, decoder);

    return customerProfile;
  }

}
