package orwell.tank.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.SimpleEscapeKeyListener;

import java.net.DatagramSocket;
import java.net.SocketException;

public final class UdpProxyFinderFactory {
    private static final Logger logback = LoggerFactory.getLogger(UdpProxyFinderFactory.class);

    public static UdpProxyFinder fromParameters(
            final int port,
            final int timeoutPerAttemptMs,
            SimpleEscapeKeyListener simpleEscapeKeyListener) {
        try {
            final DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(timeoutPerAttemptMs);
            final UdpProxyFinder udpProxyFinder = new UdpProxyFinder(
                    datagramSocket,
                    port,
                    new UdpBroadcastDataDecoder(),
                    simpleEscapeKeyListener);
            return udpProxyFinder;
        } catch (final SocketException e) {
            logback.error("Proxy finder socket exception", e);
            return null;
        }
    }
}
