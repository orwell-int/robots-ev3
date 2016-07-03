package communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * Created by Michaël Ludmann on 03/07/16.
 */
public class RobotMessageBroker {
    private final static Logger logback = LoggerFactory.getLogger(RobotMessageBroker.class);

    private final int pushPort;
    private final int pullPort;
    private final ZMQ.Context context;
    private final ZMQ.Socket sender;
    private final ZMQ.Socket receiver;
    private final String proxyIpAddress;

    public RobotMessageBroker(String proxyIpAddress, int pushPort, int pullPort) {
        this.proxyIpAddress = proxyIpAddress;
        this.pushPort = pushPort;
        this.pullPort = pullPort;

        context = ZMQ.context(1);
        sender = context.socket(ZMQ.PUSH);
        receiver = context.socket(ZMQ.PULL);

        sender.setLinger(1000);
        receiver.setLinger(1000);
    }

    public void connect() {
        logback.info("Robot is starting connection on ports " + pushPort + " and " + pullPort);
        sender.connect("tcp://" + proxyIpAddress + ":" + pushPort);
        receiver.connect("tcp://" + proxyIpAddress + ":" + pullPort);
        logback.debug("Robot is connected to proxy " + proxyIpAddress + " !");

    }

    public void disconnect() {
        sender.close();
        receiver.close();
        context.close();
    }

    public String receiveNewMessage() {
        String msg = receiver.recvStr(0);
        logback.debug("Message received: " + msg);
        return msg;
    }
}
