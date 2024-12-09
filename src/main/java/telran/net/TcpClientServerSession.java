package telran.net;

import java.net.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.json.JSONObject;
import static telran.net.TCPConfigurationProperties.*;
import java.io.*;

public class TcpClientServerSession implements Runnable {
    Protocol protocol;
    Socket socket;
    int totalTimeout = 0;
    int badResponses = 0;
    int requests = 0;
    boolean closeSession = false;

    public TcpClientServerSession(Protocol protocol, Socket socket) {
        this.protocol = protocol;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream())) {
            String request = "";
            socket.setSoTimeout(TIMEOUT);
            Instant start = Instant.now();
            while (!closeSession) {
                try {
                    request = reader.readLine();
                    String response = protocol.getResponseWithJSON(request);
                    writer.println(response);
                    requests++;
                    if (!getStatusResponse(response).equals("OK")) {
                        badResponses++;
                    }
                    totalTimeout = 0;   
                } catch (SocketTimeoutException e) {
                    Instant finish = Instant.now();
                    long requestsPerSecond = 0;
                    if (ChronoUnit.SECONDS.between(start, finish) == 1) {
                        requestsPerSecond = requests;
                        requests = 0;
                        start = Instant.now();
                    }
                    if (totalTimeout > TOTAL_TIMEOUT || badResponses > BAD_RESPONSES || requestsPerSecond > REQUESTS_PER_SECOND) {
                        closeSession = true;
                    }
                    totalTimeout += TIMEOUT;
                }
            }
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String getStatusResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString(RESPONSE_CODE_FIELD);
    }

    public void setCloseSession(boolean closeSession) {
        this.closeSession = closeSession;
    }
}
