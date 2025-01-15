package telran.net;

import java.net.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static telran.net.TCPConfigurationProperties.*;
import java.io.*;

public class TcpClientServerSession implements Runnable {
    Protocol protocol;
    Socket socket;
    int totalTimeout = 0;
    int badResponses = 0;
    int requestsPerSecond = 0;
    Instant start = Instant.now();
    TcpServer server;

    public TcpClientServerSession(Protocol protocol, Socket socket, TcpServer server) {
        this.protocol = protocol;
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream())) {
            String request = null;
            while (!server.executor.isShutdown() && !isTotalTimeout()) {
                try {
                    request = reader.readLine();
                    totalTimeout = 0;
                    if (request == null || isRequestsPerSecond()) {
                        break;
                    }
                    String response = protocol.getResponseWithJSON(request);
                    if (isBadResponses(response)) {
                        break;
                    }
                    writer.println(response);
                } catch (SocketTimeoutException e) {
                    totalTimeout += SOCKET_TIMEOUT;
                }
            }
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean isRequestsPerSecond() {
        Instant current = Instant.now();
        if (ChronoUnit.SECONDS.between(start, current) > 1 ) {
            requestsPerSecond = 0;
            start = current;
        } else {
            requestsPerSecond++;
        }
        return requestsPerSecond > server.requestsPerSecond;
    }

    private boolean isBadResponses(String response) {
        badResponses = response.contains("OK") ? 0 : badResponses + 1;
        return badResponses > server.badResponses;
    }

    private boolean isTotalTimeout() {
        return totalTimeout > server.totalTimeout;
    }
}
