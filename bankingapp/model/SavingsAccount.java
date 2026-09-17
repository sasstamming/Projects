package model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {

    public void addInterest() {
        balance = balance.add(balance.multiply(new BigDecimal("0.01")));
    }
}