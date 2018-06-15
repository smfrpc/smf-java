package example.demo;

import com.google.flatbuffers.FlatBufferBuilder;
import core.SmfClient;

import java.util.concurrent.CountDownLatch;

public class DemoApp {
    public static void main(String... args) throws InterruptedException {

        final SmfClient smfClient = new SmfClient("127.0.0.1", 7000);

        final SmfStorageClient smfStorageClient = new SmfStorageClient(smfClient);

        //construct get request.
        FlatBufferBuilder requestBuilder = new FlatBufferBuilder(0);
        int requestPosition = requestBuilder.createString("GET /something/");

        demo.Request.startRequest(requestBuilder);
        demo.Request.addName(requestBuilder, requestPosition);
        int root = demo.Request.endRequest(requestBuilder);
        requestBuilder.finish(root);

        byte[] body = requestBuilder.sizedByteArray();


        //of course, this will be removed as well
        final CountDownLatch latch = new CountDownLatch(1);

        smfStorageClient.get(body, response -> {
            int bytesToRead = response.readableBytes();
            byte[] reposeBytes = new byte[bytesToRead];
            response.readBytes(reposeBytes);

            System.out.println("Got the response : " + new String(reposeBytes));

            latch.countDown();
        });

        //await response
        latch.await();

        //close client
        smfClient.closeGracefully();
    }
}
