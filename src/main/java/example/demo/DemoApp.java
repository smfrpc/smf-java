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

            System.out.println("Received Response details =============================================");

            System.out.println("compression : " + response.readByte());
            System.out.println("bitflags : " + response.readByte());
            System.out.println("session : " + response.readUnsignedShort());
            System.out.println("size : " + response.readUnsignedShort());
            System.out.println("checksum : " + response.readUnsignedInt());
            System.out.println("meta : " + response.readUnsignedInt());

            System.out.println();

            int bytesToRead = response.readableBytes();
            byte[] responseBytes = new byte[bytesToRead];
            response.readBytes(responseBytes);

            System.out.println("Response dump (hex) = = = = = = = = = = = = = = = = = = = = = = = = = =");

            for(int i = 0; i < responseBytes.length; i++)
            {
                if(i % 10 == 0)
                {
                    System.out.println();
                }

                System.out.print(String.format("     %02X", responseBytes[i]));
            }
            System.out.println("\n= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            System.out.println("\n========================================================================");


            latch.countDown();
        });

        //await response
        latch.await();

        //close client
        smfClient.closeGracefully();
    }
}
