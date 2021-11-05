package com.ew.autofly.entity.mission;

import java.util.List;


public class PatrolSubJobPage {

    private int total;
    private int nowPage;
    private int pages;
    private List<PatrolSubJob> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<PatrolSubJob> getRows() {
        return rows;
    }

    public void setRows(List<PatrolSubJob> rows) {
        this.rows = rows;
    }
}
