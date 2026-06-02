package com.example.attendance.service.impl;

import com.example.attendance.dto.ImportResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.service.AttendanceImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 考勤 Excel 导入服务实现
 *
 * Excel 期望列顺序（第一行为标题行，跳过）：
 *   A列: 学号     (studentNo)
 *   B列: 姓名     (studentName)
 *   C列: 课程名称  (courseName)
 *   D列: 打卡日期  (checkInDate, 格式 yyyy-MM-dd)
 *   E列: 打卡时间  (checkInTime, 格式 HH:mm:ss)
 *   F列: 状态     (status: NORMAL/LATE/ABSENT)
 *   G列: 备注     (remark, 可选)
 *   H列: IP地址   (ip, 可选)
 */
@Service
public class AttendanceImportServiceImpl implements AttendanceImportService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceImportServiceImpl.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    /** 每批保存的条数（防止大文件内存溢出） */
    private static final int BATCH_SIZE = 100;

    @Override
    public ImportResult importAttendance(MultipartFile file) {
        ImportResult result = new ImportResult();

        // 1. 基础校验
        String originalFilename = file.getOriginalFilename();
        if (file.isEmpty()) {
            result.addFail("文件为空，请上传有效的 Excel 文件");
            return result;
        }
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            result.addFail("文件格式错误，仅支持 .xlsx / .xls 格式，当前文件: " + originalFilename);
            return result;
        }

        // 2. 解析 Excel
        List<Attendance> attendanceList = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            Workbook workbook;
            if (originalFilename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                rowNum++;
                // 跳过标题行（第1行）
                if (row.getRowNum() == 0) {
                    continue;
                }

                try {
                    Attendance attendance = parseRow(row, rowNum);
                    if (attendance != null) {
                        attendance.setCreateTime(LocalDateTime.now());
                        attendanceList.add(attendance);
                        result.addSuccess();
                    }
                } catch (Exception e) {
                    log.warn("第{}行解析失败: {}", rowNum, e.getMessage());
                    result.addFail("第" + rowNum + "行解析失败: " + e.getMessage());
                }

                // 批量保存，防止 OOM
                if (attendanceList.size() >= BATCH_SIZE) {
                    attendanceRepository.saveAll(attendanceList);
                    attendanceList.clear();
                }
            }

            workbook.close();

            // 保存剩余批次
            if (!attendanceList.isEmpty()) {
                attendanceRepository.saveAll(attendanceList);
            }

            log.info("Excel 导入完成: 成功{}条, 失败{}条", result.getSuccessCount(), result.getFailCount());

        } catch (IOException e) {
            log.error("Excel 文件读取失败: {}", e.getMessage(), e);
            result.addFail("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Excel 解析异常: {}", e.getMessage(), e);
            result.addFail("Excel 解析异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 解析一行 Excel 数据为 Attendance 实体
     */
    private Attendance parseRow(Row row, int rowNum) {
        Attendance attendance = new Attendance();

        // A列: 学号（字符串，必填）
        String studentNo = getCellStringValue(row.getCell(0));
        if (studentNo == null || studentNo.isBlank()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        attendance.setStudentNo(studentNo.trim());

        // B列: 姓名（必填）
        String studentName = getCellStringValue(row.getCell(1));
        if (studentName == null || studentName.isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        attendance.setStudentName(studentName.trim());

        // C列: 课程名称（可选）
        String courseName = getCellStringValue(row.getCell(2));
        attendance.setCourseName(courseName != null ? courseName.trim() : "");

        // D列: 打卡日期（必填，格式 yyyy-MM-dd）
        String dateStr = getCellStringValue(row.getCell(3));
        if (dateStr == null || dateStr.isBlank()) {
            throw new IllegalArgumentException("打卡日期不能为空");
        }
        LocalDate checkInDate;
        try {
            checkInDate = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式错误（应为 yyyy-MM-dd）: " + dateStr);
        }

        // E列: 打卡时间（可选，格式 HH:mm:ss）
        LocalTime checkInTime = LocalTime.of(8, 0, 0); // 默认上午8点
        String timeStr = getCellStringValue(row.getCell(4));
        if (timeStr != null && !timeStr.isBlank()) {
            try {
                checkInTime = LocalTime.parse(timeStr.trim(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("时间格式错误（应为 HH:mm:ss）: " + timeStr);
            }
        }
        attendance.setCheckInTime(LocalDateTime.of(checkInDate, checkInTime));

        // F列: 状态（必填，NORMAL/LATE/EARLY/ABSENT）
        String status = getCellStringValue(row.getCell(5));
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("考勤状态不能为空");
        }
        status = status.trim().toUpperCase();
        if (!"NORMAL".equals(status) && !"LATE".equals(status)
                && !"ABSENT".equals(status)) {
            throw new IllegalArgumentException("考勤状态无效（应为 NORMAL/LATE/ABSENT）: " + status);
        }
        attendance.setStatus(status);

        // G列: 备注（可选）
        String remark = getCellStringValue(row.getCell(6));
        attendance.setRemark(remark != null ? remark.trim() : "");

        // H列: IP地址（可选）
        String ip = getCellStringValue(row.getCell(7));
        attendance.setIp(ip != null ? ip.trim() : "");

        return attendance;
    }

    /**
     * 安全获取单元格的字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                // 如果是整数（如学号），转为无小数点的字符串
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }
}
