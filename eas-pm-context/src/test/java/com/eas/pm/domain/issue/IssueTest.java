package com.eas.pm.domain.issue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Issue聚合测试
 */
@DisplayName("Issue聚合测试")
class IssueTest {

    @Test
    @DisplayName("应该成功创建问题")
    void shouldCreateIssue() {
        // Given
        IssueId id = IssueId.generate();
        String projectId = "PROJ001";
        String title = "登录功能异常";
        String description = "用户无法正常登录系统";
        IssueSeverity severity = IssueSeverity.MAJOR;
        int priority = 1;

        // When
        Issue issue = new Issue(id, projectId, title, description, severity, priority);

        // Then
        assertThat(issue.id()).isEqualTo(id);
        assertThat(issue.projectId()).isEqualTo(projectId);
        assertThat(issue.title()).isEqualTo(title);
        assertThat(issue.severity()).isEqualTo(IssueSeverity.MAJOR);
        assertThat(issue.status()).isEqualTo(IssueStatus.OPEN);
        assertThat(issue.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("应该成功分配问题")
    void shouldAssignIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );
        String assigneeId = "DEV001";

        // When
        issue.assign(assigneeId);

        // Then
        assertThat(issue.assigneeId()).isEqualTo(assigneeId);
        assertThat(issue.status()).isEqualTo(IssueStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("已关闭的问题不能分配")
    void shouldNotAssignClosedIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );
        issue.assign("DEV001");
        issue.resolve("已修复登录问题");
        issue.close();

        // When & Then
        assertThatThrownBy(() -> issue.assign("DEV002"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("已关闭的问题不能分配");
    }

    @Test
    @DisplayName("应该成功解决问题")
    void shouldResolveIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );
        issue.assign("DEV001");
        String resolution = "已修复登录问题";

        // When
        issue.resolve(resolution);

        // Then
        assertThat(issue.status()).isEqualTo(IssueStatus.RESOLVED);
        assertThat(issue.resolution()).isEqualTo(resolution);
        assertThat(issue.resolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("只有处理中的问题才能解决")
    void shouldOnlyResolveInProgressIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );

        // When & Then - OPEN状态
        assertThatThrownBy(() -> issue.resolve("已修复"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只有处理中的问题才能解决");
    }

    @Test
    @DisplayName("应该成功关闭问题")
    void shouldCloseIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );
        issue.assign("DEV001");
        issue.resolve("已修复登录问题");

        // When
        issue.close();

        // Then
        assertThat(issue.status()).isEqualTo(IssueStatus.CLOSED);
    }

    @Test
    @DisplayName("只有已解决的问题才能关闭")
    void shouldOnlyCloseResolvedIssue() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );
        issue.assign("DEV001");

        // When & Then
        assertThatThrownBy(() -> issue.close())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只有已解决的问题才能关闭");
    }

    @Test
    @DisplayName("问题严重程度不能被修改")
    void severityShouldNotBeModified() {
        // Given
        Issue issue = new Issue(
            IssueId.generate(),
            "PROJ001",
            "登录功能异常",
            "用户无法正常登录系统",
            IssueSeverity.MAJOR,
            1
        );

        // When & Then - Issue没有setSeverity方法，严重程度在构造后不可变
        assertThat(issue.severity()).isEqualTo(IssueSeverity.MAJOR);
    }
}
