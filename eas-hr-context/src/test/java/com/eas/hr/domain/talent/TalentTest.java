package com.eas.hr.domain.talent;

import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.employee.SkillLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 储备人才聚合根单元测试
 */
@DisplayName("储备人才聚合根测试")
class TalentTest {

    private Talent talent;

    @BeforeEach
    void setUp() {
        talent = Talent.create(
                "张三",
                TalentSource.REFERRAL,
                ContactInfo.of("13800138000", "zhangsan@example.com")
        );
    }

    @Nested
    @DisplayName("储备人才创建测试")
    class CreationTests {

        @Test
        @DisplayName("创建后状态应为NEW")
        void shouldHaveNewStatusWhenCreated() {
            assertThat(talent.getStatus()).isEqualTo(TalentStatus.NEW);
        }

        @Test
        @DisplayName("创建后应包含基本信息")
        void shouldHaveBasicInfoWhenCreated() {
            assertThat(talent.getName()).isEqualTo("张三");
            assertThat(talent.getSource()).isEqualTo(TalentSource.REFERRAL);
            assertThat(talent.getContactInfo().phone()).isEqualTo("13800138000");
        }

        @Test
        @DisplayName("创建后应包含技能列表")
        void shouldHaveSkillsWhenCreatedWithSkills() {
            Talent talentWithSkills = Talent.create(
                    "李四",
                    TalentSource.WEBSITE,
                    ContactInfo.of("13900139000", "lisi@example.com"),
                    List.of(Skill.of("Java", SkillLevel.SENIOR))
            );

            assertThat(talentWithSkills.getSkills()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("状态变更测试")
    class StatusChangeTests {

        @Test
        @DisplayName("NEW状态可以变更为CONTACTING")
        void shouldChangeToContactingFromNew() {
            talent.updateStatus(TalentStatus.CONTACTING);

            assertThat(talent.getStatus()).isEqualTo(TalentStatus.CONTACTING);
        }

        @Test
        @DisplayName("CONTACTING状态可以变更为INTERVIEWED")
        void shouldChangeToInterviewedFromContacting() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);

            assertThat(talent.getStatus()).isEqualTo(TalentStatus.INTERVIEWED);
        }

        @Test
        @DisplayName("INTERVIEWED状态可以变更为APPROVED")
        void shouldChangeToApprovedFromInterviewed() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);
            talent.updateStatus(TalentStatus.APPROVED);

            assertThat(talent.getStatus()).isEqualTo(TalentStatus.APPROVED);
        }

        @Test
        @DisplayName("NEW状态可以直接变更为REJECTED")
        void shouldChangeToRejectedFromNew() {
            talent.updateStatus(TalentStatus.REJECTED);

            assertThat(talent.getStatus()).isEqualTo(TalentStatus.REJECTED);
        }

        @Test
        @DisplayName("非法状态转换应抛出异常")
        void shouldThrowExceptionForInvalidStatusTransition() {
            talent.updateStatus(TalentStatus.APPROVED);

            assertThatThrownBy(() -> talent.updateStatus(TalentStatus.NEW))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("转化为员工测试")
    class ConversionTests {

        @Test
        @DisplayName("APPROVED状态可以转化为员工")
        void shouldConvertToEmployeeWhenApproved() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);
            talent.updateStatus(TalentStatus.APPROVED);

            EmployeeId employeeId = EmployeeId.generate();
            talent.convertToEmployee(employeeId);

            assertThat(talent.getStatus()).isEqualTo(TalentStatus.CONVERTED);
            assertThat(talent.getConvertedEmployeeId()).isEqualTo(employeeId);
        }

        @Test
        @DisplayName("非APPROVED状态不能转化为员工")
        void shouldThrowExceptionWhenConvertingWithoutApprovedStatus() {
            EmployeeId employeeId = EmployeeId.generate();

            assertThatThrownBy(() -> talent.convertToEmployee(employeeId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only APPROVED talent can be converted");
        }

        @Test
        @DisplayName("已转化的储备人才应发布领域事件")
        void shouldPublishEventWhenConverted() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);
            talent.updateStatus(TalentStatus.APPROVED);

            EmployeeId employeeId = EmployeeId.generate();
            talent.convertToEmployee(employeeId);

            assertThat(talent.hasUnpublishedEvents()).isTrue();
        }

        @Test
        @DisplayName("已转化的储备人才不能再次变更状态")
        void shouldThrowExceptionWhenUpdatingStatusAfterConversion() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);
            talent.updateStatus(TalentStatus.APPROVED);

            EmployeeId employeeId = EmployeeId.generate();
            talent.convertToEmployee(employeeId);

            assertThatThrownBy(() -> talent.updateStatus(TalentStatus.REJECTED))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("技能管理测试")
    class SkillTests {

        @Test
        @DisplayName("储备人才可以添加技能")
        void shouldAddSkill() {
            talent.addSkill(Skill.of("Java", SkillLevel.SENIOR));

            assertThat(talent.getSkills()).hasSize(1);
        }

        @Test
        @DisplayName("添加同名技能应覆盖")
        void shouldReplaceSkillWhenAddingSameNameSkill() {
            talent.addSkill(Skill.of("Java", SkillLevel.INTERMEDIATE));
            talent.addSkill(Skill.of("Java", SkillLevel.SENIOR));

            assertThat(talent.getSkills()).hasSize(1);
            assertThat(talent.getSkills().get(0).level()).isEqualTo(SkillLevel.SENIOR);
        }
    }

    @Nested
    @DisplayName("联系信息更新测试")
    class ContactInfoTests {

        @Test
        @DisplayName("储备人才可以更新联系信息")
        void shouldUpdateContactInfo() {
            ContactInfo newContactInfo = ContactInfo.of(
                    "13900139000",
                    "newemail@example.com",
                    "wechat_id"
            );
            talent.updateContactInfo(newContactInfo);

            assertThat(talent.getContactInfo().phone()).isEqualTo("13900139000");
            assertThat(talent.getContactInfo().email()).isEqualTo("newemail@example.com");
        }
    }

    @Nested
    @DisplayName("备注管理测试")
    class NotesTests {

        @Test
        @DisplayName("储备人才可以添加备注")
        void shouldAddNotes() {
            talent.addNotes("这是备注信息");

            assertThat(talent.getNotes()).isEqualTo("这是备注信息");
        }
    }

    @Nested
    @DisplayName("查询方法测试")
    class QueryMethodTests {

        @Test
        @DisplayName("isConverted应正确判断转化状态")
        void shouldReturnTrueWhenConverted() {
            talent.updateStatus(TalentStatus.CONTACTING);
            talent.updateStatus(TalentStatus.INTERVIEWED);
            talent.updateStatus(TalentStatus.APPROVED);

            EmployeeId employeeId = EmployeeId.generate();
            talent.convertToEmployee(employeeId);

            assertThat(talent.isConverted()).isTrue();
        }

        @Test
        @DisplayName("非转化状态isConverted应返回false")
        void shouldReturnFalseWhenNotConverted() {
            assertThat(talent.isConverted()).isFalse();
        }
    }
}
