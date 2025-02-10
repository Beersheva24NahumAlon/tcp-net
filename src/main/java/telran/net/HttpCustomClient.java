package telran.net;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import telran.net.exceptions.ServerUnavailableException;

public class HttpCustomClient implements NetworkClient {
    HttpClient client;
    String protocol;
    String host;
    int port;

    public HttpCustomClient(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public String sendAndReceive(String requestType, String requestData) {
        String res = "";
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("%s://%s:%d/%s".formatted(protocol, host, port, requestType)))
                    .POST(BodyPublishers.ofString(requestData)).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            res = response.body();
            if (response.statusCode() >= 400) {
                throw new RuntimeException(res);
            }
        } catch (IOException e) {
            throw new ServerUnavailableException(host, port);
        } catch (InterruptedException e) {
        } 
        return res;
    }

}
