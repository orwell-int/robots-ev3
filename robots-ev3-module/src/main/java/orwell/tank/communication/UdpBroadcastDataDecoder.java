package orwell.tank.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.util.Arrays;

public class UdpBroadcastDataDecoder {
    private static final Logger logback = LoggerFactory.getLogger(UdpBroadcastDataDecoder.class);
    private static final String PROXY_IP_FIELD_NAME = Character.toString('*');
    private boolean isPacketDataCorrect = false;
    private String proxyRobotsIp;
    private String partialPusherAddress;
    private String partialPullerAddress;
    private String pushAddress;
    private String pullAddress;

    public UdpBroadcastDataDecoder() {

    }

    public void parseFrom(final DatagramPacket datagramPacket) {
        logback.debug("Broadcast response received from server of ip: " + datagramPacket.getAddress().getHostAddress());
        proxyRobotsIp = datagramPacket.getAddress().getHostAddress();
        logback.debug("Message received from server: " + new String(datagramPacket.getData()).trim());

        parsePacketData(datagramPacket.getData());

        if (isPacketDataCorrect && !isDataReceivedComplete()) {
            buildFullAddresses();
        } else {
            isPacketDataCorrect = false;
        }
    }

    /**
     * @param packetData data received from the server's beacon. Example:
     *                   0xA213tcp://*:100010xA313tcp://*:100000xA4
     *                   |   | |            |   | |            |
     *                   |   | |            |   | |            checkByteETX (hex value on one byte)
     *                   |   | |            |   | address of puller (i.e. pusher address on proxy side)
     *                   |   | |            |   size of robot puller on 8 bits
     *                   |   | |            checkByteSeparator (hex value on one byte)
     *                   |   | address of pusher (i.e. pull address on proxy side)
     *                   |   size of robot pusher on 8 bits
     *                   checkByteSTX (hex value on one byte)
     */
    private void parsePacketData(final byte[] packetData) {
        final int checkByteSTX = 0xA2;
        final int byteMask = 0xFF;
        if (checkByteSTX != ((int) packetData[0] & byteMask)) {
            logback.warn("checkByteSTX is not the one expected: " + Integer.toHexString((int) packetData[0] & byteMask));
            isPacketDataCorrect = false;
            return;
        }
        final int pusherSize = (int) packetData[1];
        final int endPusher = 2 + pusherSize;
        partialPusherAddress = new String(Arrays.copyOfRange(packetData, 2, endPusher));
        final int checkByteSeparator = 0xA3;
        if (checkByteSeparator != ((int) packetData[endPusher] & byteMask)) {
            logback.warn("checkByteSeparator is not the one expected: " + Integer.toHexString((int) packetData[endPusher] & byteMask));
            isPacketDataCorrect = false;
            return;
        }
        final int pullerSize = (int) packetData[endPusher + 1];
        final int endPuller = endPusher + 2 + pullerSize;
        partialPullerAddress = new String(Arrays.copyOfRange(packetData, endPusher + 2, endPuller));
        final int checkByteETX = 0xA4;
        if (checkByteETX != ((int) packetData[endPuller] & byteMask)) {
            logback.warn("checkByteETX is not the one expected: " + Integer.toHexString((int) packetData[endPuller] & byteMask));
            isPacketDataCorrect = false;
            return;
        }

        isPacketDataCorrect = true;

        logback.debug("Packet data correctly parsed");
    }

    private void buildFullAddresses() {
        pullAddress = partialPullerAddress.replace(PROXY_IP_FIELD_NAME, proxyRobotsIp);
        pushAddress = partialPusherAddress.replace(PROXY_IP_FIELD_NAME, proxyRobotsIp);
        logback.info(toString());
    }

    private boolean isDataReceivedComplete() {
        return (!partialPusherAddress.contains(PROXY_IP_FIELD_NAME)
                || !partialPullerAddress.contains(PROXY_IP_FIELD_NAME));
    }

    public boolean hasReceivedCorrectData() {
        return isPacketDataCorrect;
    }

    public String getProxyRobotsIp() {
        return proxyRobotsIp;
    }

    public String getPullAddress() {
        return pullAddress;
    }

    public String getPushAddress() {
        return pushAddress;
    }

    @Override
    public String toString() {
        return "UdpBroadcastDataDecoder values decoded: " +
                "[ProxyRobotsIp]     " + proxyRobotsIp + " " +
                "[PushAddress]    " + pushAddress + " " +
                "[PullAddress] " + pullAddress;
    }
}
