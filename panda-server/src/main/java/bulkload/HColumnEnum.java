package bulkload;

/**
 * HBase table columns for the 'cf' column family
 */
public enum HColumnEnum {
    COL_1 ("col_1".getBytes()),
    COL_2 ("col_2".getBytes());

    private final byte[] columnName;

    HColumnEnum (byte[] column) {
        this.columnName = column;
    }

    public byte[] getColumnName() {
        return this.columnName;
    }
}
