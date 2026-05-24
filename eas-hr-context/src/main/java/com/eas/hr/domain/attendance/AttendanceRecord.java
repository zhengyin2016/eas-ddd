package com.eas.hr.domain.attendance;

import com.eas.common.ddd.AggregateRoot;
import com.eas.hr.domain.employee.EmployeeId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * 考勤记录聚合根
 */
public class AttendanceRecord extends AggregateRoot<AttendanceId> {

    private EmployeeId employeeId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private AttendanceStatus status;
    private BigDecimal workHours;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 标准工作时间配置
    private static final LocalTime STANDARD_CHECK_IN = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_CHECK_OUT = LocalTime.of(18, 0);
    private static final BigDecimal STANDARD_WORK_HOURS = new BigDecimal("8.0");

    private AttendanceRecord(AttendanceId id, EmployeeId employeeId, LocalDate date) {
        super(id);
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.status = AttendanceStatus.ABSENT;
        this.workHours = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static AttendanceRecord create(EmployeeId employeeId, LocalDate date) {
        return new AttendanceRecord(AttendanceId.generate(), employeeId, date);
    }

    public static AttendanceRecord restore(AttendanceId id, EmployeeId employeeId, LocalDate date,
                                          LocalDateTime checkInTime, LocalDateTime checkOutTime,
                                          AttendanceStatus status, BigDecimal workHours, String notes,
                                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        AttendanceRecord record = new AttendanceRecord(id, employeeId, date);
        record.checkInTime = checkInTime;
        record.checkOutTime = checkOutTime;
        record.status = status;
        record.workHours = workHours;
        record.notes = notes;
        record.createdAt = createdAt;
        record.updatedAt = updatedAt;
        return record;
    }

    public void checkIn(LocalDateTime checkInTime) {
        Objects.requireNonNull(checkInTime, "Check in time cannot be null");
        if (this.checkInTime != null) {
            throw new IllegalStateException("Already checked in");
        }
        this.checkInTime = checkInTime;
        updateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public void checkOut(LocalDateTime checkOutTime) {
        Objects.requireNonNull(checkOutTime, "Check out time cannot be null");
        if (this.checkInTime == null) {
            throw new IllegalStateException("Cannot check out before check in");
        }
        if (this.checkOutTime != null) {
            throw new IllegalStateException("Already checked out");
        }
        if (checkOutTime.isBefore(this.checkInTime)) {
            throw new IllegalArgumentException("Check out time cannot be before check in time");
        }
        this.checkOutTime = checkOutTime;
        calculateWorkHours();
        updateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus() {
        if (checkInTime == null) {
            this.status = AttendanceStatus.ABSENT;
            return;
        }

        LocalTime actualCheckIn = checkInTime.toLocalTime();
        boolean isLate = actualCheckIn.isAfter(STANDARD_CHECK_IN);

        if (checkOutTime == null) {
            // 只签到了，还没签退
            this.status = isLate ? AttendanceStatus.LATE : AttendanceStatus.NORMAL;
        } else {
            LocalTime actualCheckOut = checkOutTime.toLocalTime();
            boolean isEarlyLeave = actualCheckOut.isBefore(STANDARD_CHECK_OUT);

            if (isLate && isEarlyLeave) {
                this.status = AttendanceStatus.LATE; // 优先标记迟到
            } else if (isLate) {
                this.status = AttendanceStatus.LATE;
            } else if (isEarlyLeave) {
                this.status = AttendanceStatus.EARLY_LEAVE;
            } else {
                this.status = AttendanceStatus.NORMAL;
            }
        }
    }

    private void calculateWorkHours() {
        if (checkInTime == null || checkOutTime == null) {
            this.workHours = BigDecimal.ZERO;
            return;
        }

        long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
        // 减去午休时间（假设1小时）
        if (minutes > 240) {
            minutes -= 60;
        }
        this.workHours = new BigDecimal(minutes).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
    }

    public void addNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isCheckedIn() {
        return checkInTime != null;
    }

    public boolean isCheckedOut() {
        return checkOutTime != null;
    }

    public boolean isComplete() {
        return checkInTime != null && checkOutTime != null;
    }
}
