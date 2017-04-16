package orwell.tank.messaging;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import lejos.mf.common.constants.UnitMessagePayloadHeaders;
import orwell.tank.actions.*;
import utils.Splice;
import utils.Split;

import java.util.List;

public class UnitMessageDecoderFactory {

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
            case UnitMessagePayloadHeaders.Stop:
                return new StopTank(payloadBody);
            case UnitMessagePayloadHeaders.StopProgram:
                return new StopProgram(payloadBody);
            case UnitMessagePayloadHeaders.MoveAction:
                return new Move(payloadBody);
            case UnitMessagePayloadHeaders.FireAction:
                return new Fire(payloadBody);
            case UnitMessagePayloadHeaders.GameState:
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
