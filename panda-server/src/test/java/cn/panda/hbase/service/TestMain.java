package cn.panda.hbase.service;

import junit.framework.TestCase;

public class TestMain  extends TestCase {

	private HBaseQueryV2 client = null;
	
	@Override
	protected void setUp() throws Exception {
		client = new HBaseQueryV2();
		//清理hbase
//		client.truncateTable(HBaseQueryV2.TABLE);
	}

	//入口
	public void testMain() throws Exception{
		//1. 执行业务表的插入、更新、删除
		//执行sql到mysql数据库
//		SqlExecute.executeSql(client);
		//等待1S，为保证数据插入成功
//		Thread.currentThread().sleep(1000);
//		searchFromHbase(client,"retail_fas_db.inventory_financial_book.1.avsc");
		searchFromHbase(client, "conf/retail_pos.order_main.1.avsc");
		
		//2.执行test1表的插入、更新、删除，测试全字段
		
	}

	/**
	 * 查询hbase数据
	 * @throws Exception 
	 */
	public void searchFromHbase(HBaseQueryV2 client,String schema) throws Exception {

		//hbase 里存储数据的表名
		String HBASE_TABLE = "transaction_history_dev";
		//mysql数据库的库名
		String schemaName = "retail_fas_db1";
		//mysql数据库的表名
		String tableName = "gms_period_balance";
		short dmlType = 0;
		//	        long startTS = 1450889366;
		//	        long endTS = 1451963509;
		//	        long startTS = 1492593284;
		//	        long endTS = 1492593316;
		
		long startTS = 1392659100000L;
		long endTS = 1592659120000L;
		short nodeIndex = 0;

//		try {
			// Be careful,the rowkey timestamp need reversed.
			byte[] startRowKey = ThlRowKeyGenerator.getInstance().generateRowKey(endTS, schemaName, tableName,
					dmlType, nodeIndex,0L,Short.valueOf("0"),Short.valueOf("0"));
			byte[] endRowKey = ThlRowKeyGenerator.getInstance().generateRowKey(startTS, schemaName, tableName,
					dmlType, nodeIndex,0L,Short.valueOf("0"),Short.valueOf("0"));
			client.hbaseCrud(HBASE_TABLE, null, null, schema);
//		} catch (NoSuchAlgorithmException e) {
//
//		}

	}
	
	/**
	 * 执行sql
	 * @param main
	 * @throws Exception
	 */
//	private void executeSql(HBaseQueryV2 main) throws Exception {
////		String sqlFilePath = "F:/DC_PROJECT/dataCenter_workspace/dc-cdc-plugin-0.1.0/src/test/java/cn/wonhigh/dc/cdc/plugin/testarchi/archi/sql.txt";
//		String sqlFilePath = main.getClass().getResource("sql.txt").getPath();
//		List<String> sqlList = main.loadSqlFile(sqlFilePath);
//		MysqlUtil.excuteSql(SqlLoader.getMysqConnection(), sqlList);
//	}
	
//	private Connection getMysqConnection() throws Exception{
//		
//		String DB_DRIVER = "com.mysql.jdbc.Driver";
//		String dbUrl;
//		String dbUser = "tungsten";
//		String dbPassword = "123456";
//
////		String sql = "select * from inventory_financial_book limit 1";
//
//		//需要修改成对应  dbName
//		String dbName = "retail_fas_db1";
//		
//		Connection conn;
//		try {
//			Class.forName(DB_DRIVER);
//			dbUrl = "jdbc:mysql://172.17.210.229:3306/" + dbName;
//			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
//		} catch (Exception e) {
//			throw e;
//		}
//		return conn;
//	}
}
