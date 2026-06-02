package com.example.attendance.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入结果封装
 * 记录成功条数、失败条数以及每行的错误信息
 */
public class ImportResult {

    private int successCount;       // 成功导入条数
    private int failCount;          // 失败条数
    private List<String> errors;    // 详细错误信息列表

    public ImportResult() {
        this.successCount = 0;
        this.failCount = 0;
        this.errors = new ArrayList<>();
    }

    /**
     * 记录一条成功
     */
    public void addSuccess() {
        this.successCount++;
    }

    /**
     * 记录一条失败，附带错误消息
     */
    public void addFail(String errorMsg) {
        this.failCount++;
        this.errors.add(errorMsg);
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getTotal() {
        return successCount + failCount;
    }
}
