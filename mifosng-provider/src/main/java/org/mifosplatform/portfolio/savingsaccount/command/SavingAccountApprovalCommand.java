package org.mifosplatform.portfolio.savingsaccount.command;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class SavingAccountApprovalCommand {
	
	private final Long accountId;
    private final Long productId;
    private final LocalDate approvalDate;
    private final BigDecimal depositAmountPerPeriod;
    private final BigDecimal minimumBalanceForWithdrawal;
    private final BigDecimal recurringInterestRate;
    private final BigDecimal savingInterestRate;
    private final Integer interestType;
    private final Integer tenure;
    private final Integer tenureType;
    private final Integer depositFrequencyType;
    private final Integer depositEvery;
    
    private final String note;
    
    public SavingAccountApprovalCommand(final Long accountId, final Long productId, final LocalDate approvalDate,
    		final BigDecimal depositAmountPerPeriod, final BigDecimal minimumBalanceForWithdrawal, final BigDecimal recurringInterestRate,
    		final BigDecimal savingInterestRate, final Integer interestType, final Integer tenure, final Integer tenureType,
    		final Integer depositFrequencyType, final Integer depositEvery, final String note) {
    	this.accountId = accountId;
    	this.productId = productId;
    	this.approvalDate = approvalDate;
    	this.depositAmountPerPeriod = depositAmountPerPeriod;
    	this.minimumBalanceForWithdrawal = minimumBalanceForWithdrawal;
    	this.recurringInterestRate = recurringInterestRate;
    	this.savingInterestRate = savingInterestRate;
    	this.interestType = interestType;
    	this.tenure = tenure;
    	this.tenureType = tenureType;
    	this.depositFrequencyType = depositFrequencyType;
    	this.depositEvery = depositEvery;
    	this.note = note;
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public Long getProductId() {
		return this.productId;
	}

	public LocalDate getApprovalDate() {
		return this.approvalDate;
	}

	public BigDecimal getDepositAmountPerPeriod() {
		return this.depositAmountPerPeriod;
	}

	public BigDecimal getMinimumBalanceForWithdrawal() {
		return this.minimumBalanceForWithdrawal;
	}

	public BigDecimal getRecurringInterestRate() {
		return this.recurringInterestRate;
	}

	public BigDecimal getSavingInterestRate() {
		return this.savingInterestRate;
	}

	public Integer getInterestType() {
		return this.interestType;
	}

	public Integer getTenure() {
		return this.tenure;
	}

	public Integer getTenureType() {
		return this.tenureType;
	}

	public Integer getDepositFrequencyType() {
		return this.depositFrequencyType;
	}

	public Integer getDepositEvery() {
		return this.depositEvery;
	}

	public String getNote() {
		return this.note;
	}

}
