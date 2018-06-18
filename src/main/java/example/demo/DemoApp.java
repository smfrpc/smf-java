package example.demo;

import com.google.flatbuffers.FlatBufferBuilder;
import core.SmfClient;

import java.util.concurrent.CountDownLatch;

public class DemoApp {
    public static void main(String... args) throws InterruptedException {

        final SmfClient smfClient = new SmfClient("127.0.0.1", 7000);

        final SmfStorageClient smfStorageClient = new SmfStorageClient(smfClient);


        //lets schedule 100 concurrent requests
        final int concurrentConCount = 3;
        final CountDownLatch endLatch = new CountDownLatch(concurrentConCount);
        final CountDownLatch startLatch = new CountDownLatch(concurrentConCount);

        for(int i = 0; i < concurrentConCount; i++)
        {
            new Thread(() -> {

                try {
                    startLatch.await();
                } catch (final InterruptedException e) {
                    //Pokemon - gotta catch 'em all" !
                }

                //construct get request.
                final FlatBufferBuilder requestBuilder = new FlatBufferBuilder(0);
                final String currentThreadName = Thread.currentThread().getName();
                int requestPosition = requestBuilder.createString("GET /something/ " + currentThreadName);

                demo.Request.startRequest(requestBuilder);
                demo.Request.addName(requestBuilder, requestPosition);
                final int root = demo.Request.endRequest(requestBuilder);
                requestBuilder.finish(root);

                final byte[] request = requestBuilder.sizedByteArray();

                smfStorageClient.get(request, response -> {
                    System.out.println(response.name());
                    endLatch.countDown();
                });

            }).start();

            //lets start all threads in one moment.
            startLatch.countDown();
        }


        //await response
        endLatch.await();

        //close client
        smfClient.closeGracefully();
    }
}
