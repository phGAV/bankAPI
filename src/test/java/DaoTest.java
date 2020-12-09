import impl.AccountDao;
import impl.CardDao;
import impl.ClientDao;
import model.Account;
import model.Card;
import model.Client;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DaoTest {
    private static Connection connection;
    private static JdbcConnectionPool cp;

    @BeforeAll
    public static void init() throws SQLException {
        DeleteDbFiles.execute("./db", "test", true);

        cp = JdbcConnectionPool
                .create("jdbc:h2:./db/test;INIT=RUNSCRIPT FROM 'classpath:/schema.sql'\\;RUNSCRIPT FROM 'classpath:/data.sql'",
                        "sa",
                        "sa");

        connection = cp.getConnection();
    }

    /** Client DAO tests **/
    @Test
    public void ClientDaoTestAdd() throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ? AND name = ?";

        ClientDao clientDao = new ClientDao(connection);
        Client james = new Client("James");
        clientDao.add(james);
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, james.getId());
            st.setString(2, james.getName());
            boolean hasResult = st.execute();
            assertTrue(hasResult);
        }
    }

    @Test
    public void ClientDaoTestGet() throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ?";
        long id = 1;

        ClientDao clientDao = new ClientDao(connection);
        Client foundClient = clientDao.get(id);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            st.execute();

            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(resultSet.getLong(1), foundClient.getId());
                assertEquals(resultSet.getString(2), foundClient.getName());
            }
        }
    }

    @Test
    public void ClientDaoTestGetAll() throws SQLException {
        String sql = "SELECT * FROM Clients";

        ClientDao clientDao = new ClientDao(connection);

        List<Client> list = clientDao.getAll();
        Iterator<Client> iter = list.iterator();

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            ResultSet resultSet = st.getResultSet();

            while (resultSet.next()) {
                Client cl = iter.next();

                assertEquals(resultSet.getString(2), cl.getName());
                assertEquals(resultSet.getLong(1), cl.getId());
            }
        }
    }

    @Test
    public void ClientDaoTestUpdate() throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ?";

        String[] newName = {"newName"};

        ClientDao clientDao = new ClientDao(connection);
        Client katy = new Client("Katy");

        clientDao.add(katy);
        clientDao.update(katy, newName);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, katy.getId());
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(newName[0], resultSet.getString("name"));
            }
        }
    }

    @Test
    public void ClientDaoTestDelete() throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ? AND name = ?";

        ClientDao clientDao = new ClientDao(connection);
        Client katy = new Client("Katy");

        clientDao.add(katy);
        clientDao.delete(katy);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, katy.getId());
            st.setString(2, katy.getName());
            st.execute();
            ResultSet resultSet = st.getResultSet();

            assertFalse(resultSet.next());
        }
    }

    /** Account DAO tests **/
    @Test
    public void AccountDaoTestAdd() throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE number = ? AND owner_id = ?";

        AccountDao accountDao = new AccountDao(connection);
        Account newAccount = new Account(6666666666666666L, new BigDecimal(400000), 1);
        accountDao.add(newAccount);
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, newAccount.getNumber());
            st.setLong(2, newAccount.getOwnerId());
            boolean hasResult = st.execute();
            assertTrue(hasResult);
        }
    }

    @Test
    public void AccountDaoTestGet() throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE number = ?";
        long number = 1;

        try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Accounts")) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                number = resultSet.getLong("number");
            }
        }

        AccountDao accountDao = new AccountDao(connection);
        Account foundAccount = accountDao.get(number);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, number);
            st.execute();

            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(resultSet.getLong("number"), foundAccount.getNumber());
                assertEquals(resultSet.getBigDecimal("balance"), foundAccount.getBalance());
                assertEquals(resultSet.getLong("owner_id"), foundAccount.getOwnerId());
            }
        }
    }

    @Test
    public void AccountDaoTestGetAll() throws SQLException {
        String sql = "SELECT * FROM Accounts";

        AccountDao accountDao = new AccountDao(connection);

        List<Account> list = accountDao.getAll();
        Iterator<Account> iter = list.iterator();

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            ResultSet resultSet = st.getResultSet();

            while (resultSet.next()) {
                Account item = iter.next();

                assertEquals(resultSet.getLong("number"), item.getNumber());
                assertEquals(resultSet.getBigDecimal("balance"), item.getBalance());
                assertEquals(resultSet.getLong("owner_id"), item.getOwnerId());            }
        }
    }

    @Test
    public void AccountDaoTestUpdate() throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE number = ?";

        String[] newBalance = {"2300000.50"};

        AccountDao accountDao = new AccountDao(connection);
        Account newAccount = new Account(2233223322332233L, new BigDecimal(60), 2);

        accountDao.add(newAccount);
        accountDao.update(newAccount, newBalance);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, newAccount.getNumber());
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(new BigDecimal(newBalance[0]), resultSet.getBigDecimal("balance"));
            }
        }
    }

    @Test
    public void AccountDaoTestDelete() throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE number = ? AND owner_id = ?";

        AccountDao accountDao = new AccountDao(connection);
        Account newAccount = new Account(1133223322332233L, new BigDecimal(600), 2);

        accountDao.add(newAccount);
        accountDao.delete(newAccount);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, newAccount.getNumber());
            st.setLong(2, newAccount.getOwnerId());
            st.execute();
            ResultSet resultSet = st.getResultSet();

            assertFalse(resultSet.next());
        }
    }

    /** Card DAO tests **/
    @Test
    public void CardDaoTestAdd() throws SQLException {
        String sql = "SELECT * FROM Cards WHERE card_number = ? AND account_id = ?";
        long accountId = 0;

        try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Accounts")) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                accountId = resultSet.getLong("number");
            }
        }

        CardDao cardDao = new CardDao(connection);
        Card card = new Card(1333444455556666L, accountId);
        cardDao.add(card);
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, card.getNumber());
            st.setLong(2, card.getAccount_id());
            boolean hasResult = st.execute();
            assertTrue(hasResult);
        }
    }

    @Test
    public void CardDaoTestGet() throws SQLException {
        String sql = "SELECT * FROM Cards WHERE card_number = ?";
        long number = 1;

        try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Cards")) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                number = resultSet.getLong("card_number");
            }
        }

        CardDao cardDao = new CardDao(connection);
        Card foundCard = cardDao.get(number);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, number);
            st.execute();

            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(resultSet.getLong("card_number"), foundCard.getNumber());
                assertEquals(resultSet.getLong("account_id"), foundCard.getAccount_id());
            }
        }
    }

    @Test
    public void CardDaoTestGetAll() throws SQLException {
        String sql = "SELECT * FROM Cards";

        CardDao cardDao = new CardDao(connection);

        List<Card> list = cardDao.getAll();
        Iterator<Card> iter = list.iterator();

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            ResultSet resultSet = st.getResultSet();

            while (resultSet.next()) {
                Card cd = iter.next();

                assertEquals(resultSet.getLong("card_number"), cd.getNumber());
                assertEquals(resultSet.getLong("account_id"), cd.getAccount_id());
            }
        }
    }

    @Test
    public void CardDaoTestUpdate() throws SQLException {
        String sql = "SELECT * FROM Cards WHERE card_number = ? AND account_id = ?";
        String[] newNumber = {"1000000000000002"};
        long accountId = 0;

        try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Accounts")) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                accountId = resultSet.getLong("number");
            }
        }

        CardDao cardDao = new CardDao(connection);
        Card card = new Card(3333444455556666L, accountId);

        cardDao.add(card);
        cardDao.update(card, newNumber);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, card.getNumber());
            st.setLong(2, accountId);
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                assertEquals(Long.parseLong(newNumber[0]), resultSet.getLong("card_number"));
                assertEquals(card.getAccount_id(), resultSet.getLong("account_id"));
            }
        }
    }

    @Test
    public void CardDaoTestDelete() throws SQLException {
        String sql = "SELECT * FROM Cards WHERE card_number = ? AND account_id = ?";
        long accountId = 0;

        try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Accounts")) {
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                accountId = resultSet.getLong("number");
            }
        }

        CardDao cardDao = new CardDao(connection);
        Card card = new Card(3333444455556666L, accountId);

        cardDao.add(card);
        cardDao.delete(card);

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, card.getNumber());
            st.setLong(2, card.getAccount_id());
            st.execute();
            ResultSet resultSet = st.getResultSet();

            assertFalse(resultSet.next());
        }
    }

    @AfterAll
    public static void close() throws SQLException {
        connection.close();
        cp.dispose();
    }
}
