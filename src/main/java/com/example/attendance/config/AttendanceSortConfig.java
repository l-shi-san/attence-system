package com.example.attendance.config;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class AttendanceSortConfig {

    // 排序字段常量
    public static final String SORT_BY_CHECK_IN_TIME = "checkInTime";
    public static final String SORT_BY_STUDENT_ID = "studentId";
    public static final String SORT_BY_STUDENT_NAME = "studentName";
    public static final String SORT_BY_CREATE_TIME = "createTime";

    /**
     * 创建排序对象
     * @param sortField 排序字段
     * @param sortDirection 排序方向 (asc/desc)
     * @return Sort对象
     */
    public static Sort createSort(String sortField, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(
                sortDirection == null ? "asc" : sortDirection.toLowerCase()
        );

        // 默认按签到时间排序
        String field = (sortField == null || sortField.isEmpty()) ? SORT_BY_CHECK_IN_TIME : sortField;

        return Sort.by(direction, field);
    }
}