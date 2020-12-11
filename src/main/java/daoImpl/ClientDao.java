package daoImpl;

import model.Client;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ClientDao implements Dao<Client> {
    Connection connection;
    Statement statement;

    public ClientDao(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
    }

    @Override
    public Client get(long id) throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ?";
        Client client = null;

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            st.execute();
            ResultSet resultSet = st.getResultSet();
            if (resultSet.next()) {
                client = new Client(resultSet.getString("name"));
                client.setId(id);
            }
        }
        return client;
    }

    @Override
    public List<Client> getAll() throws SQLException {
        String sql = "SELECT * FROM Clients";

        List<Client> clients = new LinkedList<>();

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
            ResultSet resultSet = st.getResultSet();

            while (resultSet.next()) {
                Client client = new Client(resultSet.getString("name"));
                client.setId(resultSet.getInt("id"));
                clients.add(client);
            }
        }
        return clients;
    }

    @Override
    public void add(Client client) throws SQLException {
        String sql = "INSERT INTO Clients (name) VALUES (?)";

        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, client.getName());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()) {
                client.setId(resultSet.getLong("id"));
            }
        }
    }

    @Override
    public void update(Client client, String[] params) throws SQLException {
        String sql = "UPDATE Clients SET name = ? WHERE id = ?";

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, params[0]);
            st.setLong(2, client.getId());
            st.execute();
        }
    }

    @Override
    public void delete(Client client) throws SQLException {
        String sql = "DELETE FROM Clients WHERE name = ? AND id = ?";

        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, client.getName());
            st.setLong(2, client.getId());
            st.execute();
        }
    }
}
