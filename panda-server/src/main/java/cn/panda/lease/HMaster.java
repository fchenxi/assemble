package cn.panda.lease;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.logging.Logger;

public class HMaster<K, V> {

    private static final Logger LOG = Logger.getLogger(HMaster.class.getName());

    private ConcurrentMap<K, V> cacheObjMap = new ConcurrentHashMap<K, V>();

    private DelayQueue<Leases.Lease> delayQueue = new DelayQueue<Leases.Lease>();

    private LeaseListener leaseListener = new ScannerListener("scan");

    public static void main(String[] args) throws Exception {
        HMaster<Integer, String> cache = new HMaster<Integer, String>();
        Leases leases = new Leases();

        leases.createLease("scan_-1", -1000000,
                new ScannerListener("scan_-1000000"));
        leases.createLease("scan_-2", -2000000,
                new ScannerListener("scan_-2000000"));
        leases.createLease("scan_2", -3000000,
                new ScannerListener("scan_-3000000"));
        leases.createLease("scan_3", -30000,
                new ScannerListener("scan_-30000"));
        leases.createLease("scan_4", -4000,
                new ScannerListener("scan_-4000"));
        leases.createLease("scan_5", 5000000,
                new ScannerListener("scan_5000000"));
        leases.start();
    }
}