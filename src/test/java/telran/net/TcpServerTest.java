package telran.net;

import java.util.Scanner;

public class TcpServerTest {
    private static final int N_CLIENTS = 10;
    private static final int N_REQUESTS = 200;
    private static final int PORT = 4000;
    private static final String HOST = "localhost";
    private static Protocol protocol = new ProtocolTest();
    private static TcpClient[] clients = new TcpClient[N_CLIENTS];
    private static TcpServer server = new TcpServer(protocol, PORT);

    public static void main(String[] args) {
        Thread threadServer = new Thread(server);
        threadServer.start();
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new TcpClient(HOST, PORT);
        }
        for (int i = 0; i < N_REQUESTS; i++) {
            clients[0].sendAndReceive("OK", "OK");
        }
        System.out.print("To shutdown server input \"shutdown\" and press Enter:");
        Scanner scanner = new Scanner(System.in); 
        String command = scanner.nextLine();
        if (command.equals("shutdown")) {
            server.shutdown();
        }
        scanner.close(); 
    } 
}
