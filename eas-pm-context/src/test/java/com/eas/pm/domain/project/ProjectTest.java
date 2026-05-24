package com.eas.pm.domain.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Project聚合测试
 */
@DisplayName("Project聚合测试")
class ProjectTest {

    @Test
    @DisplayName("应该成功创建项目")
    void shouldCreateProject() {
        // Given
        ProjectId id = ProjectId.generate();

        // When
        Project project = Project.builder()
            .id(id)
            .name("EAS系统开发项目")
            .customerId("CUST001")
            .contractId("CONTRACT001")
            .pmId("PM001")
            .budget(new BigDecimal("1000000"))
            .startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 12, 31))
            .build();

        // Then
        assertThat(project.id()).isEqualTo(id);
        assertThat(project.name()).isEqualTo("EAS系统开发项目");
        assertThat(project.status()).isEqualTo(ProjectStatus.PREPARING);
    }

    @Test
    @DisplayName("应该成功审批项目")
    void shouldApproveProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();

        // When
        project.approve();

        // Then
        assertThat(project.status()).isEqualTo(ProjectStatus.APPROVED);
    }

    @Test
    @DisplayName("非准备中状态的项目不能审批")
    void shouldNotApproveNonPreparingProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.approve();

        // When & Then
        assertThatThrownBy(() -> project.approve())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只有准备中的项目才能审批");
    }

    @Test
    @DisplayName("应该成功启动项目")
    void shouldStartProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.approve();

        // When
        project.start();

        // Then
        assertThat(project.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("应该成功暂停项目")
    void shouldSuspendProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.approve();
        project.start();

        // When
        project.suspend();

        // Then
        assertThat(project.status()).isEqualTo(ProjectStatus.SUSPENDED);
    }

    @Test
    @DisplayName("应该成功恢复项目")
    void shouldResumeProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.approve();
        project.start();
        project.suspend();

        // When
        project.resume();

        // Then
        assertThat(project.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("应该成功关闭项目")
    void shouldCloseProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.approve();
        project.start();

        // When
        project.close();

        // Then
        assertThat(project.status()).isEqualTo(ProjectStatus.CLOSED);
    }

    @Test
    @DisplayName("应该成功创建任务")
    void shouldCreateTask() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();

        // When
        Task task = project.createTask("开发用户管理模块", "DEV001", 40);

        // Then
        assertThat(task).isNotNull();
        assertThat(task.name()).isEqualTo("开发用户管理模块");
        assertThat(task.assigneeId()).isEqualTo("DEV001");
        assertThat(task.estimatedHours()).isEqualTo(40);
        assertThat(project.tasks()).hasSize(1);
    }

    @Test
    @DisplayName("已关闭的项目不能创建任务")
    void shouldNotCreateTaskForClosedProject() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        project.close();

        // When & Then
        assertThatThrownBy(() -> project.createTask("开发用户管理模块", "DEV001", 40))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("已关闭的项目不能创建任务");
    }

    @Test
    @DisplayName("应该成功添加里程碑")
    void shouldAddMilestone() {
        // Given
        Project project = Project.builder()
            .name("EAS系统开发项目")
            .build();
        LocalDate plannedDate = LocalDate.of(2026, 6, 30);

        // When
        Milestone milestone = project.addMilestone("完成核心模块开发", plannedDate);

        // Then
        assertThat(milestone).isNotNull();
        assertThat(milestone.name()).isEqualTo("完成核心模块开发");
        assertThat(milestone.plannedDate()).isEqualTo(plannedDate);
        assertThat(project.milestones()).hasSize(1);
    }

    @Test
    @DisplayName("结束日期不能早于开始日期")
    void notAllowEndDateBeforeStartDate() {
        // When & Then
        assertThatThrownBy(() ->
            Project.builder()
                .name("EAS系统开发项目")
                .startDate(LocalDate.of(2026, 12, 31))
                .endDate(LocalDate.of(2026, 1, 1))
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("结束日期不能早于开始日期");
    }

    @Test
    @DisplayName("预算不能为负数")
    void notAllowNegativeBudget() {
        // When & Then
        assertThatThrownBy(() ->
            Project.builder()
                .name("EAS系统开发项目")
                .budget(new BigDecimal("-1000"))
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("项目预算不能为负数");
    }
}
