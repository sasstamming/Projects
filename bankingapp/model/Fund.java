package model;

import java.math.BigDecimal;

public enum Fund {
    LOW_RISK(new BigDecimal("0.02")),
    MEDIUM_RISK(new BigDecimal("0.05")),
    HIGH_RISK(new BigDecimal("0.10"));

    private final BigDecimal rate;

    Fund(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}