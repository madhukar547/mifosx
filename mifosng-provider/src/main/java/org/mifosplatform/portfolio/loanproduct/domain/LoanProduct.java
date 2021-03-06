/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.fund.domain.Fund;
import org.mifosplatform.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonArray;

/**
 * Loan products allow for categorisation of an organisations loans into
 * something meaningful to them.
 * 
 * They provide a means of simplifying creation/maintenance of loans. They can
 * also allow for product comparison to take place when reporting.
 * 
 * They allow for constraints to be added at product level.
 */
@Entity
@Table(name = "m_product_loan", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unq_name") })
public class LoanProduct extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = true)
    private Fund fund;

    @ManyToOne
    @JoinColumn(name = "loan_transaction_strategy_id", nullable = true)
    private LoanTransactionProcessingStrategy transactionProcessingStrategy;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "m_product_loan_charge", joinColumns = @JoinColumn(name = "product_loan_id"), inverseJoinColumns = @JoinColumn(name = "charge_id"))
    private List<Charge> charges;

    @Embedded
    private final LoanProductRelatedDetail loanProductRelatedDetail;

    @Column(name = "accounting_type", nullable = false)
    private Integer accountingRule;

    public static LoanProduct assembleFromJson(final Fund fund, final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy,
            final List<Charge> productCharges, final JsonCommand command, final AprCalculator aprCalculator) {

        final String name = command.stringValueOfParameterNamed("name");
        final String description = command.stringValueOfParameterNamed("description");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
        final Integer digitsAfterDecimal = command.integerValueOfParameterNamed("digitsAfterDecimal");

        final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal);
        final BigDecimal principal = command.bigDecimalValueOfParameterNamed("principal");
        final BigDecimal minPrincipal = command.bigDecimalValueOfParameterNamed("minPrincipal");
        final BigDecimal maxPrincipal = command.bigDecimalValueOfParameterNamed("maxPrincipal");

        final InterestMethod interestMethod = InterestMethod.fromInt(command.integerValueOfParameterNamed("interestType"));
        final InterestCalculationPeriodMethod interestCalculationPeriodMethod = InterestCalculationPeriodMethod.fromInt(command
                .integerValueOfParameterNamed("interestCalculationPeriodType"));
        final AmortizationMethod amortizationMethod = AmortizationMethod.fromInt(command.integerValueOfParameterNamed("amortizationType"));
        final PeriodFrequencyType repaymentFrequencyType = PeriodFrequencyType.fromInt(command
                .integerValueOfParameterNamed("repaymentFrequencyType"));
        final PeriodFrequencyType interestFrequencyType = PeriodFrequencyType.fromInt(command
                .integerValueOfParameterNamed("interestRateFrequencyType"));
        final BigDecimal interestRatePerPeriod = command.bigDecimalValueOfParameterNamed("interestRatePerPeriod");
        final BigDecimal annualInterestRate = aprCalculator.calculateFrom(interestFrequencyType, interestRatePerPeriod);

        final Integer repaymentEvery = command.integerValueOfParameterNamed("repaymentEvery");
        final Integer numberOfRepayments = command.integerValueOfParameterNamed("numberOfRepayments");
        final BigDecimal inArrearsTolerance = command.bigDecimalValueOfParameterNamed("inArrearsTolerance");
        final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(command.integerValueOfParameterNamed("accountingRule"));

        return new LoanProduct(fund, loanTransactionProcessingStrategy, name, description, currency, principal, minPrincipal, maxPrincipal,
                interestRatePerPeriod, interestFrequencyType, annualInterestRate, interestMethod, interestCalculationPeriodMethod,
                repaymentEvery, repaymentFrequencyType, numberOfRepayments, amortizationMethod, inArrearsTolerance, productCharges,
                accountingRuleType);
    }

    protected LoanProduct() {
        this.loanProductRelatedDetail = null;
    }

    public LoanProduct(final Fund fund, final LoanTransactionProcessingStrategy transactionProcessingStrategy, final String name,
            final String description, final MonetaryCurrency currency, final BigDecimal defaultPrincipal,
            final BigDecimal defaultMinPrincipal, final BigDecimal defaultMaxPrincipal,
            final BigDecimal defaultNominalInterestRatePerPeriod, final PeriodFrequencyType interestPeriodFrequencyType,
            final BigDecimal defaultAnnualNominalInterestRate, final InterestMethod interestMethod,
            final InterestCalculationPeriodMethod interestCalculationPeriodMethod, final Integer repayEvery,
            final PeriodFrequencyType repaymentFrequencyType, final Integer defaultNumberOfInstallments,
            final AmortizationMethod amortizationMethod, final BigDecimal inArrearsTolerance, final List<Charge> charges,
            final AccountingRuleType accountingRuleType) {
        this.fund = fund;
        this.transactionProcessingStrategy = transactionProcessingStrategy;
        this.name = name.trim();
        if (StringUtils.isNotBlank(description)) {
            this.description = description.trim();
        } else {
            this.description = null;
        }

        if (charges != null) {
            this.charges = charges;
        }

        this.loanProductRelatedDetail = new LoanProductRelatedDetail(currency, defaultPrincipal, defaultMinPrincipal, defaultMaxPrincipal,
                defaultNominalInterestRatePerPeriod, interestPeriodFrequencyType, defaultAnnualNominalInterestRate, interestMethod,
                interestCalculationPeriodMethod, repayEvery, repaymentFrequencyType, defaultNumberOfInstallments, amortizationMethod,
                inArrearsTolerance);

        if (accountingRuleType != null) {
            this.accountingRule = accountingRuleType.getValue();
        }
    }

    public MonetaryCurrency getCurrency() {
        return this.loanProductRelatedDetail.getCurrency();
    }

    public void update(final Fund fund) {
        this.fund = fund;
    }

    public void update(final LoanTransactionProcessingStrategy strategy) {
        this.transactionProcessingStrategy = strategy;
    }

    public boolean update(final List<Charge> newProductCharges) {
        boolean updated = false;
        if (this.charges != null) {
            final Set<Charge> setOfCharges = new HashSet<Charge>(this.charges);

            updated = setOfCharges.addAll(newProductCharges);
            if (updated) {
                this.charges = newProductCharges;
            }
        }
        return updated;
    }

    public Integer getAccountingType() {
        return this.accountingRule;
    }

    public Map<String, Object> update(final JsonCommand command, final AprCalculator aprCalculator) {

        final Map<String, Object> actualChanges = this.loanProductRelatedDetail.update(command, aprCalculator);

        final String accountingTypeParamName = "accountingRule";
        if (command.isChangeInIntegerParameterNamed(accountingTypeParamName, this.accountingRule)) {
            final Integer newValue = command.integerValueOfParameterNamed(accountingTypeParamName);
            actualChanges.put(accountingTypeParamName, newValue);
            this.accountingRule = newValue;
        }

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }

        final String descriptionParamName = "description";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = newValue;
        }

        Long existingFundId = null;
        if (this.fund != null) {
            existingFundId = this.fund.getId();
        }
        final String fundIdParamName = "fundId";
        if (command.isChangeInLongParameterNamed(fundIdParamName, existingFundId)) {
            final Long newValue = command.longValueOfParameterNamed(fundIdParamName);
            actualChanges.put(fundIdParamName, newValue);
        }

        Long existingStrategyId = null;
        if (this.transactionProcessingStrategy != null) {
            existingStrategyId = this.transactionProcessingStrategy.getId();
        }
        final String transactionProcessingStrategyParamName = "transactionProcessingStrategyId";
        if (command.isChangeInLongParameterNamed(transactionProcessingStrategyParamName, existingStrategyId)) {
            final Long newValue = command.longValueOfParameterNamed(transactionProcessingStrategyParamName);
            actualChanges.put(transactionProcessingStrategyParamName, newValue);
        }

        final String chargesParamName = "charges";
        if (command.hasParameter(chargesParamName)) {
            JsonArray jsonArray = command.arrayOfParameterNamed(chargesParamName);
            if (jsonArray != null) {

            }
            actualChanges.put(chargesParamName, command.jsonFragment(chargesParamName));
        }

        return actualChanges;
    }

    public boolean isCashBasedAccountingEnabled() {
        return AccountingRuleType.CASH_BASED.getValue().equals(this.accountingRule);
    }

    public boolean isAccrualBasedAccountingEnabled() {
        return AccountingRuleType.ACCRUAL_BASED.getValue().equals(this.accountingRule);
    }
    
    public Money getMinPrincipalAmount(){
        return this.loanProductRelatedDetail.getMinPrincipal();
    }
    
    public Money getMaxPrincipalAmount(){
        return this.loanProductRelatedDetail.getMaxPrincipal();
    }
}