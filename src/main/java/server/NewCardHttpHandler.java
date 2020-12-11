package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import daoImpl.CardDao;
import model.Card;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class NewCardHttpHandler implements HttpHandler {
    private static final int STATUS_OK = 200;
    private static final int STATUS_CLIENT_ERROR = 400;
    private static final int STATUS_SERVER_ERROR = 500;

    public Connection connection;

    public NewCardHttpHandler(JdbcConnectionPool cp) throws SQLException {
        this.connection = cp.getConnection();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Card requestParamValue = null;

        if("POST".equals(httpExchange.getRequestMethod())) {
            try {
                requestParamValue = handlePostRequest(httpExchange);
                handleResponse(httpExchange, requestParamValue);
            } catch (SQLException e) {
                httpExchange.sendResponseHeaders(STATUS_SERVER_ERROR, 0);
                e.printStackTrace();
            }
        } else {
            httpExchange.sendResponseHeaders(STATUS_CLIENT_ERROR, 0);
        }
    }

//    curl -X POST localhost:8080/newCard -d '{"account_id":1111222233334444, "number":7000700070007000}'

    private Card handlePostRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder in = new StringBuilder();
        int b;
        while((b = httpInput.read()) != -1) {
            in.append((char)b);
        }

        Gson gson = new Gson();
        Card card = gson.fromJson(in.toString(), Card.class);

        httpInput.close();
        inputStream.close();
        return card;
    }

    private void handleResponse(HttpExchange httpExchange, Card requestParamValue) throws IOException, SQLException {
        OutputStream outputStream = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

        CardDao cardDao = new CardDao(connection);
        cardDao.add(requestParamValue);

        String response = "SUCCESS";

        httpExchange.sendResponseHeaders(STATUS_OK, response.length());

        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
