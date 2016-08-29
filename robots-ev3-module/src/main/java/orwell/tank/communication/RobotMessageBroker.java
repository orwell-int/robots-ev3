package orwell.tank.communication;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageBuilder;
import lejos.mf.common.exception.UnitMessageException;
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
    private final String proxyIpAddress;
    private ZMQ.Context context;
    private ZMQ.Socket sender;
    private ZMQ.Socket receiver;
    private String senderConnectAddress;
    private String receiverConnectAddress;

    public RobotMessageBroker(String proxyIpAddress, int pushPort, int pullPort) {
        this.proxyIpAddress = proxyIpAddress;
        this.pushPort = pushPort;
        this.pullPort = pullPort;

        setSenderConnectAddress(proxyIpAddress, pushPort);
        setReceiverConnectAddress(proxyIpAddress, pullPort);

        initializeContext();
    }

    private void setSenderConnectAddress(String proxyIpAddress, int pushPort) {
        senderConnectAddress = "tcp://" + proxyIpAddress + ":" + pushPort;
    }

    private void setReceiverConnectAddress(String proxyIpAddress, int pullPort) {
        receiverConnectAddress = "tcp://" + proxyIpAddress + ":" + pullPort;
    }

    private void initializeContext() {
        context = ZMQ.context(1);
        sender = context.socket(ZMQ.PUSH);
        receiver = context.socket(ZMQ.PULL);

        sender.setLinger(1000);
        receiver.setLinger(1000);
    }

    public void connect() {
        logback.info("Robot is starting connection on ports " + pushPort + " (push) and " + pullPort + " (pull)");
        sender.connect(senderConnectAddress);
        receiver.connect(receiverConnectAddress);
        logback.debug("Robot is ready for incoming proxy connection " + proxyIpAddress + " !");

    }

    public void disconnect() {
        sender.disconnect(senderConnectAddress);
        receiver.disconnect(receiverConnectAddress);
        sender.close();
        receiver.close();
        context.close();
        logback.info("Robot is now disconnected from proxy");
    }

    public UnitMessage receivedNewMessage() throws UnitMessageException {
        String msg = receiver.recvStr(1); // do not block thread waiting for a message
        if (msg != null) {
            logback.debug("Message received: " + msg);
        }
        return UnitMessageBuilder.build(msg);
    }

    public void sendMessage(UnitMessage message) {
        logback.debug("Sending" + message.getMessageType() + " message to proxy");
        sender.send(message.toString());
    }
}
