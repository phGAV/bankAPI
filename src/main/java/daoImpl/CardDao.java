package daoImpl;

import model.Account;
import model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CardDao implements Dao<Card> {
    Connection connection;

    public CardDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Card get(long number) throws SQLException {
        String sql = "SELECT * FROM Cards WHERE card_number = ?";
        Card card = null;

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, number);
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if(resultSet.next()) {
                card = new Card(
                        resultSet.getLong("card_number"),
                        resultSet.getLong("account_id")
                );
            }
        }
        return card;
    }

    @Override
    public List<Card> getAll() throws SQLException {
        String sql = "SELECT * FROM Cards";
        List<Card> cardList;

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            cardList = setToList(st.getResultSet());
        }
        return cardList;
    }

    public List<Card> getAllByClientId(long id) throws SQLException {
        String sql = "SELECT * FROM Cards JOIN Accounts A on A.number = Cards.account_id " +
                     "JOIN Clients C on C.id = A.owner_id WHERE owner_id = ?";
        List<Card> cardList;

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            st.execute();
            cardList = setToList(st.getResultSet());
        }
        return cardList;
    }

    public List<Card> getAllByAccountId(Account account) throws SQLException {
        String sql = "SELECT * FROM Cards JOIN Accounts A on A.number = Cards.account_id " +
                     "JOIN Clients C on C.id = A.owner_id WHERE account_id = ?";
        List<Card> cardList;

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, account.getNumber());
            st.execute();
            cardList = setToList(st.getResultSet());
        }
        return cardList;
    }

    @Override
    public void add(Card card) throws SQLException {
        String sql = "INSERT INTO Cards (card_number, account_id) VALUES (?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, card.getNumber());
            st.setLong(2, card.getAccount_id());
            st.execute();
        }
    }

    @Override
    public void update(Card card, String[] params) throws SQLException {
        String sql = "UPDATE Cards SET card_number = ? WHERE card_number = ? AND account_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, Long.parseLong(params[0]));
            st.setLong(2, card.getNumber());
            st.setLong(3, card.getAccount_id());
            st.execute();
        }
    }

    @Override
    public void delete(Card card) throws SQLException {
        String sql = "DELETE FROM Cards WHERE card_number = ? AND account_id = ?";

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, card.getNumber());
            st.setLong(2, card.getAccount_id());
            st.execute();
        }
    }

    private List<Card> setToList(ResultSet resultSet) throws SQLException {
        List<Card> cardList = new LinkedList<>();
        while (resultSet.next()) {
            Card card = new Card(
                    resultSet.getLong("card_number"),
                    resultSet.getLong("account_id")
            );
            cardList.add(card);
        }
        return cardList;
    }
}
