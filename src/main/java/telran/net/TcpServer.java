package telran.net;

import static telran.net.TCPConfigurationProperties.*;
import java.net.*;
import java.util.concurrent.*;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    ExecutorService executor;
    int badResponses;
    int requestsPerSecond;
    int totalTimeout;


    public TcpServer(Protocol protocol, int port, int badResponses, int requestsPerSecond, int totalTimeout, int nThreads) {
        this.protocol = protocol;
        this.port = port;
        this.badResponses = badResponses;
        this.requestsPerSecond = requestsPerSecond;
        this.totalTimeout = totalTimeout;
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            System.out.println("Server is listening the port " + port);
            while (!executor.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(SOCKET_TIMEOUT);
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
