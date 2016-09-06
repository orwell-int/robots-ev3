package orwell.tank.hardware;

import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 06/09/16.
 */
public class ThreadedSensor<T> implements Runnable {
    private final static Logger logback = LoggerFactory.getLogger(ThreadedSensor.class);
    private final long SENSOR_READ_VALUE_INTERVAL;
    private T lastValue = null;

    private ISensor<T> sensor;
    private volatile boolean shouldRun;

    public ThreadedSensor(ISensor<T> sensor) {
        this.sensor = sensor;
        SENSOR_READ_VALUE_INTERVAL = sensor.getReadValueInterval();
    }

    public T get() {
        lastValue = sensor.get();
        return lastValue;
    }

    public void start() {
        shouldRun = true;
        new Thread(this).start();
    }

    public void stop() {
        shouldRun = false;
    }

    public void close() {
        stop();
        sensor.close();
    }

    @Override
    public void run() {
        while (shouldRun) {
            sensor.readValue();
            if (SENSOR_READ_VALUE_INTERVAL > 0) {
                try {
                    Thread.sleep(SENSOR_READ_VALUE_INTERVAL);
                } catch (InterruptedException e) {
                    logback.error(e.getMessage());
                }
            }
        }
    }

    public boolean hasUpdate() {
        T newValue = sensor.get();
        return !newValue.equals(lastValue);
    }

    public UnitMessageType getType() {
        return sensor.getType();
    }
}
