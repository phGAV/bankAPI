package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Card {
    private long number;
    private long account_id;
//    private BigDecimal balance;

    public Card(long number, long account_id) {
        this.number = number;
        this.account_id = account_id;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number && account_id == card.account_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, account_id);
    }
}
