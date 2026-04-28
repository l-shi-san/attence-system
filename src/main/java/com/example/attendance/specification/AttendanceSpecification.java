package com.example.attendance.specification;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSpecification {

    /**
     * 学生ID条件（模糊查询）
     */
    public static Specification<Attendance> hasStudentId(String studentId) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(studentId)) {
                return criteriaBuilder.conjunction(); // 无条件
            }
            return criteriaBuilder.like(root.get("studentId"), "%" + studentId + "%");
        };
    }

    /**
     * 学生姓名条件（模糊查询）
     */
    public static Specification<Attendance> hasStudentName(String studentName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(studentName)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("studentName"), "%" + studentName + "%");
        };
    }

    /**
     * 课程ID条件
     */
    public static Specification<Attendance> hasCourseId(String courseId) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(courseId)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("courseId"), courseId);
        };
    }

    /**
     * 考勤状态条件
     */
    public static Specification<Attendance> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return criteriaBuilder.conjunction();
            }
            try {
                AttendanceStatus attendanceStatus = AttendanceStatus.valueOf(status.toUpperCase());
                return criteriaBuilder.equal(root.get("status"), attendanceStatus);
            } catch (IllegalArgumentException e) {
                return criteriaBuilder.conjunction();
            }
        };
    }

    /**
     * 签到时间范围条件
     */
    public static Specification<Attendance> hasCheckInTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("checkInTime"), startTime));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("checkInTime"), endTime));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 座位行条件
     */
    public static Specification<Attendance> hasSeatRow(Integer seatRow) {
        return (root, query, criteriaBuilder) -> {
            if (seatRow == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("seatRow"), seatRow);
        };
    }

    /**
     * 动态组合查询（所有条件可选）
     */
    public static Specification<Attendance> dynamicQuery(String studentId,
                                                         String studentName,
                                                         String courseId,
                                                         String status,
                                                         LocalDateTime startTime,
                                                         LocalDateTime endTime,
                                                         Integer seatRow) {
        return Specification
                .where(hasStudentId(studentId))
                .and(hasStudentName(studentName))
                .and(hasCourseId(courseId))
                .and(hasStatus(status))
                .and(hasCheckInTimeBetween(startTime, endTime))
                .and(hasSeatRow(seatRow));
    }
}