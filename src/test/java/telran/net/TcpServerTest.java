package telran.net;

import java.util.Scanner;

public class TcpServerTest {

    public static void main(String[] args) {
        Protocol protocol = new ProtocolTest();
        TcpServer server = new TcpServer(protocol, 4000, 20, 100, 40000);
        Thread threadServer = new Thread(server);
        threadServer.start();
        Scanner scanner = new Scanner(System.in); 
        System.out.print("To shutdown server input \"shutdown\" and press Enter:");
        String command = scanner.nextLine();
        if (command.equals("shutdown")) {
            server.shutdown();
        }
        scanner.close(); 
    } 
}
