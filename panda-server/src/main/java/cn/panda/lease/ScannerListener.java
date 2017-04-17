package cn.panda.lease;

import org.slf4j.Logger;

/**
 * Instantiated as a scanner lease. If the lease times out, the scanner is
 * closed
 */
class ScannerListener implements LeaseListener {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(ScannerListener.class);
    private final String scannerName;

    ScannerListener(final String n) {
        this.scannerName = n;
    }

    @Override
    public void leaseExpired() {
        System.err.println("Found the leaseExpired " + this.scannerName + " expired");
//      RegionScannerHolder rsh = scanners.remove(this.scannerName);
//      if (rsh != null) {
//        RegionScanner s = rsh.s;
//        LOG.info("Scanner " + this.scannerName + " lease expired on region "
//          + s.getRegionInfo().getRegionNameAsString());
//        try {
//          HRegion region = regionServer.getRegion(s.getRegionInfo().getRegionName());
//          if (region != null && region.getCoprocessorHost() != null) {
//            region.getCoprocessorHost().preScannerClose(s);
//          }
//
//          s.close();
//          if (region != null && region.getCoprocessorHost() != null) {
//            region.getCoprocessorHost().postScannerClose(s);
//          }
//        } catch (IOException e) {
//          LOG.error("Closing scanner for "
//            + s.getRegionInfo().getRegionNameAsString(), e);
//        }
//      } else {
//        LOG.warn("Scanner " + this.scannerName + " lease expired, but no related" +
//          " scanner found, hence no chance to close that related scanner!");
//      }
    }
}