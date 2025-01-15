package telran.net;

import static telran.net.TCPConfigurationProperties.*;
import java.net.*;
import java.util.concurrent.*;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    int badResponses;
    int requestsPerSecond;
    int totalTimeout;


    public TcpServer(Protocol protocol, int port, int badResponses, int requestsPerSecond, int totalTimeout) {
        this.protocol = protocol;
        this.port = port;
        this.badResponses = badResponses;
        this.requestsPerSecond = requestsPerSecond;
        this.totalTimeout = totalTimeout;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(TIMEOUT);
            System.out.println("Server is listening the port " + port);
            while (!executor.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(TIMEOUT);
                    var session = new TcpClientServerSession(protocol, socket, this);
                    executor.execute(session);
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
