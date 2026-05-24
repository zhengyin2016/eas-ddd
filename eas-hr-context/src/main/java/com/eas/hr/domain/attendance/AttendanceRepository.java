package com.eas.hr.domain.attendance;

import com.eas.common.ddd.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 考勤记录资源库接口
 */
public interface AttendanceRepository extends Repository<AttendanceRecord, AttendanceId> {

    /**
     * 根据员工ID和日期查找考勤记录
     *
     * @param employeeId 员工ID
     * @param date       日期
     * @return 考勤记录
     */
    Optional<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

    /**
     * 根据员工ID和月份查找考勤记录
     *
     * @param employeeId 员工ID
     * @param yearMonth  年月
     * @return 考勤记录列表
     */
    List<AttendanceRecord> findByEmployeeIdAndMonth(String employeeId, int year, int month);

    /**
     * 根据日期查找所有考勤记录
     *
     * @param date 日期
     * @return 考勤记录列表
     */
    List<AttendanceRecord> findByDate(LocalDate date);

    /**
     * 查找指定日期范围内的考勤记录
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 考勤记录列表
     */
    List<AttendanceRecord> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查找指定状态的考勤记录
     *
     * @param status 考勤状态
     * @return 考勤记录列表
     */
    List<AttendanceRecord> findByStatus(AttendanceStatus status);
}
