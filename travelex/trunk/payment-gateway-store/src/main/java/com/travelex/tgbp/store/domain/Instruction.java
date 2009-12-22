package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.travelex.tgbp.store.type.Currency;

/**
 * Represents payment record entity.
 */
public class Instruction extends PersistentEntity {

    private Long submissionId;
    private Currency currency;
    @Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
    private LocalDate valueDate;
    private BigDecimal amount;
    private Long outputInstructionId;
    private String paymentData;
    private String paymentGroupData;

    /**
     * JPA Constructor
     */
    Instruction() { }

    /**
     * Initialises with attributes.
     *
     * @param submissionId - submission identifier.
     */
    public Instruction(Currency currency, LocalDate valueDate, BigDecimal amount) {
        this.currency = currency;
        this.valueDate = valueDate;
        this.amount = amount;
    }

    /**
     * Sets parent submission id.
     *
     * @param submissionId - parent submission id.
     */
    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    /**
     * Sets the snippet of data for the group containing the instruction.
     * Note: This wouldn't scale beyond the PoC.
     * @param paymentGroupData
     */
    public void setPaymentGroupData(String paymentGroupData) {
        this.paymentGroupData = paymentGroupData;
    }

    /**
     * Sets the snippet of data related to the instruction
     * @param data
     */
    public void setPaymentData(String data){
        this.paymentData = data;
    }

    /**
     * Gets the snippet of data for the group containing the instruction.
     * @return the paymentGroupData
     */
    public String getPaymentGroupData() {
        return paymentGroupData;
    }

    /**
     * Returns parent submission identifier.
     *
     * @return the submission id
     */
    public Long getSubmissionId() {
        return submissionId;
    }

    /**
     * Returns instruction currency.
     *
     * @return currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Returns instruction value date.
     *
     * @return value date
     */
    public LocalDate getValueDate() {
        return valueDate;
    }

    /**
     * Returns instruction amount.
     *
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns corresponding output instruction id.
     *
     * @return output instruction id.
     */
    public Long getOutputInstructionId() {
        return outputInstructionId;
    }

    /**
     * Gets the data related to the instruction
     * @return
     */
    public String getPaymentData(){
        return paymentData;
    }
}
