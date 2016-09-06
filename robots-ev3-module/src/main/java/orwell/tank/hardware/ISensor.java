package orwell.tank.hardware;

import lejos.mf.common.UnitMessageType;

/**
 * Created by Michaël Ludmann on 6/17/15.
 */
public interface ISensor<T> {

    T get();

    void readValue();

    long getReadValueInterval();

    void close();

    UnitMessageType getType();
}
