package smf.server.core;

import java.nio.ByteBuffer;
import java.util.function.Function;

public interface RpcService {
    String getServiceName();

    long getServiceId();

    Function<ByteBuffer, byte[]> getHandler(long id);
}

