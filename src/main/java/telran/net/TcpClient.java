package telran.net;

import java.net.*;
import java.time.Instant;
import java.io.*;
import org.json.JSONObject;

import static telran.net.TCPConfigurationProperties.*;

public class TcpClient implements Closeable{
    Socket socket;
    PrintStream writer;
    BufferedReader reader;
    int interval;
    int attempts;
    String host;
    int port;

    public TcpClient(String host, int port, int interval, int attempts) {
        this.host = host;
        this.port = port;
        this.interval = interval;
        this.attempts = attempts;
        connect();
    }

    public TcpClient(String host, int port) {
        this(host, port, DEFAULT_INTERVAL, DEFAULT_ATTAMPTS);
    }

    private void connect() {
        int count = attempts;
        do {
            try {
                socket = new Socket(host, port);
                writer = new PrintStream(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                count = 0;
            } catch (IOException e) {
                waitForInterval();
                count--;
            }
        } while (count > 0);
    }

    private void waitForInterval() {
        Instant finish = Instant.now().plusMillis(interval);
        while (Instant.now().isBefore(finish));
    } 

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public String sendAndReceive(String requestType, String requestData) {
        Request request = new Request(requestType, requestData);
        try {
            writer.println(request);
            String responseJSON = reader.readLine();
            JSONObject jsonObject = new JSONObject(responseJSON);
            ResponseCode responseCode = jsonObject.getEnum(ResponseCode.class, RESPONSE_CODE_FIELD);
            String responseData = jsonObject.getString(RESPONSE_DATA_FIELD);
            if (responseCode != ResponseCode.OK) {
                throw new RuntimeException(responseData);
            }
            return responseData;
        } catch (IOException e) {
            throw new RuntimeException("Server is unavailable");
        } 
    }
}
