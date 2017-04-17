package cn.panda.hbase.service;

import query.CustomerProfile;
import query.ProductPurchase;
import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GenerateTestData {

  public static void main(String args[]) throws IOException {

    WriteToHBase();

  }

  public static void WriteToHBase() throws IOException {

    Configuration config = HBaseConfiguration.create();

    config.set("hbase.master","127.0.0.1:60000");
    config.set("hbase.security.authentication", "simple");

    Connection connection = ConnectionFactory.createConnection(config);

    Table table = null;
    try {
      // establish the connection to the cluster.
      connection = ConnectionFactory.createConnection();
      // retrieve a handle to the target table.
      table = connection.getTable(TableName.valueOf("profiles"));


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

      Schema schema = new Schema.Parser().parse(new File(
              "F:\\我的空间\\phoenix-api\\trunk\\phoenix-api\\panda-server\\src\\main\\java\\avro\\customerprofile.avsc"));

      int id = 0;

      for(CustomerProfile user : users) {

        byte[] userBytes = Util.getSerializedMessage(user, schema);
        System.out.println("putting data for user");
        Put put = new Put(Bytes.toBytes("user-" + ++id));
        put.addColumn(Bytes.toBytes("profile"), Bytes.toBytes("data"), userBytes);
        table.put(put);
      }


    } finally {
      // close everything down
      if (table != null) table.close();
      if (connection != null) connection.close();
    }
  }



}
