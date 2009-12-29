package com.travelex.tgbp.store.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Represents group of instructions which are sent out in a same output file.
 */
public class OutputSubmission extends PersistentEntity{

    private ClearingMechanism clearingMechanism;
    private String fileName;
    private byte[] outputFile;
    @Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
    private LocalDate outputDate;

    private transient long totalItemCount;
    private transient BigDecimal totalAmount;
    private transient Set<OutputInstruction> outputInstructions = new HashSet<OutputInstruction>();

    /**
     * JPA Constructor
     */
    OutputSubmission() { }

    /**
     * Initialises with attributes.
     *
     * @param clearingMechanism - target clearing mechanism.
     */
    public OutputSubmission(ClearingMechanism clearingMechanism) {
        this.clearingMechanism = clearingMechanism;
        this.outputDate = new LocalDate();
    }

    /**
     * Returns target clearing mechanism.
     *
     * @return clearing mechanism.
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * Adds output instruction to this output submission
     *
     * @param instruction - output instruction
     */
    public void addOutputInstruction(OutputInstruction instruction) {
        outputInstructions.add(instruction);
    }

    public void assignOnInstructions(){
        for(OutputInstruction oi : outputInstructions){
            oi.assignSubmission(id);
        }
    }

    /**
     * Returns output instructions contained in this submission.
     *
     * @return collection of output instructions.
     */
    public Collection<OutputInstruction> getOutputInstructions() {
        return Collections.unmodifiableCollection(outputInstructions);
    }

    /**
     * Returns sum of payment item amount contained in this submission
     *
     * @return the totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets sum of payment item amount contained in this submission
     *
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Returns payment items count contained in this submission
     *
     * @return the totalItemCount
     */
    public long getTotalItemCount() {
        return totalItemCount;
    }

    /**
     * Sets payment items count contained in this submission
     *
     * @param totalItemCount the totalItemCount to set
     */
    public void setTotalItemCount(long totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    /**
     * Returns output submission file
     *
     * @return the outputFile
     */
    public byte[] getOutputFile() {
        return outputFile;
    }

    /**
     * Sets output submission file
     *
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(byte[] outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the outputDate
     */
    public LocalDate getOutputDate() {
        return outputDate;
    }

}
