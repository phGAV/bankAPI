package server;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import daoImpl.CardDao;
import model.Card;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
        String requestParamValue = null;

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

    private String handlePostRequest(HttpExchange httpExchange) {
        return null;
    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException, SQLException {
        OutputStream outputStream = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));

        long id = Long.parseLong(requestParamValue);
        CardDao cardDao = new CardDao(connection);
        List<Card> resultList = cardDao.getAllByClientId(id);

        String jsonOutput = JSON.toJSONString(resultList);

        httpExchange.sendResponseHeaders(STATUS_OK, jsonOutput.length());

        outputStream.write(jsonOutput.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
