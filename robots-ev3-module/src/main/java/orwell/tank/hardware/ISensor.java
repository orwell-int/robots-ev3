package orwell.tank.hardware;

import lejos.mf.common.UnitMessageType;

public interface ISensor<T> {

    T get();

    void readValue();

    long getReadValueInterval();

    void close();

    UnitMessageType getType();
}
