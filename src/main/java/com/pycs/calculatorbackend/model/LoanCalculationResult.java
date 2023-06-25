package com.pycs.calculatorbackend.model;

import java.util.List;

/**
 * @author njagi
 * @Date 25/06/2023
 */
public class LoanCalculationResult {
    private double interestRate;
    private double processingFees;
    private double exciseDuty;
    private double legalFees;
    private double takeHomeAmount;
    private List<InstallmentDetail> installments;

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getProcessingFees() {
        return processingFees;
    }

    public void setProcessingFees(double processingFees) {
        this.processingFees = processingFees;
    }

    public double getExciseDuty() {
        return exciseDuty;
    }

    public void setExciseDuty(double exciseDuty) {
        this.exciseDuty = exciseDuty;
    }

    public double getLegalFees() {
        return legalFees;
    }

    public void setLegalFees(double legalFees) {
        this.legalFees = legalFees;
    }

    public double getTakeHomeAmount() {
        return takeHomeAmount;
    }

    public void setTakeHomeAmount(double takeHomeAmount) {
        this.takeHomeAmount = takeHomeAmount;
    }

    public List<InstallmentDetail> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentDetail> installments) {
        this.installments = installments;
    }
}
