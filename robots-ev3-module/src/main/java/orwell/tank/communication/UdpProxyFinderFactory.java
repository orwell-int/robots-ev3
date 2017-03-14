package orwell.tank.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.SimpleKeyListener;

import java.net.DatagramSocket;
import java.net.SocketException;

public final class UdpProxyFinderFactory {
    private final static Logger logback = LoggerFactory.getLogger(UdpProxyFinderFactory.class);

    public static UdpProxyFinder fromParameters(
            final int port,
            final int timeoutPerAttemptMs,
            SimpleKeyListener simpleKeyListener) {
        try {
            final DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(timeoutPerAttemptMs);
            final UdpProxyFinder udpProxyFinder = new UdpProxyFinder(
                    datagramSocket,
                    port,
                    new UdpBroadcastDataDecoder(),
                    simpleKeyListener);
            return udpProxyFinder;
        } catch (final SocketException e) {
            logback.error(e.getMessage());
            return null;
        }
    }
}
