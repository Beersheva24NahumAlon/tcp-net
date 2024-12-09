package telran.net;

public interface TCPConfigurationProperties {
    String REQUEST_TYPE_FIELD = "requestType";
    String REQUEST_DATA_FIELD = "requestData";
    String RESPONSE_CODE_FIELD = "responseCode";
    String RESPONSE_DATA_FIELD = "responSseData";
    int DEFAULT_INTERVAL = 3000;
    int DEFAULT_ATTAMPTS = 10;
    int TIMEOUT = 10;
    int TOTAL_TIMEOUT = 60000;
    int BAD_RESPONSES = 20;
    int REQUESTS_PER_SECOND = 100;
}
