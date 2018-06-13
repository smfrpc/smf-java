package example.demo;

import core.RpcCall;
import core.SmfClient;
import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public class SmfStorageClient {

    private static long GET_METHOD_META = 212494116^1719559449;

    private final SmfClient smfClient;

    public SmfStorageClient(final SmfClient smfClient) {
        this.smfClient = smfClient;
    }

    public void get(final byte[] body, final Consumer<ByteBuf> callback)
    {
        smfClient.executeAsync(new RpcCall(GET_METHOD_META, body, callback));
    }
}
