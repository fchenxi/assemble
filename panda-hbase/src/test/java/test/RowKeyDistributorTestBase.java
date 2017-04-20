/**
 * Copyright 2010 Sematext International
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;

import cn.panda.common.hbase.distributor.AbstractRowKeyDistributor;
import cn.panda.common.hbase.distributor.DistributedScanner;
import cn.panda.common.hbase.distributor.WdTableInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Provides basic tests for row key distributor
 *
 * @author Alex Baranau
 */
public abstract class RowKeyDistributorTestBase {
  protected static final String TABLE_NAME = "table";
  protected static final byte[] TABLE = Bytes.toBytes(TABLE_NAME);
  protected static final byte[] CF = Bytes.toBytes("colfam");
  protected static final byte[] QUAL = Bytes.toBytes("qual");
  private final AbstractRowKeyDistributor keyDistributor;
  private HBaseTestingUtility testingUtility;
  private HTable hTable;

  public RowKeyDistributorTestBase(AbstractRowKeyDistributor keyDistributor) {
    this.keyDistributor = keyDistributor;
  }

  @Before
  public void before() throws Exception {
    testingUtility = new HBaseTestingUtility();
    testingUtility.startMiniZKCluster();
    testingUtility.startMiniCluster(1);
    hTable = testingUtility.createTable(TABLE, CF);
  }

  @After
  public void after() throws Exception {
    hTable = null;
    testingUtility.shutdownMiniCluster();
    testingUtility.shutdownMiniZKCluster();
    testingUtility = null;
  }

  /** Testing simple get. */
  @Test
  public void testGet() throws IOException, InterruptedException {
    // Testing simple get
    byte[] key = new byte[] {123, 124, 122};
    byte[] distributedKey = keyDistributor.getDistributedKey(key);
    byte[] value = Bytes.toBytes("some");

    hTable.put(new Put(distributedKey).add(CF, QUAL, value));

    Result result = hTable.get(new Get(distributedKey));
    Assert.assertArrayEquals(key, keyDistributor.getOriginalKey(result.getRow()));
    Assert.assertArrayEquals(value, result.getValue(CF, QUAL));
  }

  /** Test scan with start and stop key. */
  @Test
  public void testSimpleScanBounded() throws IOException {
    long origKeyPrefix = System.currentTimeMillis();

    int seekIntervalMinValue = 100;
    int seekIntervalMaxValue = 899;
    byte[] startKey = Bytes.toBytes(origKeyPrefix + seekIntervalMinValue);
    byte[] stopKey = Bytes.toBytes(origKeyPrefix + seekIntervalMaxValue + 1);
    Scan scan = new Scan(startKey, stopKey);
    testSimpleScanInternal(origKeyPrefix, scan, 500, 500, seekIntervalMinValue, seekIntervalMaxValue);
  }

  /** Test scan over the whole table. */
  @Test
  public void testSimpleScanUnbounded() throws IOException {
    long origKeyPrefix = System.currentTimeMillis();
    testSimpleScanInternal(origKeyPrefix, new Scan(), 500, 500, 0, 999);
  }

  /** Test scan without stop key. */
  @Test
  public void testSimpleScanWithoutStopKey() throws IOException {
    long origKeyPrefix = System.currentTimeMillis();
    int seekIntervalMinValue = 100;
    byte[] startKey = Bytes.toBytes(origKeyPrefix + seekIntervalMinValue);
    testSimpleScanInternal(origKeyPrefix, new Scan(startKey), 500, 500, 100, 999);
  }

  /** Test scan with start and stop key. */
  @Test
  public void testMapReduceBounded() throws IOException, InterruptedException, ClassNotFoundException {
    long origKeyPrefix = System.currentTimeMillis();

    int seekIntervalMinValue = 100;
    int seekIntervalMaxValue = 899;
    byte[] startKey = Bytes.toBytes(origKeyPrefix + seekIntervalMinValue);
    byte[] stopKey = Bytes.toBytes(origKeyPrefix + seekIntervalMaxValue + 1);
    Scan scan = new Scan(startKey, stopKey);
    testMapReduceInternal(origKeyPrefix, scan, 500, 500, seekIntervalMinValue, seekIntervalMaxValue);
  }

  /** Test scan over the whole table. */
  @Test
  public void testMapReduceUnbounded() throws IOException, InterruptedException, ClassNotFoundException {
    long origKeyPrefix = System.currentTimeMillis();
    testMapReduceInternal(origKeyPrefix, new Scan(), 500, 500, 0, 999);
  }

  private int writeTestData(long origKeyPrefix, int numRows, int rowKeySeed,
                            int seekIntervalMinValue, int seekIntervalMaxValue) throws IOException {
    int valuesCountInSeekInterval = 0;
    for (int i = 0; i < numRows; i++) {
      int val = rowKeySeed + i - i * (i % 2) * 2; // i.e. 500, 499, 502, 497, 504, ...
      valuesCountInSeekInterval += (val >= seekIntervalMinValue && val <= seekIntervalMaxValue) ? 1 : 0;
      byte[] key = Bytes.toBytes(origKeyPrefix + val);
      byte[] distributedKey = keyDistributor.getDistributedKey(key);
      byte[] value = Bytes.toBytes(val);
      hTable.put(new Put(distributedKey).add(CF, QUAL, value));
    }
    return valuesCountInSeekInterval;
  }

  private void testSimpleScanInternal(long origKeyPrefix, Scan scan, int numValues, int startWithValue,
                                      int seekIntervalMinValue, int seekIntervalMaxValue) throws IOException {
    int valuesCountInSeekInterval =
            writeTestData(origKeyPrefix, numValues, startWithValue, seekIntervalMinValue, seekIntervalMaxValue);

    // TODO: add some filters to the scan for better testing
    ResultScanner distributedScanner = DistributedScanner.create(hTable, scan, keyDistributor);

    Result previous = null;
    int countMatched = 0;
    for (Result current : distributedScanner) {
      countMatched++;
      if (previous != null) {
        byte[] currentRowOrigKey = keyDistributor.getOriginalKey(current.getRow());
        byte[] previousRowOrigKey = keyDistributor.getOriginalKey(previous.getRow());
        Assert.assertTrue(Bytes.compareTo(currentRowOrigKey, previousRowOrigKey) >= 0);

        int currentValue = Bytes.toInt(current.getValue(CF, QUAL));
        Assert.assertTrue(currentValue >= seekIntervalMinValue);
        Assert.assertTrue(currentValue <= seekIntervalMaxValue);
      }
      previous = current;
    }

    Assert.assertEquals(valuesCountInSeekInterval, countMatched);
  }

  private void testMapReduceInternal(long origKeyPrefix, Scan scan, int numValues, int startWithValue,
                                     int seekIntervalMinValue, int seekIntervalMaxValue)
          throws IOException, InterruptedException, ClassNotFoundException {
    int valuesCountInSeekInterval =
            writeTestData(origKeyPrefix, numValues, startWithValue, seekIntervalMinValue, seekIntervalMaxValue);

    // Reading data
    Configuration conf = testingUtility.getConfiguration();
    Job job = new Job(conf, "testMapReduceInternal()-Job");
    job.setJarByClass(this.getClass());
    TableMapReduceUtil.initTableMapperJob(TABLE_NAME, scan,
            RowCounterMapper.class, ImmutableBytesWritable.class, Result.class, job);

    // Substituting standard TableInputFormat which was set in TableMapReduceUtil.initTableMapperJob(...)
    job.setInputFormatClass(WdTableInputFormat.class);
    keyDistributor.addInfo(job.getConfiguration());

    job.setOutputFormatClass(NullOutputFormat.class);
    job.setNumReduceTasks(0);

    boolean succeeded = job.waitForCompletion(true);
    Assert.assertTrue(succeeded);

    long mapInputRecords = job.getCounters().findCounter(RowCounterMapper.Counters.ROWS).getValue();
    Assert.assertEquals(valuesCountInSeekInterval, mapInputRecords);
  }

  /**
   * Mapper that runs the count.
   * NOTE: it was copied from RowCounter class
   */
  static class RowCounterMapper extends TableMapper<ImmutableBytesWritable, Result> {
    /** Counter enumeration to count the actual rows. */
    public static enum Counters {ROWS}

    @Override
    public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException {
      for (KeyValue value: values.list()) {
        if (value.getValue().length > 0) {
          context.getCounter(Counters.ROWS).increment(1);
          break;
        }
      }
    }
  }
}
