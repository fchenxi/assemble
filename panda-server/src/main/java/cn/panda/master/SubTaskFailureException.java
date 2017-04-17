package cn.panda.master;

public class SubTaskFailureException extends Exception {

    /**
     * the retry info of subtask.
     */
    @SuppressWarnings("rawtypes")
    public final RetryInfo retryInfo;
    public SubTaskFailureException(RetryInfo retryInfo, Exception cause){
        super(cause);
        this.retryInfo = retryInfo;
    }
}
