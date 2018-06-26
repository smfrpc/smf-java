package smf.server.core;

import smf.Header;

import java.nio.ByteBuffer;
import java.util.Objects;

public class RpcRequest {

    private final smf.Header header;
    private final ByteBuffer requestBody;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcRequest that = (RpcRequest) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(requestBody, that.requestBody);
    }

    public Header getHeader() {
        return header;
    }

    public ByteBuffer getRequestBody() {
        return requestBody;
    }

    @Override
    public int hashCode() {

        return Objects.hash(header, requestBody);
    }

    public RpcRequest(final Header header, final ByteBuffer requestBody) {
        this.header = header;
        this.requestBody = requestBody;
    }

}
