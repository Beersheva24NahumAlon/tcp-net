package telran.net;

import java.io.IOException;
import telran.net.exceptions.ServerUnavailableException;

public class BadRerponseTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        TcpClient client = new TcpClient("localhost", 4000);
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(500);
                client.sendAndReceive("BAD", "");
            } catch (ServerUnavailableException e) {
                System.out.println("Server closed the connection!");
                break;
            } catch (RuntimeException e) {
                System.out.println("Server returns bad responce #" + i);
            }
        }
        client.close();
    }
}
