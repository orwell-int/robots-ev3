package orwell.tank.communication;

import org.junit.Test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class UdpBroadcastDataDecoderTest {
    @Test
    public void parseFrom() throws UnknownHostException {
        final int checkByteSTX = 0xA2;
        final int checkByteSeparator = 0xA3;
        final int checkByteETX = 0xA4;
        final int pushPort = 10000;
        final int pullPort = 10001;
        final String robotPushAddress = "tcp://*:" + pushPort;
        final String robotPullAddress = "tcp://*:" + pullPort;
        byte[] sendData = new byte[5 + robotPushAddress.length() + robotPullAddress.length()];
        int index = 0;
        sendData[index++] = (byte) checkByteSTX;
        sendData[index++] = (byte) robotPushAddress.length();
        System.arraycopy(robotPushAddress.getBytes(), 0, sendData, index, robotPushAddress.length());
        index += robotPushAddress.length();
        sendData[index++] = (byte) checkByteSeparator;
        sendData[index++] = (byte) robotPullAddress.length();
        System.arraycopy(robotPullAddress.getBytes(), 0, sendData, index, robotPullAddress.length());
        index += robotPullAddress.length();
        sendData[index++] = (byte) checkByteETX;
        //Send a response
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost"), 42);
        UdpBroadcastDataDecoder udpBroadcastDataDecoder = new UdpBroadcastDataDecoder();
        udpBroadcastDataDecoder.parseFrom(sendPacket);
        assertEquals("tcp://127.0.0.1:10000", udpBroadcastDataDecoder.getPushAddress());
        assertEquals("tcp://127.0.0.1:10001", udpBroadcastDataDecoder.getPullAddress());
    }

}