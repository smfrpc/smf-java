package core;

import smf.Header;

import java.nio.ByteBuffer;

public class RpcResponse {

    private final smf.Header header;
    private final ByteBuffer repsonseBody;

    public RpcResponse(final smf.Header header, final ByteBuffer repsonseBody)
    {
        this.header = header;
        this.repsonseBody = repsonseBody;
    }

    public Header getHeader() {
        return header;
    }

    public ByteBuffer getResponseBody() {
        return repsonseBody;
    }

}
