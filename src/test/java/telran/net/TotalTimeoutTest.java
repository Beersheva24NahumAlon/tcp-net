package telran.net;

import java.io.IOException;

public class TotalTimeoutTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        int timeout = 80000;
        TcpClient client = new TcpClient("localhost", 4000);
        try {
            client.sendAndReceive("OK", "");
            System.out.println("Send to server request #1");
            System.out.printf("Waiting for %d ms\n", timeout);
            Thread.sleep(timeout);
            client.sendAndReceive("OK", "");
            System.out.println("Send to server request #2");
        } catch (RuntimeException e) {
            System.out.println("Server closed the connection!");
        }
        client.close();
    }

}
