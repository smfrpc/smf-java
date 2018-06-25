package smf.server.core;

import smf.Header;

import java.nio.ByteBuffer;
import java.util.Objects;

public class RpcRequest {

    private final smf.Header header;
    private final ByteBuffer responseBody;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcRequest that = (RpcRequest) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(responseBody, that.responseBody);
    }

    public Header getHeader() {
        return header;
    }

    public ByteBuffer getResponseBody() {
        return responseBody;
    }

    @Override
    public int hashCode() {

        return Objects.hash(header, responseBody);
    }

    public RpcRequest(final Header header, final ByteBuffer responseBody) {
        this.header = header;
        this.responseBody = responseBody;
    }

}
