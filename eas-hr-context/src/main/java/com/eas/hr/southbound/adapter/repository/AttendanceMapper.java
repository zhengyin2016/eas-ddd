package com.eas.hr.southbound.adapter.repository;

import com.eas.hr.domain.attendance.AttendanceId;
import com.eas.hr.domain.attendance.AttendanceStatus;
import com.eas.hr.domain.employee.EmployeeId;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤记录MyBatis Mapper接口
 */
@Mapper
public interface AttendanceMapper {

    @Insert("INSERT INTO hr_attendance (id, employee_id, date, check_in_time, check_out_time, " +
            "status, work_hours, notes, created_at, updated_at) " +
            "VALUES (#{id.value}, #{employeeId.value}, #{date}, #{checkInTime}, #{checkOutTime}, " +
            "#{status}, #{workHours}, #{notes}, #{createdAt}, #{updatedAt})")
    void insert(com.eas.hr.domain.attendance.AttendanceRecord record);

    @Update("UPDATE hr_attendance SET check_in_time = #{checkInTime}, check_out_time = #{checkOutTime}, " +
            "status = #{status}, work_hours = #{workHours}, notes = #{notes}, updated_at = #{updatedAt} " +
            "WHERE id = #{id.value}")
    void update(com.eas.hr.domain.attendance.AttendanceRecord record);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance WHERE id = #{value}")
    @ResultMap("attendanceResultMap")
    AttendanceDO findById(String id);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance " +
            "WHERE employee_id = #{employeeId} AND date = #{date}")
    @ResultMap("attendanceResultMap")
    AttendanceDO findByEmployeeIdAndDate(@Param("employeeId") String employeeId, @Param("date") LocalDate date);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance " +
            "WHERE employee_id = #{employeeId} AND YEAR(date) = #{year} AND MONTH(date) = #{month}")
    @ResultMap("attendanceResultMap")
    List<AttendanceDO> findByEmployeeIdAndMonth(@Param("employeeId") String employeeId,
                                                 @Param("year") int year,
                                                 @Param("month") int month);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance WHERE date = #{value}")
    @ResultMap("attendanceResultMap")
    List<AttendanceDO> findByDate(LocalDate date);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance " +
            "WHERE date BETWEEN #{startDate} AND #{endDate}")
    @ResultMap("attendanceResultMap")
    List<AttendanceDO> findByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Select("SELECT id, employee_id, date, check_in_time, check_out_time, status, work_hours, " +
            "notes, created_at, updated_at FROM hr_attendance WHERE status = #{value}")
    @ResultMap("attendanceResultMap")
    List<AttendanceDO> findByStatus(String status);

    /**
     * 考勤记录数据对象
     */
    class AttendanceDO {
        private String id;
        private String employeeId;
        private LocalDate date;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private AttendanceStatus status;
        private BigDecimal workHours;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public LocalDateTime getCheckInTime() { return checkInTime; }
        public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
        public LocalDateTime getCheckOutTime() { return checkOutTime; }
        public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public BigDecimal getWorkHours() { return workHours; }
        public void setWorkHours(BigDecimal workHours) { this.workHours = workHours; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
