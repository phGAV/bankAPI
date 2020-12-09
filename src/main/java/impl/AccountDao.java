package impl;

import model.Account;
import model.Card;
import model.Client;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AccountDao implements Dao<Account> {

    Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Account get(long number) throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE number = ?";
        Account account = null;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, number);
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                account = new Account(
                        resultSet.getLong("number"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getLong("owner_id"));
            }
        }
        return account;
    }

    @Override
    public List<Account> getAll() throws SQLException {
        String sql = "SELECT * FROM Accounts";

        List<Account> accountList = new LinkedList<>();
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            while (resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong("number"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getLong("owner_id"));
                accountList.add(account);
            }
        }
        return accountList;
    }

    public List<Account> getAllByClientId(Client client) throws SQLException {
        String sql = "SELECT * FROM Clients JOIN Accounts A on Clients.id = A.owner_id WHERE Clients.id = ?";

        List<Account> accountList = new LinkedList<>();
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, client.getId());
            st.execute();
            ResultSet resultSet = st.getResultSet();
            while (resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong("number"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getLong("owner_id"));
                accountList.add(account);
            }
        }
        return accountList;
    }

    @Override
    public void add(Account account) throws SQLException {
        String sql = "INSERT INTO Accounts (number, balance, owner_id) VALUES (?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, account.getNumber());
            st.setBigDecimal(2, account.getBalance());
            st.setLong(3, account.getOwnerId());
            st.execute();
        }
    }

    @Override
    public void update(Account account, String[] params) throws SQLException {
        String sql = "UPDATE Accounts SET balance = ? WHERE number = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setBigDecimal(1, new BigDecimal(params[0]));
            st.setLong(2, account.getNumber());
            st.execute();
        }
    }

    @Override
    public void delete(Account account) throws SQLException {
        String sql = "DELETE FROM Accounts WHERE number = ? AND owner_id = ?";

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, account.getNumber());
            st.setLong(2, account.getOwnerId());
            st.execute();
        }
    }
}
