package model;

import java.math.BigDecimal;

public abstract class Account {
    protected BigDecimal balance = BigDecimal.ZERO;

    public BigDecimal getBalance() {
        return balance;
    }

    public void add(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void subtract(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}