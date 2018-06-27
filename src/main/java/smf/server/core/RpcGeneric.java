package smf.server.core;

import smf.Header;

import java.util.Objects;

public class RpcGeneric {
    private final smf.Header header;
    private final byte[] requestBody;

    public RpcGeneric(final Header header, final byte[] requestBody) {
        this.header = header;
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcGeneric that = (RpcGeneric) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(requestBody, that.requestBody);
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    @Override
    public int hashCode() {

        return Objects.hash(header, requestBody);
    }
}
