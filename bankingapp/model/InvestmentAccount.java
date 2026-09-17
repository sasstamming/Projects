package model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class InvestmentAccount extends Account {

    private Map<Fund, BigDecimal> invested = new EnumMap<>(Fund.class);

    public InvestmentAccount() {
        for (Fund f : Fund.values()) {
            invested.put(f, BigDecimal.ZERO);
        }
    }

    public Map<Fund, BigDecimal> getInvestedAmounts() {
        return invested;
    }

    public void applyAppreciation() {
        for (Fund f : Fund.values()) {
            BigDecimal amount = invested.get(f);
            invested.put(f, amount.add(amount.multiply(f.getRate())));
        }
    }

    public void invest(Fund fund, BigDecimal amount) {
        subtract(amount);
        invested.put(fund, invested.get(fund).add(amount));
    }

    public void withdrawAll() {
        for (Fund f : Fund.values()) {
            balance = balance.add(invested.get(f));
            invested.put(f, BigDecimal.ZERO);
        }
    }
}
