package example.demo;

import com.google.flatbuffers.FlatBufferBuilder;
import core.SmfClient;

import java.util.concurrent.CountDownLatch;

public class DemoApp {
    public static void main(String... args) throws InterruptedException {

        final SmfClient smfClient = new SmfClient("127.0.0.1", 7000);

        final SmfStorageClient smfStorageClient = new SmfStorageClient(smfClient);

        //construct get request.
        FlatBufferBuilder request = new FlatBufferBuilder(0);
        int requestPosition = request.createString("GET /something/");
        int root = demo.Request.createRequest(request, requestPosition);
        request.finish(root);

        //this can explode if buffer not backed by array will be used :D
        byte[] body = request.dataBuffer().array();

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
