package com.eas.pm.domain.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Task实体测试
 */
@DisplayName("Task实体测试")
class TaskTest {

    @Test
    @DisplayName("应该成功创建任务")
    void shouldCreateTask() {
        // Given
        TaskId id = TaskId.generate();
        String name = "开发用户登录功能";
        String assigneeId = "DEV001";
        int estimatedHours = 40;

        // When
        Task task = new Task(id, name, assigneeId, estimatedHours);

        // Then
        assertThat(task.id()).isEqualTo(id);
        assertThat(task.name()).isEqualTo(name);
        assertThat(task.assigneeId()).isEqualTo(assigneeId);
        assertThat(task.estimatedHours()).isEqualTo(estimatedHours);
        assertThat(task.status()).isEqualTo(TaskStatus.TODO);
        assertThat(task.actualHours()).isEqualTo(0);
    }

    @Test
    @DisplayName("应该成功开始任务")
    void shouldStartTask() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );

        // When
        task.start();

        // Then
        assertThat(task.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("只有待办任务才能开始")
    void shouldOnlyStartTodoTask() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );
        task.start();

        // When & Then
        assertThatThrownBy(() -> task.start())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只有待办任务才能开始");
    }

    @Test
    @DisplayName("应该成功完成任务")
    void shouldCompleteTask() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );
        task.start();
        int actualHours = 35;

        // When
        task.complete(actualHours);

        // Then
        assertThat(task.status()).isEqualTo(TaskStatus.DONE);
        assertThat(task.actualHours()).isEqualTo(actualHours);
    }

    @Test
    @DisplayName("只有进行中的任务才能完成")
    void shouldOnlyCompleteInProgressTask() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );

        // When & Then
        assertThatThrownBy(() -> task.complete(35))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只有进行中的任务才能完成");
    }

    @Test
    @DisplayName("实际工时不能为负数")
    void shouldNotAllowNegativeActualHours() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );
        task.start();

        // When & Then
        assertThatThrownBy(() -> task.complete(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("实际工时不能为负数");
    }

    @Test
    @DisplayName("可以设置0实际工时")
    void shouldAllowZeroActualHours() {
        // Given
        Task task = new Task(
            TaskId.generate(),
            "开发用户登录功能",
            "DEV001",
            40
        );
        task.start();

        // When
        task.complete(0);

        // Then
        assertThat(task.status()).isEqualTo(TaskStatus.DONE);
        assertThat(task.actualHours()).isEqualTo(0);
    }
}
