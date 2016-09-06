package orwell.tank.hardware;

/**
 * Created by Michaël Ludmann on 06/09/16.
 */
public class SensorMeasure<T> {
    private volatile T value = null;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
