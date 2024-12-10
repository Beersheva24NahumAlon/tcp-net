package telran.net;

import java.io.IOException;
import telran.net.exceptions.ServerUnavailableException;

public class RequestsPerSecondTest {
    public static void main(String[] args) throws IOException {
        TcpClient client = new TcpClient("localhost", 4000);
        for (int i = 0; i < 200; i++) {
            try {
                client.sendAndReceive("OK", "");
                System.out.println("Sending request #" + i);
            } catch (ServerUnavailableException e) {
                System.out.println("Server closed the connection!");
                break;
            }
        }
        client.close();
    }
}
