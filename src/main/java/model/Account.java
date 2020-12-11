package model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Account {

    @JSONField(name = "account number")
    private long number;

    @JSONField(name = "owner_id")
    private long owner_id;

    @JSONField(name = "balance")
    private BigDecimal balance;

//    List<Card> cardList;

    public Account(long number, BigDecimal balance, long owner_id) {
        this.number = number;
        this.owner_id = owner_id;
        this.balance = balance;
    }

    public Account(long number, long owner_id) {
        this.number = number;
        this.owner_id = owner_id;
        this.balance = new BigDecimal(0);
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(long owner_id) {
        this.owner_id = owner_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return number == account.number && owner_id == account.owner_id && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, owner_id, balance);
    }
}
