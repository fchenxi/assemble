package cn.panda.lease;

import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.classification.InterfaceAudience;
import org.apache.hadoop.hbase.classification.InterfaceStability;

/**
 * Reports a problem with a lease
 */
@InterfaceAudience.Public
@InterfaceStability.Evolving
public class LeaseException extends DoNotRetryIOException {

  private static final long serialVersionUID = 8179703995292418650L;

  /** default constructor */
  public LeaseException() {
    super();
  }

  /**
   * @param message
   */
  public LeaseException(String message) {
    super(message);
  }
}
