package cn.panda.hbase.service;

import query.CustomerProfile;
import query.CustomerProfileFormatter;
import query.ProductPurchase;
import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class TestCustomerProfileFormatter {

  @Test
  public void createLocalHBaseTestData() throws Exception {

    startHBaseMiniCluster();

    Configuration config = HBaseConfiguration.create();

    Connection connection = cluster.getConnection();

    cluster.createTable(Bytes.toBytes("customer_profiles"), Bytes.toBytes("profiles"));

    Admin admin = connection.getAdmin();
    TableName tableName = TableName.valueOf("customer_profiles");

    if (!admin.isTableAvailable(tableName)) {
      HTableDescriptor desc = new HTableDescriptor(tableName);
      HColumnDescriptor coldef = new HColumnDescriptor(
          Bytes.toBytes("profiles"));
      desc.addFamily(coldef);
      admin.createTable(desc);
    }

    Table userTable = connection.getTable(tableName);

    ArrayList<CustomerProfile> users =  new ArrayList<CustomerProfile>();

    CustomerProfile user1 = new CustomerProfile();
    user1.setName("Georgina");
    user1.setCustomerType("Standard");
    user1.setEstimatedLifetimeValue(10000);
    user1.setNextBestAction("email");

    ProductPurchase purchase1 = new ProductPurchase();
    purchase1.setCode("1234");
    purchase1.setItem("Book");
    purchase1.setQuantity(1);
    purchase1.setEventDate("20150101");

    List<ProductPurchase> user1PurchaseHistory = new ArrayList<ProductPurchase>();

    user1PurchaseHistory.add(purchase1);

    user1.setPurchaseHistory(user1PurchaseHistory);

    users.add(user1);

    CustomerProfile user2 = new CustomerProfile();
    user2.setName("Harriet");
    user2.setCustomerType("Standard");
    user2.setEstimatedLifetimeValue(10000);
    user2.setNextBestAction("email");

    ProductPurchase purchase2 = new ProductPurchase();
    purchase2.setCode("1234");
    purchase2.setItem("Book");
    purchase2.setQuantity(1);
    purchase2.setEventDate("20150101");

    List<ProductPurchase> user2PurchaseHistory = new ArrayList<ProductPurchase>();

    user2PurchaseHistory.add(purchase2);

    user2.setPurchaseHistory(user2PurchaseHistory);

    users.add(user2);

    CustomerProfile user3 = new CustomerProfile();
    user3.setName("William");
    user3.setCustomerType("Standard");
    user3.setEstimatedLifetimeValue(10000);
    user3.setNextBestAction("email");

    ProductPurchase purchase3 = new ProductPurchase();
    purchase3.setCode("1234");
    purchase3.setItem("Book");
    purchase3.setQuantity(1);
    purchase3.setEventDate("20150101");

    List<ProductPurchase> user3PurchaseHistory = new ArrayList<ProductPurchase>();

    user3PurchaseHistory.add(purchase3);

    user3.setPurchaseHistory(user3PurchaseHistory);

    users.add(user3);


    Schema schema = new Schema.Parser().parse(new File("src/main/avro/customerprofile.avsc"));

    int id = 0;

    for(CustomerProfile user : users) {

      byte[] userBytes = getSerializedMessage(user, schema);

      Put put = new Put(Bytes.toBytes("user-" + ++id));
      put.addColumn(Bytes.toBytes("profiles"), Bytes.toBytes("profile"), userBytes);
      userTable.put(put);
    }

    connection.close();

    stopHBaseMiniCluster();
  }

  @Test
  public void TestFormat() throws IOException {

    CustomerProfile user = new CustomerProfile();
    user.setName("Rob");
    user.setCustomerType("Gold");
    user.setEstimatedLifetimeValue(100000);
    user.setLoyaltyCardNumber("123456");

    ProductPurchase purchase = new ProductPurchase();
    purchase.setCode("1234");
    purchase.setItem("Book");
    purchase.setQuantity(1);
    purchase.setEventDate("20150101");

    List<ProductPurchase> purchaseHistory = new ArrayList<ProductPurchase>();

    purchaseHistory.add(purchase);

    user.setPurchaseHistory(purchaseHistory);

    Schema schema = new Schema.Parser().parse(new File("src/main/avro/customerprofile.avsc"));

    byte[] userBytes = getSerializedMessage(user, schema);

    String format = CustomerProfileFormatter.format(userBytes);

    System.out.println(format);

    assert(format.equals("{\"name\": \"Rob\", \"next_best_action\": null, \"loyalty_card_number\": \"123456\", \"customer_type\": \"Gold\", \"estimated_lifetime_value\": 100000, \"purchase_history\": [{\"item\": \"Book\", \"code\": \"1234\", \"quantity\": 1, \"event_date\": \"20150101\"}]}"));

  }

  private byte[] getSerializedMessage(CustomerProfile user,Schema schema) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
    DatumWriter<CustomerProfile> writer = new SpecificDatumWriter<CustomerProfile>(CustomerProfile.getClassSchema());

    writer.write(user, encoder);
    encoder.flush();
    out.close();

    return out.toByteArray();
  }

  private void startHBaseMiniCluster() throws Exception {
      cluster.startMiniCluster();
  }

  private void stopHBaseMiniCluster() throws Exception {
      cluster.shutdownMiniHBaseCluster();


  }

  private HBaseTestingUtility cluster = new HBaseTestingUtility();



}
