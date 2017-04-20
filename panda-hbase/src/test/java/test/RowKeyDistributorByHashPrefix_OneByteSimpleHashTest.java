package test;

import cn.panda.common.hbase.distributor.RowKeyDistributorByHashPrefix;

public class RowKeyDistributorByHashPrefix_OneByteSimpleHashTest extends RowKeyDistributorTestBase {
  public RowKeyDistributorByHashPrefix_OneByteSimpleHashTest() {
    super(new RowKeyDistributorByHashPrefix(new RowKeyDistributorByHashPrefix.OneByteSimpleHash(15)));
  }
}
