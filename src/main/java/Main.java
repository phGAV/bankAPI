import impl.ClientDao;
import model.Client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.List;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Server;


public class Main {

    public static void main(String[] args) {
        try {
            DeleteDbFiles.execute("./db", "bank", true);

            JdbcConnectionPool cp = JdbcConnectionPool
                    .create("jdbc:h2:./db/bank;INIT=RUNSCRIPT FROM 'classpath:/schema.sql'\\;RUNSCRIPT FROM 'classpath:/data.sql'",
                            "sa",
                            "sa");
            Connection conn = cp.getConnection();

            ClientDao clientDao = new ClientDao(conn);
            Client client = new Client("Kim");
            clientDao.add(client);
            List<Client> lst = clientDao.getAll();
            for (Client l : lst) {
                System.out.println(l.getId() + l.getName());
            }
            conn.close();
            cp.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
