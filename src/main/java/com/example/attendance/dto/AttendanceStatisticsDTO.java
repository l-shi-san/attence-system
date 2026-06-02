package com.example.attendance.dto;

import java.time.LocalDate;

/**
 * 考勤统计结果 DTO
 * 用于按时间范围统计考勤数据
 */
public class AttendanceStatisticsDTO {

    private Long totalCount;        // 总考勤次数
    private Long normalCount;       // 正常次数
    private Long lateCount;         // 迟到次数
    private Long absentCount;       // 缺勤次数
    private Long earlyCount;        // 早退次数
    private Double attendanceRate;  // 出勤率（百分比，保留两位小数）

    // 分组统计字段
    private String groupKey;        // 统计分组键（如 "2026-W22" 表示第22周，"2026-06" 表示6月）
    private String groupLabel;      // 分组标签（如 "第22周", "2026年6月"）

    public AttendanceStatisticsDTO() {
    }

    public AttendanceStatisticsDTO(Long totalCount, Long normalCount, Long lateCount,
                                   Long absentCount, Long earlyCount) {
        this.totalCount = totalCount;
        this.normalCount = normalCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.earlyCount = earlyCount;
        // 自动计算：出勤率 = (正常 + 迟到) / 总次数 * 100
        // 迟到也算出勤，但标记为不良出勤
        if (totalCount != null && totalCount > 0) {
            long attended = (normalCount != null ? normalCount : 0)
                          + (lateCount != null ? lateCount : 0);
            this.attendanceRate = Math.round(attended * 10000.0 / totalCount) / 100.0;
        } else {
            this.attendanceRate = 0.0;
        }
    }

    // ===== Getters & Setters =====

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Long normalCount) {
        this.normalCount = normalCount;
    }

    public Long getLateCount() {
        return lateCount;
    }

    public void setLateCount(Long lateCount) {
        this.lateCount = lateCount;
    }

    public Long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Long absentCount) {
        this.absentCount = absentCount;
    }

    public Long getEarlyCount() {
        return earlyCount;
    }

    public void setEarlyCount(Long earlyCount) {
        this.earlyCount = earlyCount;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }
}
