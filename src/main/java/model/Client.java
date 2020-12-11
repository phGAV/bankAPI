package model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Objects;

public class Client {

    @JSONField(name = "id")
    private long id;

    @JSONField(name = "name")
    private String name;
//    List<Account> accountList;

    public Client(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

//    public List<Account> getAccountList() {
//        return accountList;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return (id == client.getId()) && name.equals(client.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
