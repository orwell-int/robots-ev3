package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BroadcastAddress {
    public static InetAddress getBroadcastAddress(InetAddress ipAddress, InetAddress subnetMask) {
        byte[] address = ipAddress.getAddress();
        byte[] subnet = subnetMask.getAddress();
        if (-1 == subnet[3]) {
            return subnetMask;
        }
        if (address.length != subnet.length) {
            throw new IllegalArgumentException("Both IP address and subnet mask should be of the same length");
        }
        byte[] result = new byte[address.length];
        for (int i = 0; i < result.length; i++)
            result[i] = (byte)(address[i] | (subnet[i] ^ 255));

        try {
            return InetAddress.getByAddress(result);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
