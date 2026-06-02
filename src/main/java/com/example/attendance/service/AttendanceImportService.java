package com.example.attendance.service;

import com.example.attendance.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 考勤 Excel 导入服务接口
 */
public interface AttendanceImportService {

    /**
     * 解析并导入 Excel 考勤数据
     *
     * @param file 上传的 Excel 文件（.xlsx 或 .xls）
     * @return 导入结果（成功/失败条数 + 错误详情）
     */
    ImportResult importAttendance(MultipartFile file);
}
