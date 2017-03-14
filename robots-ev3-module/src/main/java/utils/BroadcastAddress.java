package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Michaël Ludmann on 12/03/17.
 */
public class BroadcastAddress {

    public static InetAddress getBroadcastAddress(InetAddress ipAddress, InetAddress subnetMask)
    {
        byte[] address = ipAddress.getAddress();
        byte[] subnet = subnetMask.getAddress();
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
