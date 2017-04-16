package utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BroadcastAddressTest {
    @Test
    public void getBroadcastAddress() throws Exception {
        InetAddress ip = InetAddress.getByName("192.168.2.33");
        InetAddress mask = InetAddress.getByName("255.255.255.0");

        InetAddress broadcast = BroadcastAddress.getBroadcastAddress(ip, mask);

        assertEquals("192.168.2.255", broadcast.getHostAddress());
    }

    @Test
    public void getBroadcastAddress_WithRealBroadcastAddress() throws Exception {
        InetAddress ip = InetAddress.getByName("192.168.2.33");
        InetAddress mask = InetAddress.getByName("192.168.2.255");

        InetAddress broadcast = BroadcastAddress.getBroadcastAddress(ip, mask);

        assertEquals("192.168.2.255", broadcast.getHostAddress());
    }
}
