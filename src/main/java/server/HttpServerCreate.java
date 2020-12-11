package server;

import com.sun.net.httpserver.HttpServer;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServerCreate {

    public static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public static void startServer(JdbcConnectionPool cp) throws IOException, SQLException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

        server.createContext("/cardList", new CardListHttpHandler(cp));             // получение списка карт по id владельца
        server.createContext("/accountBalance", new AccountBalanceHttpHandler(cp)); // проверка баланса по номеру счёта
        server.createContext("/depositMoney", new DepositMoneyHttpHandler(cp));     // внесение средств на счёт
        server.createContext("/newCard", new NewCardHttpHandler(cp));               // выпуск новой карты к счёту

        server.setExecutor(threadPoolExecutor);
        server.start();

    }
}
