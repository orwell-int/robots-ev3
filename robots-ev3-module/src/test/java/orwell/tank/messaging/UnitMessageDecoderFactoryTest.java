package orwell.tank.messaging;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.actions.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Michaël Ludmann on 28/08/16.
 */
@RunWith(JUnit4.class)
public class UnitMessageDecoderFactoryTest {
    private final static Logger logback = LoggerFactory.getLogger(UnitMessageDecoderFactoryTest.class);

    @Test
    public void testParseFrom_Move() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "move 0.50 1.00");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(Move.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_Fire() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "fire true false");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(Fire.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_Stop() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "stop");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(StopTank.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_StopProgram() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "stopPrg");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(StopProgram.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_GameState() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "game");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(GameState.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_NotHandled() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "anything else");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(NotHandled.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_NotHandled_Empty() throws Exception {
        UnitMessage unitMessage = new UnitMessage(UnitMessageType.Command, "");
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(NotHandled.class, inputAction.getClass());
    }

    @Test
    public void testParseFrom_NotHandled_Null() throws Exception {
        UnitMessage unitMessage = null;
        IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);

        assertEquals(NotHandled.class, inputAction.getClass());
    }
}
