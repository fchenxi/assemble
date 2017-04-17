package cn.panda.lease;

import cn.panda.utils.HasThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Leases extends HasThread {
    private static final Log LOG = LogFactory.getLog(Leases.class.getName());
    private final Map<String, Lease> leases = new ConcurrentHashMap<String, Lease>();

    public Leases() {
    }

    @Override
    public void run() {
        //long toWait = leaseCheckFrequency;
        Lease nextLease = null;
        long nextLeaseDelay = Long.MAX_VALUE;
        nextLease = null;
        nextLeaseDelay = Long.MAX_VALUE;
        for (Iterator<Map.Entry<String, Lease>> it = leases.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Lease> entry = it.next();
            Lease lease = entry.getValue();
            long thisLeaseDelay = lease.getDelay(TimeUnit.MILLISECONDS);
            if ( thisLeaseDelay > 0) {
                if (nextLease == null || thisLeaseDelay < nextLeaseDelay) {
                    nextLease = lease;
                    nextLeaseDelay = thisLeaseDelay;
                }
            } else {
                // A lease expired.  Run the expired code before removing from map
                // since its presence in map is used to see if lease exists still.
                if (lease.getListener() == null) {
                    LOG.error("lease listener is null for lease " + lease.getLeaseName());
                } else {
                    lease.getListener().leaseExpired();
                    LOG.error("lease " + lease.getLeaseName() + " expired: " + lease.expirationTime + " current: " + EnvironmentEdgeManager.currentTime() );
                }
                it.remove();
            }
        }
    }
    /**
     * Create a lease and insert it to the map of leases.
     *
     * @param leaseName name of the lease
     * @param leaseTimeoutPeriod length of the lease in milliseconds
     * @param listener listener that will process lease expirations
     * @throws LeaseStillHeldException
     */
    public void createLease(String leaseName, int leaseTimeoutPeriod, final LeaseListener listener)
            throws LeaseStillHeldException {
        addLease(new Lease(leaseName, leaseTimeoutPeriod, listener));
    }

    /**
     * Inserts lease.  Resets expiration before insertion.
     * @param lease
     * @throws LeaseStillHeldException
     */
    public void addLease(final Lease lease) throws LeaseStillHeldException {
//        if (this.stopRequested) {
//            return;
//        }
        if (leases.containsKey(lease.getLeaseName())) {
            throw new LeaseStillHeldException(lease.getLeaseName());
        }
        lease.resetExpirationTime();
        leases.put(lease.getLeaseName(), lease);
    }

    /**
     * Renew a lease
     *
     * @param leaseName name of lease
     * @throws LeaseException
     */
    public void renewLease(final String leaseName) throws LeaseException {
//        if (this.stopRequested) {
//            return;
//        }
//        Lease lease = leases.get(leaseName);
//
//        if (lease == null ) {
//            throw new LeaseException("lease '" + leaseName +
//                    "' does not exist or has already expired");
//        }
//        lease.resetExpirationTime();
    }

    /**
     * Client explicitly cancels a lease.
     * @param leaseName name of lease
     * @throws org.apache.hadoop.hbase.regionserver.LeaseException
     */
    public void cancelLease(final String leaseName) throws LeaseException {
        removeLease(leaseName);
    }

    /**
     * Remove named lease.
     * Lease is removed from the map of leases.
     * Lease can be reinserted using {@link #addLease(Lease)}
     *
     * @param leaseName name of lease
     * @throws org.apache.hadoop.hbase.regionserver.LeaseException
     * @return Removed lease
     */
    Lease removeLease(final String leaseName) throws LeaseException {
        Lease lease = leases.remove(leaseName);
        if (lease == null) {
            throw new LeaseException("lease '" + leaseName + "' does not exist");
        }
        return lease;
    }
    /**
     * Thrown if we are asked to create a lease but lease on passed name already
     * exists.
     */
    @SuppressWarnings("serial")
    public static class LeaseStillHeldException extends IOException {
        private final String leaseName;

        /**
         * @param name
         */
        public LeaseStillHeldException(final String name) {
            this.leaseName = name;
        }

        /** @return name of lease */
        public String getName() {
            return this.leaseName;
        }
    }
    static class Lease implements Delayed {
        private final String leaseName;
        private final LeaseListener listener;
        private int leaseTimeoutPeriod;
        private long expirationTime;

        public Lease(final String leaseName, int leaseTimeoutPeriod, LeaseListener listener) {
            this.leaseName = leaseName;
            this.listener = listener;
            this.leaseTimeoutPeriod = leaseTimeoutPeriod;
            this.expirationTime = 0;
        }
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expirationTime - EnvironmentEdgeManager.currentTime(),
                    TimeUnit.MILLISECONDS);
        }
        public LeaseListener getListener() {
            return listener;
        }

        public String getLeaseName() {
            return leaseName;
        }
        public int compareTo(Delayed o) {
            long delta = this.getDelay(TimeUnit.MILLISECONDS) -
                    o.getDelay(TimeUnit.MILLISECONDS);

            return this.equals(o) ? 0 : (delta > 0 ? 1 : -1);
        }
        /**
         * Resets the expiration time of the lease.
         */
        public void resetExpirationTime() {
            this.expirationTime = EnvironmentEdgeManager.currentTime() + this.leaseTimeoutPeriod;
        }
    }
}
