package server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import daoImpl.AccountDao;
import daoImpl.CardDao;
import model.Account;
import model.Card;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
            requestParamValue = handlePostRequest(httpExchange);
            try {
                handleResponse(httpExchange, requestParamValue);
            } catch (SQLException e) {
                httpExchange.sendResponseHeaders(STATUS_SERVER_ERROR, 0);
                e.printStackTrace();
            }
        } else {
            httpExchange.sendResponseHeaders(STATUS_CLIENT_ERROR, 0);
        }
    }

    //-H "Content-Type: application/json; charset=UTF-8"
//    curl -X POST -H "Content-Type: application/json; charset=UTF-8" --data ./src/main/java/server/deposit.json http://localhost:8080/depositMoney
//    curl -X POST localhost:8080/depositMoney -d '{"account":1111222233334444, "deposit":7000}'

    class Deposit {
        long account;
        BigDecimal value;
    }

    private Deposit handlePostRequest(HttpExchange httpExchange) throws IOException {
//        InputStream inputStream = httpExchange.getRequestBody();
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(
                httpExchange.getRequestBody(), "UTF-8"));
        StringBuilder in = new StringBuilder();
        String input;
        while ((input = httpInput.readLine()) != null) {
            in.append(input).append(" ");
        }
        httpInput.close();

//        String jsonInput = JSON.toJSONString(in.toString());

        return JSON.parseObject(in.toString(), Deposit.class);
    }

    private void handleResponse(HttpExchange httpExchange, Deposit requestParamValue) throws IOException, SQLException {
        OutputStream outputStream = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

        AccountDao accountDao = new AccountDao(connection);
        Account result = accountDao.get(requestParamValue.account);
        BigDecimal newBalance = result.getBalance().add(requestParamValue.value);
        accountDao.update(result, new String[]{newBalance.toString()});

        String jsonOutput = "SUCCESS\n" + JSON.toJSONString(result);

        httpExchange.sendResponseHeaders(STATUS_OK, jsonOutput.length());

        outputStream.write(jsonOutput.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
