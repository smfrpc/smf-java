package core;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class RpcRequest {
    private final int sessionId;
    private final long methodMeta;
    private final byte[] body;
    private final Consumer<ByteBuffer> callback;

    public RpcRequest(final int sessionId, long methodMeta, byte[] body, final Consumer<ByteBuffer> callback) {
        this.sessionId = sessionId;
        this.methodMeta = methodMeta;
        this.body = body;
        this.callback = callback;
    }

    public int getSessionId() {
        return sessionId;
    }

    public long getMethodMeta() {
        return methodMeta;
    }

    public byte[] getBody() {
        return body;
    }

    public Consumer<ByteBuffer> getCallback() {
        return callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcRequest that = (RpcRequest) o;
        return sessionId == that.sessionId &&
                methodMeta == that.methodMeta &&
                Arrays.equals(body, that.body) &&
                Objects.equals(callback, that.callback);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(sessionId, methodMeta, callback);
        result = 31 * result + Arrays.hashCode(body);
        return result;
    }
}
