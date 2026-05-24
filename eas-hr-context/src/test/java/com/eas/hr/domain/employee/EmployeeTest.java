package com.eas.hr.domain.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 员工聚合根单元测试
 */
@DisplayName("员工聚合根测试")
class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.create(
                "张三",
                Gender.MALE,
                "110101199001011234",
                "13800138000",
                "zhangsan@example.com",
                DepartmentId.of("DEPT001"),
                PositionId.of("POS001")
        );
    }

    @Nested
    @DisplayName("员工创建测试")
    class CreationTests {

        @Test
        @DisplayName("创建员工后状态应为待入职")
        void shouldHavePendingStatusWhenCreated() {
            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.PENDING);
        }

        @Test
        @DisplayName("创建员工后应包含基本信息")
        void shouldHaveBasicInfoWhenCreated() {
            assertThat(employee.getName()).isEqualTo("张三");
            assertThat(employee.getGender()).isEqualTo(Gender.MALE);
            assertThat(employee.getPhone()).isEqualTo("13800138000");
            assertThat(employee.getEmail()).isEqualTo("zhangsan@example.com");
        }

        @Test
        @DisplayName("创建员工后应有部门ID和岗位ID")
        void shouldHaveDepartmentAndPositionWhenCreated() {
            assertThat(employee.getDepartmentId().value()).isEqualTo("DEPT001");
            assertThat(employee.getPositionId().value()).isEqualTo("POS001");
        }

        @Test
        @DisplayName("创建员工后应有初始状态变更记录")
        void shouldHaveInitialStatusHistoryWhenCreated() {
            assertThat(employee.getStatusHistory()).hasSize(1);
            assertThat(employee.getStatusHistory().get(0).toStatus()).isEqualTo(EmployeeStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("员工入职测试")
    class OnboardingTests {

        @Test
        @DisplayName("待入职员工可以办理入职")
        void shouldOnboardWhenStatusIsPending() {
            employee.onboard();

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.PROBATION);
        }

        @Test
        @DisplayName("非待入职员工不能办理入职")
        void shouldThrowExceptionWhenOnboardingWithNonPendingStatus() {
            employee.onboard();

            assertThatThrownBy(() -> employee.onboard())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only PENDING status can be onboarded");
        }
    }

    @Nested
    @DisplayName("试用期转正测试")
    class ProbationTests {

        @Test
        @DisplayName("试用期员工可以转正")
        void shouldConfirmProbationWhenStatusIsProbation() {
            employee.onboard();
            employee.confirmProbation();

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.REGULAR);
        }

        @Test
        @DisplayName("非试用期员工不能转正")
        void shouldThrowExceptionWhenConfirmingProbationWithNonProbationStatus() {
            assertThatThrownBy(() -> employee.confirmProbation())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only PROBATION status can be confirmed");
        }
    }

    @Nested
    @DisplayName("员工调岗测试")
    class TransferTests {

        @Test
        @DisplayName("正式员工可以办理调岗")
        void shouldTransferWhenStatusIsRegular() {
            employee.onboard();
            employee.confirmProbation();

            employee.startTransfer(
                    DepartmentId.of("DEPT002"),
                    PositionId.of("POS002")
            );

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.TRANSFERRING);
            assertThat(employee.getDepartmentId().value()).isEqualTo("DEPT002");
            assertThat(employee.getPositionId().value()).isEqualTo("POS002");
        }

        @Test
        @DisplayName("调岗中员工可以完成调岗")
        void shouldCompleteTransferWhenStatusIsTransferring() {
            employee.onboard();
            employee.confirmProbation();
            employee.startTransfer(DepartmentId.of("DEPT002"), PositionId.of("POS002"));

            employee.completeTransfer();

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.REGULAR);
        }

        @Test
        @DisplayName("非正式员工不能办理调岗")
        void shouldThrowExceptionWhenTransferringWithNonRegularStatus() {
            assertThatThrownBy(() -> employee.startTransfer(
                    DepartmentId.of("DEPT002"),
                    PositionId.of("POS002")
            )).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only REGULAR employees can transfer");
        }

        @Test
        @DisplayName("调岗员工应有状态变更记录")
        void shouldHaveStatusHistoryWhenTransferred() {
            employee.onboard();
            employee.confirmProbation();
            employee.startTransfer(DepartmentId.of("DEPT002"), PositionId.of("POS002"));

            assertThat(employee.getStatusHistory()).hasSizeGreaterThanOrEqualTo(4);
        }
    }

    @Nested
    @DisplayName("员工离职测试")
    class ResignationTests {

        @Test
        @DisplayName("在职员工可以办理离职")
        void shouldResignWhenStatusIsNotResigned() {
            employee.onboard();
            employee.confirmProbation();

            employee.resign("个人原因");

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.RESIGNING);
        }

        @Test
        @DisplayName("离职中员工可以确认离职完成")
        void shouldConfirmResignationWhenStatusIsResigning() {
            employee.onboard();
            employee.confirmProbation();
            employee.resign("个人原因");

            employee.confirmResignation();

            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.RESIGNED);
        }

        @Test
        @DisplayName("已离职员工不能再次离职")
        void shouldThrowExceptionWhenResigningWithResignedStatus() {
            employee.onboard();
            employee.confirmProbation();
            employee.resign("个人原因");
            employee.confirmResignation();

            assertThatThrownBy(() -> employee.resign("再次离职"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Employee is already resigned");
        }

        @Test
        @DisplayName("离职员工应发布领域事件")
        void shouldPublishEventWhenResigning() {
            employee.onboard();
            employee.confirmProbation();

            employee.resign("个人原因");

            assertThat(employee.hasUnpublishedEvents()).isTrue();
        }
    }

    @Nested
    @DisplayName("员工技能测试")
    class SkillTests {

        @Test
        @DisplayName("员工可以添加技能")
        void shouldAddSkill() {
            employee.addSkill(Skill.of("Java", SkillLevel.SENIOR));

            assertThat(employee.getSkills()).hasSize(1);
            assertThat(employee.getSkills().get(0).name()).isEqualTo("Java");
        }

        @Test
        @DisplayName("添加同名技能应覆盖")
        void shouldReplaceSkillWhenAddingSameNameSkill() {
            employee.addSkill(Skill.of("Java", SkillLevel.INTERMEDIATE));
            employee.addSkill(Skill.of("Java", SkillLevel.SENIOR));

            assertThat(employee.getSkills()).hasSize(1);
            assertThat(employee.getSkills().get(0).level()).isEqualTo(SkillLevel.SENIOR);
        }

        @Test
        @DisplayName("员工可以移除技能")
        void shouldRemoveSkill() {
            employee.addSkill(Skill.of("Java", SkillLevel.SENIOR));
            employee.removeSkill("Java");

            assertThat(employee.getSkills()).isEmpty();
        }

        @Test
        @DisplayName("员工可以添加认证技能")
        void shouldAddCertifiedSkill() {
            employee.addSkill(Skill.certified(
                    "Java",
                    SkillLevel.SENIOR,
                    LocalDate.of(2023, 1, 1)
            ));

            assertThat(employee.getSkills().get(0).hasCertificate()).isTrue();
        }
    }

    @Nested
    @DisplayName("员工信息更新测试")
    class UpdateInfoTests {

        @Test
        @DisplayName("员工可以更新基本信息")
        void shouldUpdateInfo() {
            employee.updateInfo("李四", "13900139000", "lisi@example.com");

            assertThat(employee.getName()).isEqualTo("李四");
            assertThat(employee.getPhone()).isEqualTo("13900139000");
            assertThat(employee.getEmail()).isEqualTo("lisi@example.com");
        }

        @Test
        @DisplayName("已离职员工不能更新信息")
        void shouldThrowExceptionWhenUpdatingInfoForResignedEmployee() {
            employee.onboard();
            employee.confirmProbation();
            employee.resign("个人原因");
            employee.confirmResignation();

            assertThatThrownBy(() -> employee.updateInfo("李四", "13900139000", "lisi@example.com"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot update resigned employee");
        }
    }

    @Nested
    @DisplayName("员工查询方法测试")
    class QueryMethodTests {

        @Test
        @DisplayName("正式员工isRegular应返回true")
        void shouldReturnTrueWhenIsRegular() {
            employee.onboard();
            employee.confirmProbation();

            assertThat(employee.isRegular()).isTrue();
        }

        @Test
        @DisplayName("非正式员工isRegular应返回false")
        void shouldReturnFalseWhenIsNotRegular() {
            assertThat(employee.isRegular()).isFalse();
        }

        @Test
        @DisplayName("正式员工canTransfer应返回true")
        void shouldReturnTrueWhenCanTransfer() {
            employee.onboard();
            employee.confirmProbation();

            assertThat(employee.canTransfer()).isTrue();
        }

        @Test
        @DisplayName("已离职员工isResigned应返回true")
        void shouldReturnTrueWhenIsResigned() {
            employee.onboard();
            employee.confirmProbation();
            employee.resign("个人原因");
            employee.confirmResignation();

            assertThat(employee.isResigned()).isTrue();
        }
    }
}
