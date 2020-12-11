package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import daoImpl.AccountDao;
import model.Account;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class DepositMoneyHttpHandler implements HttpHandler {
    private static final int STATUS_OK = 200;
    private static final int STATUS_CLIENT_ERROR = 400;
    private static final int STATUS_SERVER_ERROR = 500;

    public Connection connection;

    public DepositMoneyHttpHandler(JdbcConnectionPool cp) throws SQLException {
        this.connection = cp.getConnection();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Deposit requestParamValue = null;

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

//    curl -X POST localhost:8080/depositMoney -d '{"account":1111222233334444, "deposit":7000}'

    static class Deposit {
        long account;
        BigDecimal deposit;

        public Deposit() {
        }
    }

    private Deposit handlePostRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder in = new StringBuilder();
        int b;
        while((b = httpInput.read()) != -1) {
            in.append((char)b);
        }

        Gson gson = new Gson();
        Deposit deposit = gson.fromJson(in.toString(), Deposit.class);

        httpInput.close();
        inputStream.close();
        return deposit;
    }

    private void handleResponse(HttpExchange httpExchange, Deposit requestParamValue) throws IOException, SQLException {
        OutputStream outputStream = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

        AccountDao accountDao = new AccountDao(connection);
        Account result = accountDao.get(requestParamValue.account);
        BigDecimal newBalance = result.getBalance().add(requestParamValue.deposit);
        accountDao.update(result, new String[]{newBalance.toString()});

        Gson gson = new Gson();
        String jsonOutput = "SUCCESS\n" + gson.toJson(result);

        httpExchange.sendResponseHeaders(STATUS_OK, jsonOutput.length());

        outputStream.write(jsonOutput.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
