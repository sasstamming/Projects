package model;

import java.math.BigDecimal;

public class User {

    private final String name;
    private BigDecimal cash;
    private final InvestmentAccount investmentAcc;
    private final SavingsAccount savingsAcc;

    public User(String name) {
        this.name = name;
        this.cash = new BigDecimal("1000");
        this.investmentAcc = new InvestmentAccount();
        this.savingsAcc = new SavingsAccount();
    }

    public void applyGrowth() {
        savingsAcc.addInterest();
        investmentAcc.applyAppreciation();
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAcc;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAcc;
    }
}