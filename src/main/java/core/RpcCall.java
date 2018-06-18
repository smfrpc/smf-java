package core;


import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class RpcCall {
    private final int sessionId;
    private final long methodMeta;
    private final byte[] body;
    private final Consumer<ByteBuffer> callback;

    public RpcCall(final int sessionId, long methodMeta, byte[] body, final Consumer<ByteBuffer> callback) {
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

    public Consumer<ByteBuffer> getCallback()
    {
        return callback;
    }


    //fixme equals hashcode toString
}
