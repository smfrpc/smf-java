package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FIXME this is ok now because performance doesn't matter - but this HAVE TO be refactored
 * <p>
 * Generate sessionId with max to {@param USHRT_MAX} because of SMF sessionsId max value.
 * This class is ThreadSafe and can be accessed from multiple threads.
 */
public class SessionIdGenerator {
    private final static Logger LOG = LogManager.getLogger();
    private static final int USHRT_MAX = 65535;

    private final Set<Integer> pendingSessions = ConcurrentHashMap.newKeySet();
    private final AtomicInteger sessionIdGen = new AtomicInteger(0);


    public int next() {
        //fixme bound check
        int sessionId = sessionIdGen.incrementAndGet();
        pendingSessions.add(sessionId);

        LOG.debug("Generated sessionId : {} ", sessionId);

        return sessionId;
    }

    public void release(final int sessionId) {
        pendingSessions.remove(sessionId);
    }

}
