package orwell.tank.messaging;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.actions.*;
import utils.Splice;
import utils.Split;

import java.util.List;

/**
 * Created by Michaël Ludmann on 10/07/16.
 */
public class UnitMessageDecoderFactory {
    private final static Logger logback = LoggerFactory.getLogger(UnitMessageDecoderFactory.class);

    public static IInputAction parseFrom(UnitMessage message) {
        if (isNotHandled(message)) {
            return new NotHandled(null);
        }

        List<String> payloadArray = Split.split(' ', message.getPayload());
        if (payloadArray.isEmpty())
            return new NotHandled(null);
        String payloadHeader = payloadArray.get(0);
        List<String> payloadBody;

        if (1 == payloadArray.size()) {
            payloadBody = null;
        } else {
            payloadBody = Splice.subList(payloadArray, 1, payloadArray.size());
        }

        if (UnitMessageType.Connection == message.getMessageType()) {
            return new Connection(payloadHeader);
        }

        switch (payloadHeader) {
            case "stop":
                return new StopTank(payloadBody);
            case "stopPrg":
                return new StopProgram(payloadBody);
            case "move":
                return new Move(payloadBody);
            case "fire":
                return new Fire(payloadBody);
            case "game":
                return new GameState(payloadBody);
            default:
                return new NotHandled(payloadBody);
        }
    }

    private static boolean isNotHandled(UnitMessage message) {
        return message == null ||
                (UnitMessageType.Command != message.getMessageType() &&
                        UnitMessageType.Connection != message.getMessageType());
    }
}
