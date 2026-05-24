package com.eas.crm.domain.opportunity;

import com.eas.crm.domain.customer.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("商机聚合测试")
class OpportunityTest {

    @Test
    @DisplayName("应该成功创建商机，初始阶段为初步接触，赢率20%")
    void shouldCreateOpportunityWithInitialStage() {
        // Given
        CustomerId customerId = CustomerId.of("customer001");
        String title = "ERP系统采购";
        Money estimatedAmount = Money.of(500000);
        LocalDate expectedCloseDate = LocalDate.now().plusMonths(3);
        String ownerId = "sales001";

        // When
        Opportunity opportunity = Opportunity.create(
                customerId, title, estimatedAmount, expectedCloseDate, ownerId
        );

        // Then
        assertThat(opportunity.getId()).isNotNull();
        assertThat(opportunity.getCustomerId()).isEqualTo(customerId);
        assertThat(opportunity.getTitle()).isEqualTo(title);
        assertThat(opportunity.getStage()).isEqualTo(OpportunityStage.INITIAL_CONTACT);
        assertThat(opportunity.getProbability()).isEqualTo(20);
        assertThat(opportunity.isWon()).isFalse();
        assertThat(opportunity.isLost()).isFalse();
    }

    @Test
    @DisplayName("应该成功推进商机阶段")
    void shouldAdvanceOpportunityStage() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );

        // When
        opportunity.advanceStage(OpportunityStage.NEEDS_CONFIRMATION);

        // Then
        assertThat(opportunity.getStage()).isEqualTo(OpportunityStage.NEEDS_CONFIRMATION);
        assertThat(opportunity.getProbability()).isEqualTo(40);
    }

    @Test
    @DisplayName("商机阶段只能向前推进")
    void shouldNotAdvanceStageBackward() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        opportunity.advanceStage(OpportunityStage.PROPOSAL);

        // When & Then
        assertThatThrownBy(() -> opportunity.advanceStage(OpportunityStage.NEEDS_CONFIRMATION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("forward");
    }

    @Test
    @DisplayName("到达合同签订阶段时自动标记为赢单")
    void shouldAutoMarkWonWhenReachingContractSigning() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );

        // When
        opportunity.advanceStage(OpportunityStage.CONTRACT_SIGNING);

        // Then
        assertThat(opportunity.isWon()).isTrue();
        assertThat(opportunity.getStage()).isEqualTo(OpportunityStage.CONTRACT_SIGNING);
        assertThat(opportunity.getProbability()).isEqualTo(95);
    }

    @Test
    @DisplayName("应该成功标记商机为赢单")
    void shouldMarkOpportunityAsWon() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        Money actualAmount = Money.of(480000);

        // When
        opportunity.markWon(actualAmount);

        // Then
        assertThat(opportunity.isWon()).isTrue();
        assertThat(opportunity.getActualAmount()).isEqualTo(actualAmount);
        assertThat(opportunity.getProbability()).isEqualTo(100);
    }

    @Test
    @DisplayName("应该成功标记商机为输单")
    void shouldMarkOpportunityAsLost() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        String reason = "客户选择了竞争对手";

        // When
        opportunity.markLost(reason);

        // Then
        assertThat(opportunity.isLost()).isTrue();
        assertThat(opportunity.getLostReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("赢单后不能再修改商机")
    void shouldNotModifyWonOpportunity() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        opportunity.markWon(Money.of(480000));

        // When & Then
        assertThatThrownBy(() -> opportunity.advanceStage(OpportunityStage.CONTRACT_SIGNING))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("won");
    }

    @Test
    @DisplayName("输单后不能再修改商机")
    void shouldNotModifyLostOpportunity() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        opportunity.markLost("价格原因");

        // When & Then
        assertThatThrownBy(() -> opportunity.advanceStage(OpportunityStage.NEEDS_CONFIRMATION))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("lost");
    }

    @Test
    @DisplayName("应该成功更新预计成交日期")
    void shouldUpdateExpectedCloseDate() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );
        LocalDate newDate = LocalDate.now().plusMonths(6);

        // When
        opportunity.updateExpectedCloseDate(newDate);

        // Then
        assertThat(opportunity.getExpectedCloseDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("预计成交日期不能是过去的日期")
    void shouldNotSetPastExpectedCloseDate() {
        // Given
        Opportunity opportunity = Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.of(500000), LocalDate.now().plusMonths(3), "sales001"
        );

        // When & Then
        assertThatThrownBy(() -> opportunity.updateExpectedCloseDate(LocalDate.now().minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("past");
    }

    @Test
    @DisplayName("创建商机时金额必须大于0")
    void shouldNotCreateOpportunityWithZeroAmount() {
        // When & Then
        assertThatThrownBy(() -> Opportunity.create(
                CustomerId.of("customer001"), "ERP系统采购",
                Money.zero(), LocalDate.now().plusMonths(3), "sales001"
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
