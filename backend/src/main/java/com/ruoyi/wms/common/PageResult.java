package com.ruoyi.wms.common;

import java.util.List;

/**
 * 分页响应对象
 */
public class PageResult<T> {
    private long total; // 总条数
    private List<T> records; // 数据列表
    private int pageNum; // 当前页码
    private int pageSize; // 每页条数
    private int pages; // 总页数
    
    public PageResult() {
    }
    
    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
        this.pageNum = 1;
        this.pageSize = records.size();
        this.pages = records.isEmpty() ? 0 : 1;
    }
    
    public PageResult(long total, List<T> records, int pageNum, int pageSize) {
        this.total = total;
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
        this.pages = (int) Math.ceil((double) total / this.pageSize);
    }
    
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) this.total / pageSize);
    }
    
    public int getPages() {
        return pages;
    }
    
    public void setPages(int pages) {
        this.pages = pages;
    }
}