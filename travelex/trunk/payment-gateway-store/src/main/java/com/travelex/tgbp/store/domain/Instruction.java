package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import com.travelex.tgbp.store.type.Currency;

/**
 * Represents payment record entity.
 */
public class Instruction {

    private Long id;
    private Long submissionId;
    private Currency currency;
    private LocalDate valueDate;
    private BigDecimal amount;
    private Long outputInstructionId;
    private String data;

    /**
     * Initialises with attributes.
     *
     * @param submissionId - submission identifier.
     */
    public Instruction(Long submissionId, Currency currency, LocalDate valueDate, BigDecimal amount) {
        this.submissionId = submissionId;
        this.currency = currency;
        this.valueDate = valueDate;
        this.amount = amount;
    }
    
    /**
     * Sets the snippet of data related to the instruction
     * @param data
     */
    public void setData(String data){
    	this.data = data;
    }

    /**
     * Returns entity identifier.
     *
     * @return the entity id
     */
    public Long getId() {
        return id;
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
    public String getData(){
    	return data;
    }

}
