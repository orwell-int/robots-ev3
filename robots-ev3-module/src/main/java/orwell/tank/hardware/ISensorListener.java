package orwell.tank.hardware;

import lejos.mf.common.UnitMessageType;

import java.util.ArrayList;

/**
 * Created by Michaël Ludmann on 6/16/15.
 */
public interface ISensorListener {

    void receivedNewValue(UnitMessageType messageType, String newValue);

    void add(ArrayList<ISensor> sensors);
}
