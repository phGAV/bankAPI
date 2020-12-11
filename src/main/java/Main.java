import java.io.IOException;
import java.sql.*;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import server.HttpServerCreate;

public class Main {

    public static void main(String[] args) {
        try {
            DeleteDbFiles.execute("./db", "bank", true);

            JdbcConnectionPool cp = JdbcConnectionPool
                    .create("jdbc:h2:./db/bank;INIT=RUNSCRIPT FROM 'classpath:/schema.sql'\\;RUNSCRIPT FROM 'classpath:/data.sql'",
                            "sa",
                            "sa");

            HttpServerCreate.startServer(cp);
            cp.dispose();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
