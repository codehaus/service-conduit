package com.travelex.tgbp.output.types;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Configuration type describing remittance account information for a clearing mechanism.
 */
public class RemittanceAccountConfig {

    private ClearingMechanism clearingMechanism;
    private String bankId;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String accountHolderAddressLine1;
    private String accountHolderCity;
    private String accountHolderState;
    private String accountHolderZip;

    /**
     * @return the clearingMechanism
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * @return the bankId
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @return the accountHolderName
     */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * @return the accountHolderAddressLine1
     */
    public String getAccountHolderAddressLine1() {
        return accountHolderAddressLine1;
    }

    /**
     * @return the accountHolderCity
     */
    public String getAccountHolderCity() {
        return accountHolderCity;
    }

    /**
     * @return the accountHolderState
     */
    public String getAccountHolderState() {
        return accountHolderState;
    }

    /**
     * @return the accountHolderZip
     */
    public String getAccountHolderZip() {
        return accountHolderZip;
    }

}
