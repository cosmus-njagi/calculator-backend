package com.pycs.calculatorbackend.controller;

import com.pycs.calculatorbackend.configuration.JwtTokenUtil;
import com.pycs.calculatorbackend.model.*;
import com.pycs.calculatorbackend.service.MailServiceImpl;
import com.pycs.calculatorbackend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author njagi
 * @Date 24/06/2023
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    MailServiceImpl mailService;

    private Response apiResponse = new Response();

    private void authenticate(String username, String password) throws AuthenticationException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            String authUsername = authenticationRequest.getUsername();
            String authPassword = authenticationRequest.getPassword();
            System.out.println("username received ## " + authenticationRequest.getUsername());
            authenticate(authUsername, authPassword);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authUsername);
            final String token = jwtTokenUtil.generateToken(userDetails);
            System.out.println("authentication successful");
            apiResponse.setReturnCode("00");
            apiResponse.setMessage(new JwtResponse(token).getToken());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (DisabledException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! internal error occurred");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! invalid credentials");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (AuthenticationException e) {
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! internal error occurred");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calculateLoan")
    public LoanCalculationResult calculateLoan(@RequestBody LoanRequest loanRequest) {
        double loanAmount = loanRequest.getAmount();
        int loanPeriod = loanRequest.getLoanPeriod();
        PaymentFrequency paymentFrequency = loanRequest.getPaymentFrequency();
        InterestType interestType = loanRequest.getInterestType();
        LocalDate startDate = loanRequest.getStartDate();

        double interestRate = 0.0;
        if (interestType == InterestType.FLAT_RATE) {
            interestRate = 0.18;
        } else if (interestType == InterestType.REDUCING_BALANCE) {
            interestRate = 0.25;
        }

        double processingFees = calculateProcessingFees(loanAmount);
        double exciseDuty = calculateExciseDuty(processingFees);
        double legalFees = 10000.0; //in KES

        double takeHomeAmount = calculateTakeHomeAmount(loanAmount, processingFees, exciseDuty, legalFees);

        // Calculate installment details based on the loan period
        List<InstallmentDetail> installments = new ArrayList<>();
        double balance = loanAmount;

        for (int i = 1; i <= loanPeriod; i++) {
            InstallmentDetail installment = new InstallmentDetail();
            installment.setInstallmentNumber(i);
            installment.setDueDate(startDate.plusMonths(i));
            installment.setStartingBalance(balance);

            double interestAmount = calculateInterestAmount(balance, interestRate, paymentFrequency);
            double installmentAmount = calculateInstallmentAmount(balance, interestAmount, loanPeriod, paymentFrequency);
            double totalRepayment = installmentAmount + processingFees + exciseDuty + legalFees;

            installment.setInterestAmount(interestAmount);
            installment.setInstallmentAmount(installmentAmount);
            installment.setTotalRepayment(totalRepayment);

            installments.add(installment);

            balance -= installmentAmount;
        }

        LoanCalculationResult calculationResult = new LoanCalculationResult();
        calculationResult.setInterestRate(interestRate);
        calculationResult.setProcessingFees(processingFees);
        calculationResult.setExciseDuty(exciseDuty);
        calculationResult.setLegalFees(legalFees);
        calculationResult.setTakeHomeAmount(takeHomeAmount);
        calculationResult.setInstallments(installments);

        return calculationResult;
    }

    private double calculateInterestAmount(double balance, double interestRate, PaymentFrequency paymentFrequency) {
        double annualInterestRate = interestRate * 100;
        double interestAmount = 0.0;

        switch (paymentFrequency) {
            case ANNUALLY:
                interestAmount = (balance * annualInterestRate) / 100;
                break;
            case QUARTERLY:
                interestAmount = (balance * annualInterestRate) / (100 * 4);
                break;
            case MONTHLY:
                interestAmount = (balance * annualInterestRate) / (100 * 12);
                break;
            case EVERY_SIX_MONTHS:
                interestAmount = (balance * annualInterestRate) / (100 * 2);
                break;
        }

        return interestAmount;
    }

    private double calculateInstallmentAmount(double balance, double interestAmount, int loanPeriod, PaymentFrequency paymentFrequency) {
        int numberOfPayments = getNumberOfPayments(paymentFrequency, loanPeriod);
        return (balance + interestAmount) / numberOfPayments;
    }

    private int getNumberOfPayments(PaymentFrequency paymentFrequency, int loanPeriod) {
        switch (paymentFrequency) {
            case ANNUALLY:
                return loanPeriod;
            case QUARTERLY:
                return loanPeriod * 4;
            case MONTHLY:
                return loanPeriod * 12;
            case EVERY_SIX_MONTHS:
                return loanPeriod * 2;
            default:
                return 0;
        }
    }


    private double calculateProcessingFees(double loanAmount) {
        return loanAmount * 0.03;
    }

    private double calculateExciseDuty(double processingFees) {
        return processingFees * 0.2;
    }

    private double calculateTakeHomeAmount(double loanAmount, double processingFees, double exciseDuty, double legalFees) {
        return loanAmount - processingFees - exciseDuty - legalFees;
    }

    // sending email
    @PostMapping(  "/sendEmail")
    public ResponseEntity<?> sendCalculatorEmail (@ModelAttribute MailBody mailbody) {
        String to = mailbody.getTo();
        String subject = mailbody.getSubject();
        String text = mailbody.getText();
        MultipartFile attachment = mailbody.getAttachment();
        System.out.println("to address "+to);
        try {
            this.mailService.sendMail(to, subject, text, attachment);
            apiResponse.setReturnCode("00");
            apiResponse.setMessage("Email sent successfully");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            apiResponse.setReturnCode("01");
            apiResponse.setMessage("oops! internal error occurred");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

