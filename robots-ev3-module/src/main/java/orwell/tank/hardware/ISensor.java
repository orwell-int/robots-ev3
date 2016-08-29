package orwell.tank.hardware;

/**
 * Created by Michaël Ludmann on 6/17/15.
 */
public interface ISensor {

    void addSensorListener(ISensorListener sensorListener);

    void startListen();

    void stopListen();
}
