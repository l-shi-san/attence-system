package com.example.attendance.controller;

import com.example.attendance.dto.AttendanceStatisticsDTO;
import com.example.attendance.dto.ImportResult;
import com.example.attendance.service.AttendanceImportService;
import com.example.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 考勤文件上传与统计控制器
 *
 * 功能：
 * 1. 批量导入 Excel 考勤数据
 * 2. 按日期范围、按周、按月统计考勤
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceUploadController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceUploadController.class);

    @Autowired
    private AttendanceImportService attendanceImportService;

    @Autowired
    private AttendanceService attendanceService;

    // ========================================================================
    // 文件上传
    // ========================================================================

    /**
     * 跳转上传页面
     */
    @GetMapping("/upload")
    public String uploadPage() {
        return "attendance-upload";
    }

    /**
     * 处理文件上传 + Excel 导入
     */
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        // 1. 空文件检查
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "请选择要上传的 Excel 文件！");
            return "redirect:/attendance/upload";
        }

        // 2. 文件大小验证（10MB 上限在 application.properties 配置，
        //    这里额外检查确保友好的错误提示）
        long maxSize = 10 * 1024 * 1024L; // 10MB
        if (file.getSize() > maxSize) {
            String errorMsg = String.format(
                    "文件大小超出限制！当前文件：%.2f MB，最大允许：10 MB",
                    file.getSize() / (1024.0 * 1024.0)
            );
            redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
            return "redirect:/attendance/upload";
        }

        // 3. 文件名检查
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMsg", "文件名不能为空！");
            return "redirect:/attendance/upload";
        }

        // 4. 格式检查
        if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
            redirectAttributes.addFlashAttribute(
                    "errorMsg", "文件格式错误！请上传 .xlsx 或 .xls 格式的 Excel 文件，当前文件：" + filename
            );
            return "redirect:/attendance/upload";
        }

        // 5. 执行导入
        try {
            log.info("开始导入考勤数据，文件：{}，大小：{} bytes", filename, file.getSize());
            ImportResult result = attendanceImportService.importAttendance(file);

            // 将结果传给页面展示
            redirectAttributes.addFlashAttribute("importResult", result);
            redirectAttributes.addFlashAttribute("fileName", filename);

            log.info("导入完成：成功{}条，失败{}条", result.getSuccessCount(), result.getFailCount());

        } catch (MaxUploadSizeExceededException e) {
            // Spring 自动拦截超过 max-file-size 的请求，但作为兜底处理
            redirectAttributes.addFlashAttribute("errorMsg", "文件大小超出 10MB 限制！");
            return "redirect:/attendance/upload";

        } catch (Exception e) {
            log.error("文件上传异常：{}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMsg", "上传处理异常：" + e.getMessage());
            return "redirect:/attendance/upload";
        }

        return "redirect:/attendance/upload";
    }

    // ========================================================================
    // 考勤数据导出
    // ========================================================================

    @GetMapping("/export")
    public void exportAttendance(@RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate,
                                 HttpServletResponse response) throws Exception {
        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate) : LocalDate.now();

        String fileName = "考勤数据_" + start + "_" + end + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        byte[] excelData = attendanceService.exportAttendanceToExcel(start, end);
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
    }

    // ========================================================================
    // 考勤统计
    // ========================================================================

    /**
     * 考勤统计页面（支持日期范围、周、月三种维度）
     */
    @GetMapping("/statistics")
    public String statisticsPage(@RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate,
                                 @RequestParam(required = false, defaultValue = "course") String mode,
                                 @RequestParam(required = false, defaultValue = "12") int weeks,
                                 @RequestParam(required = false, defaultValue = "6") int months,
                                 Model model) {

        if ("weekly".equals(mode)) {
            // ===== 按周统计 =====
            List<AttendanceStatisticsDTO> weeklyStats = attendanceService.getWeeklyStatistics(weeks);
            model.addAttribute("statsList", weeklyStats);
            model.addAttribute("statMode", "weekly");
            model.addAttribute("weeks", weeks);

        } else if ("monthly".equals(mode)) {
            // ===== 按月统计 =====
            List<AttendanceStatisticsDTO> monthlyStats = attendanceService.getMonthlyStatistics(months);
            model.addAttribute("statsList", monthlyStats);
            model.addAttribute("statMode", "monthly");
            model.addAttribute("months", months);

        } else if ("course".equals(mode)) {
            // ===== 按课程统计 =====
            LocalDate start = (startDate != null && !startDate.isBlank())
                    ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = (endDate != null && !endDate.isBlank())
                    ? LocalDate.parse(endDate) : LocalDate.now();
            List<AttendanceStatisticsDTO> courseStats = attendanceService.getStatisticsByCourse(start, end);
            model.addAttribute("statsList", courseStats);
            model.addAttribute("statMode", "course");
            model.addAttribute("startDate", start.toString());
            model.addAttribute("endDate", end.toString());

        } else {
            // ===== 按日期范围统计 =====
            LocalDate start = (startDate != null && !startDate.isBlank())
                    ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = (endDate != null && !endDate.isBlank())
                    ? LocalDate.parse(endDate) : LocalDate.now();

            AttendanceStatisticsDTO stats = attendanceService.getStatisticsByDateRange(start, end);
            model.addAttribute("stats", stats);
            model.addAttribute("statMode", "range");
            model.addAttribute("startDate", start.toString());
            model.addAttribute("endDate", end.toString());
        }

        return "attendance-statistics";
    }
}
