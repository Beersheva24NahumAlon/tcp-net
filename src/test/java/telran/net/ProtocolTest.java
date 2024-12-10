package telran.net;

public class ProtocolTest implements Protocol {

    @Override
    public Response getResponse(Request request) {
        return request.requestType().equals("OK") ? new Response(ResponseCode.OK, "")
                : new Response(ResponseCode.WRONG_TYPE, "");
    }
}
