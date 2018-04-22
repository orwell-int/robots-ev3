package orwell.tank.communication;

import lejos.mf.common.constants.UdpProxyFinderStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.SimpleEscapeKeyListener;
import utils.BroadcastAddress;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;

public class UdpProxyFinder {
    private static final Logger logback = LoggerFactory.getLogger(UdpProxyFinder.class);
    private final int broadcastPort;
    private final DatagramSocket datagramSocket;
    private final UdpBroadcastDataDecoder udpBroadcastDataDecoder;
    private final SimpleEscapeKeyListener simpleEscapeKeyListener;
    private String pushAddress;
    private String pullAddress;
    private int attemptsPerformed = 0;

    UdpProxyFinder(
            final DatagramSocket datagramSocket,
            final int broadcastPort,
            final UdpBroadcastDataDecoder udpBroadcastDataDecoder,
            SimpleEscapeKeyListener simpleEscapeKeyListener) {
        assert null != datagramSocket;
        assert null != udpBroadcastDataDecoder;
        this.datagramSocket = datagramSocket;
        this.broadcastPort = broadcastPort;
        this.udpBroadcastDataDecoder = udpBroadcastDataDecoder;
        this.simpleEscapeKeyListener = simpleEscapeKeyListener;
    }

    /**
     * Find the server using UDP broadcast and fill data fields
     */
    public void broadcastAndGetServerAddress() {
        try {
            datagramSocket.setBroadcast(true);
            while (shouldTryToFindBeacon()) {
                logback.info("Trying to find UDP beacon on port " + broadcastPort + ", attempt [" + (attemptsPerformed + 1) + "]");
                // Broadcast the message over all the network interfaces
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    final NetworkInterface networkInterface = interfaces.nextElement();
                    sendBroadcastToInterface(networkInterface);
                }

                logback.debug("Done looping over all network interfaces. Now waiting for a reply!");
                waitForServerResponse(datagramSocket);

                attemptsPerformed++;
            }
            fillFoundAddressFields();
            datagramSocket.close();


        } catch (final Exception e) {
            logback.error("Exception happened while broadcasting and getting server-game address", e);
        }
    }

    /**
     * @return true if we did not get any meaningful response from server
     * and have not reached max allowed attempts number
     */
    private boolean shouldTryToFindBeacon() {
        return (!simpleEscapeKeyListener.wasKeyPressed() && !udpBroadcastDataDecoder.hasReceivedCorrectData());
    }

    private void sendBroadcastToInterface(final NetworkInterface networkInterface) throws SocketException {
        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
            return; // Do not broadcast to the loopback interface
        }

        for (final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            logback.debug("interface address = " + interfaceAddress.getAddress() + " ; broadcast = " +
                          interfaceAddress.getBroadcast());
            final InetAddress broadcastAddress = BroadcastAddress
                    .getBroadcastAddress(interfaceAddress.getAddress(), interfaceAddress.getBroadcast());
            if (null != broadcastAddress) {
                logback.debug("Trying to send broadcast package on interface: " + networkInterface.getDisplayName());
                sendBroadcastPackageToAddress(broadcastAddress);
            }
        }
    }

    private void waitForServerResponse(final DatagramSocket datagramSocket) throws IOException {
        int RECEIVER_BUFFER_SIZE = 512;
        final byte[] receiverBuf = new byte[RECEIVER_BUFFER_SIZE];
        final DatagramPacket receivePacket = new DatagramPacket(receiverBuf, receiverBuf.length);

        try {
            datagramSocket.receive(receivePacket);
            udpBroadcastDataDecoder.parseFrom(receivePacket);
        } catch (final SocketTimeoutException e) {
            logback.debug("Datagram socket received timeout, continue...");
        }
    }

    private void fillFoundAddressFields() {
        if (hasFoundServer()) {
            pushAddress = udpBroadcastDataDecoder.getPushAddress();
            pullAddress = udpBroadcastDataDecoder.getPullAddress();
        }
    }

    private void sendBroadcastPackageToAddress(final InetAddress broadcastAddress) {
        final String broadcastAddressString = broadcastAddress.getHostAddress();
        final String request;
        final byte[] requestBytes;
        try {
            request = UdpProxyFinderStrings.DiscoverProxyRobotsRequest + " " + execReadToString("hostname").trim();
            requestBytes = request.getBytes();
        } catch (IOException e) {
            logback.error("Failed to execute hostname command", e);
            return;
        }

        final DatagramPacket datagramPacket;
        try {
            datagramPacket = new DatagramPacket(requestBytes, requestBytes.length,
                                                InetAddress.getByName(broadcastAddressString), broadcastPort);
        } catch (UnknownHostException e) {
            logback.error("Failed to get broadcast address by name: " + broadcastAddressString, e);
            return;
        }

        try {
            datagramSocket.send(datagramPacket);
        } catch (final Exception e) {
            logback.error(
                    "Address " + datagramPacket.getAddress() + " Port " + datagramPacket.getPort() + " SocketAddr " +
                    datagramPacket.getSocketAddress() + " Data " + Arrays.toString(datagramPacket.getData()), e);
            return;
        }
        logback.info("Request packet" + " [" + request + "] " + "sent to: " + broadcastAddress.getHostAddress());
    }

    /**
     * @return true if UdpProxyFinder has found the server and it returned correct data
     */
    private boolean hasFoundServer() {
        return udpBroadcastDataDecoder.hasReceivedCorrectData();
    }

    /**
     * @return null if broadcast was not called first
     * otherwise return puller address of the server (push on proxy side)
     */
    public String getPushAddress() {
        return pushAddress;
    }

    /**
     * @return null if broadcast was not called first
     * otherwise return publisher address of the server (subscribe on proxy side)
     */
    public String getPullAddress() {
        return pullAddress;
    }

    private static String execReadToString(String execCommand) throws IOException {
        Process process = Runtime.getRuntime().exec(execCommand);
        try (InputStream stream = process.getInputStream()) {
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        }
    }
}
