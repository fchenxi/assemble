package cn.panda.model;

import java.io.Serializable;

/**
 * 分页工具类,修复PageSize Bug
 *
 * @author      tong.cx
 * @version     0.0.1
 * @datetime    2016/4/7 18:36
 * @copyright   wonhigh.cn
 */
public class Pager implements Serializable {
    private static final long serialVersionUID = -6283758828350113183L;
    protected int totalCount = 0;
    protected int pageSize = 20;
    protected int pageNo = 1;
    private long startRowNum;
    private long endRowNum;
    public static final int DEF_COUNT = 20;

    public Pager() {
    }

    public Pager(int pageNo, int pageSize, int totalCount) {
        if (totalCount <= 0) {
            this.totalCount = 0;
        } else {
            this.totalCount = totalCount;
        }

        if (pageSize <= 0) {
            this.pageSize = 20;
        } else {
            this.pageSize = pageSize;
        }
        if (pageNo * pageSize > totalCount) {
            this.pageSize = totalCount - (totalCount / pageSize) * pageSize;
        }
        if (pageNo <= 0) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }
        if ((this.pageNo - 1) * this.pageSize >= totalCount && totalCount > 0) {
            this.pageNo = totalCount / this.pageSize;
        }

    }

    public void adjustPage() {
        if (this.totalCount <= 0) {
            this.totalCount = 0;
        }

        if (this.pageSize <= 0) {
            this.pageSize = 20;
        }

        if (this.pageNo <= 0) {
            this.pageNo = 1;
        }

        if ((this.pageNo - 1) * this.pageSize >= this.totalCount) {
            this.pageNo = this.totalCount / this.pageSize;
        }

    }

    public int getPageNo() {
        return this.pageNo;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getTotalPage() {
        int totalPage = this.totalCount / this.pageSize;
        if (this.totalCount % this.pageSize != 0 || totalPage == 0) {
            ++totalPage;
        }

        return totalPage;
    }

    public boolean isFirstPage() {
        return this.pageNo <= 1;
    }

    public boolean isLastPage() {
        return this.pageNo >= this.getTotalPage();
    }

    public int getNextPage() {
        return this.isLastPage() ? this.pageNo : this.pageNo + 1;
    }

    public int getPrePage() {
        return this.isFirstPage() ? this.pageNo : this.pageNo - 1;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getStartRowNum() {
        return this.isFirstPage() ? 0L : (long) ((this.pageNo - 1) * this.pageSize);
    }

    public void setStartRowNum(long startRowNum) {
        this.startRowNum = startRowNum;
    }

    public long getEndRowNum() {
        return this.getStartRowNum() + (long) this.pageSize + 1L;
    }

    public void setEndRowNum(long endRowNum) {
        this.endRowNum = endRowNum;
    }
}
