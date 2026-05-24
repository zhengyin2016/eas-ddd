package com.eas.pm.domain.assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Assignment聚合测试
 */
@DisplayName("Assignment聚合测试")
class AssignmentTest {

    @Test
    @DisplayName("应该成功创建分配")
    void shouldCreateAssignment() {
        // Given
        AssignmentId id = AssignmentId.generate();
        String projectId = "PROJ001";
        String employeeId = "EMP001";
        AssignmentRole role = AssignmentRole.DEVELOPER;
        int allocation = 50;
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        // When
        Assignment assignment = new Assignment(id, projectId, employeeId, role, allocation, startDate, endDate);

        // Then
        assertThat(assignment.id()).isEqualTo(id);
        assertThat(assignment.projectId()).isEqualTo(projectId);
        assertThat(assignment.employeeId()).isEqualTo(employeeId);
        assertThat(assignment.role()).isEqualTo(role);
        assertThat(assignment.allocation()).isEqualTo(allocation);
        assertThat(assignment.released()).isFalse();
    }

    @Test
    @DisplayName("应该成功更新分配比例")
    void shouldUpdateAllocation() {
        // Given
        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            "PROJ001",
            "EMP001",
            AssignmentRole.DEVELOPER,
            50,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );

        // When
        assignment.updateAllocation(80);

        // Then
        assertThat(assignment.allocation()).isEqualTo(80);
    }

    @Test
    @DisplayName("分配比例必须在0-100之间")
    void shouldValidateAllocationRange() {
        // Given
        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            "PROJ001",
            "EMP001",
            AssignmentRole.DEVELOPER,
            50,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );

        // When & Then - 小于0
        assertThatThrownBy(() -> assignment.updateAllocation(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("分配比例必须在0-100之间");

        // When & Then - 大于100
        assertThatThrownBy(() -> assignment.updateAllocation(101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("分配比例必须在0-100之间");
    }

    @Test
    @DisplayName("应该成功释放分配")
    void shouldReleaseAssignment() {
        // Given
        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            "PROJ001",
            "EMP001",
            AssignmentRole.DEVELOPER,
            50,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );

        // When
        assignment.release();

        // Then
        assertThat(assignment.released()).isTrue();
        assertThat(assignment.releasedAt()).isNotNull();
    }

    @Test
    @DisplayName("已释放的分配不能再次释放")
    void shouldNotReleaseReleasedAssignment() {
        // Given
        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            "PROJ001",
            "EMP001",
            AssignmentRole.DEVELOPER,
            50,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );
        assignment.release();

        // When & Then
        assertThatThrownBy(() -> assignment.release())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("分配已释放");
    }

    @Test
    @DisplayName("已释放的分配不能修改")
    void shouldNotUpdateReleasedAssignment() {
        // Given
        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            "PROJ001",
            "EMP001",
            AssignmentRole.DEVELOPER,
            50,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
        );
        assignment.release();

        // When & Then
        assertThatThrownBy(() -> assignment.updateAllocation(80))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("已释放的分配不能修改");
    }

    @Test
    @DisplayName("创建时分配比例必须在范围内")
    void shouldValidateAllocationOnCreation() {
        // Given
        AssignmentId id = AssignmentId.generate();
        String projectId = "PROJ001";
        String employeeId = "EMP001";
        AssignmentRole role = AssignmentRole.DEVELOPER;
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        // When & Then
        assertThatThrownBy(() -> new Assignment(id, projectId, employeeId, role, -10, startDate, endDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("分配比例必须在0-100之间");

        assertThatThrownBy(() -> new Assignment(id, projectId, employeeId, role, 150, startDate, endDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("分配比例必须在0-100之间");
    }
}
