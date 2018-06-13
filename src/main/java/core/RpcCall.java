package core;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public class RpcCall {
    private final long methodMeta;
    private final byte[] body;
    private final Consumer<ByteBuf> callback;

    public RpcCall(long methodMeta, byte[] body, final Consumer<ByteBuf> callback) {
        this.methodMeta = methodMeta;
        this.body = body;
        this.callback = callback;
    }

    public long getMethodMeta() {
        return methodMeta;
    }

    public byte[] getBody() {
        return body;
    }

    public Consumer<ByteBuf> getCallback()
    {
        return callback;
    }

    //fixme equals hashcode toString
}
