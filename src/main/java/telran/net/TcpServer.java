package telran.net;

import static telran.net.TCPConfigurationProperties.TIMEOUT;

import java.net.*;
import java.util.concurrent.*;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    boolean gracefulShutdown = false;

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(TIMEOUT);
            System.out.println("Server is listening the port " + port);
            while (!gracefulShutdown) {
                try {
                    Socket socket = serverSocket.accept();
                    var session = new TcpClientServerSession(protocol, socket);
                    Thread thread = new Thread(session);
                    executor.execute(thread);
                } catch (SocketTimeoutException e) {
                    if (gracefulShutdown) {
                        executor.shutdownNow();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void shutdown() {
        gracefulShutdown = true;
    }
}
