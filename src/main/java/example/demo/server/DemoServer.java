package example.demo.server;

import smf.server.core.SmfServer;

public class DemoServer {
    public static void main(final String... args) throws InterruptedException {
        final SmfServer smfServer = new SmfServer("127.0.0.1", 7000);
    }
}
