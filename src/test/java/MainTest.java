import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainTest {

    @Test
    public void test() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8081/").openConnection();

    }
}
