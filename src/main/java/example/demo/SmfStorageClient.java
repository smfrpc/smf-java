package example.demo;

import core.SmfClient;
import demo.Response;

import java.util.function.Consumer;

public class SmfStorageClient {

    private static long GET_METHOD_META = 212494116 ^ 1719559449;

    private final SmfClient smfClient;

    public SmfStorageClient(final SmfClient smfClient) {
        this.smfClient = smfClient;
    }

    public void get(final byte[] body, final Consumer<demo.Response> callback) {
        smfClient.executeAsync(GET_METHOD_META, body, (rawResponse) -> {
            final Response rootAsResponse = Response.getRootAsResponse(rawResponse);
            callback.accept(rootAsResponse);
        });
    }
}
