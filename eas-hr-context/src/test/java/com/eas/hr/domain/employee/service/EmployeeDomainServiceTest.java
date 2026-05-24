package com.eas.hr.domain.employee.service;

import com.eas.hr.domain.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 员工领域服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("员工领域服务测试")
class EmployeeDomainServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeDomainService employeeDomainService;

    @BeforeEach
    void setUp() {
        employeeDomainService = new EmployeeDomainService(employeeRepository);
    }

    @Nested
    @DisplayName("身份证号验证测试")
    class IdCardValidationTests {

        @Test
        @DisplayName("正确的身份证号格式应通过验证")
        void shouldValidateCorrectIdCardFormat() {
            assertThat(employeeDomainService.validateIdCardFormat("110101199001011234")).isTrue();
        }

        @Test
        @DisplayName("错误的身份证号格式应不通过验证")
        void shouldFailValidationForIncorrectIdCardFormat() {
            assertThat(employeeDomainService.validateIdCardFormat("12345")).isFalse();
            assertThat(employeeDomainService.validateIdCardFormat("ABCDEFGHIJKLMNOPQR")).isFalse();
            assertThat(employeeDomainService.validateIdCardFormat("")).isFalse();
            assertThat(employeeDomainService.validateIdCardFormat(null)).isFalse();
        }

        @Test
        @DisplayName("唯一的身份证号应通过验证")
        void shouldValidateUniqueIdCard() {
            when(employeeRepository.existsByIdCard(anyString())).thenReturn(false);

            assertThat(employeeDomainService.validateIdCardUnique("110101199001011234")).isTrue();
        }

        @Test
        @DisplayName("重复的身份证号应不通过验证")
        void shouldFailValidationForDuplicateIdCard() {
            when(employeeRepository.existsByIdCard(anyString())).thenReturn(true);

            assertThat(employeeDomainService.validateIdCardUnique("110101199001011234")).isFalse();
        }
    }

    @Nested
    @DisplayName("手机号验证测试")
    class PhoneValidationTests {

        @Test
        @DisplayName("正确的手机号格式应通过验证")
        void shouldValidateCorrectPhoneFormat() {
            assertThat(employeeDomainService.validatePhoneFormat("13800138000")).isTrue();
            assertThat(employeeDomainService.validatePhoneFormat("15912345678")).isTrue();
        }

        @Test
        @DisplayName("错误的手机号格式应不通过验证")
        void shouldFailValidationForIncorrectPhoneFormat() {
            assertThat(employeeDomainService.validatePhoneFormat("12345")).isFalse();
            assertThat(employeeDomainService.validatePhoneFormat("1380013800")).isFalse(); // 10位
            assertThat(employeeDomainService.validatePhoneFormat("138001380000")).isFalse(); // 12位
            assertThat(employeeDomainService.validatePhoneFormat("")).isFalse();
            assertThat(employeeDomainService.validatePhoneFormat(null)).isFalse();
        }

        @Test
        @DisplayName("唯一的手机号应通过验证")
        void shouldValidateUniquePhone() {
            when(employeeRepository.existsByPhone(anyString())).thenReturn(false);

            assertThat(employeeDomainService.validatePhoneUnique("13800138000")).isTrue();
        }

        @Test
        @DisplayName("重复的手机号应不通过验证")
        void shouldFailValidationForDuplicatePhone() {
            when(employeeRepository.existsByPhone(anyString())).thenReturn(true);

            assertThat(employeeDomainService.validatePhoneUnique("13800138000")).isFalse();
        }
    }

    @Nested
    @DisplayName("邮箱验证测试")
    class EmailValidationTests {

        @Test
        @DisplayName("正确的邮箱格式应通过验证")
        void shouldValidateCorrectEmailFormat() {
            assertThat(employeeDomainService.validateEmailFormat("test@example.com")).isTrue();
            assertThat(employeeDomainService.validateEmailFormat("user.name@domain.co.jp")).isTrue();
        }

        @Test
        @DisplayName("错误的邮箱格式应不通过验证")
        void shouldFailValidationForIncorrectEmailFormat() {
            assertThat(employeeDomainService.validateEmailFormat("test")).isFalse();
            assertThat(employeeDomainService.validateEmailFormat("test@")).isFalse();
            assertThat(employeeDomainService.validateEmailFormat("@example.com")).isFalse();
            assertThat(employeeDomainService.validateEmailFormat("")).isFalse();
            assertThat(employeeDomainService.validateEmailFormat(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("空值验证测试")
    class NullValidationTests {

        @Test
        @DisplayName("空身份证号应抛出异常")
        void shouldThrowExceptionWhenValidatingNullIdCard() {
            assertThatThrownBy(() -> employeeDomainService.validateIdCardUnique(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("空手机号应抛出异常")
        void shouldThrowExceptionWhenValidatingNullPhone() {
            assertThatThrownBy(() -> employeeDomainService.validatePhoneUnique(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("空字符串身份证号应抛出异常")
        void shouldThrowExceptionWhenValidatingBlankIdCard() {
            assertThatThrownBy(() -> employeeDomainService.validateIdCardUnique(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
