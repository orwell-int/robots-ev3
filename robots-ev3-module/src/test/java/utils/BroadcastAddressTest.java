package utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

/**
 * Created by Michaël Ludmann on 12/03/17.
 */
@RunWith(JUnit4.class)
public class BroadcastAddressTest {
    private final static Logger logback = LoggerFactory.getLogger(BroadcastAddress.class);

    @Test
    public void getBroadcastAddress() throws Exception {
        InetAddress ip = InetAddress.getByName("192.168.0.17");
        InetAddress mask = InetAddress.getByName("255.255.255.0");

        InetAddress broadcast = BroadcastAddress.getBroadcastAddress(ip, mask);

        assertEquals("192.168.0.255", broadcast.getHostAddress());
    }
}
