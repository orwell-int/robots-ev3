package orwell.tank.hardware;

import lejos.hardware.Battery;
import lejos.mf.common.UnitMessageType;

/**
 * Created by Michaël Ludmann on 27/11/16.
 */
public class BatteryInfo implements ISensor<String> {
    private static final String BATTERY_MESSAGE_SPLIT_CHAR = " ";
    private static final long READ_VALUE_INTERVAL = 10000;
    private SensorMeasure<String> sensorMeasure = new SensorMeasure<>();

    @Override
    public String get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        int voltageMilliVolt = Battery.getVoltageMilliVolt();
        float batteryCurrentAmps = Battery.getBatteryCurrent();
        float motorCurrentAmps = Battery.getMotorCurrent();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(voltageMilliVolt);
        stringBuilder.append(BATTERY_MESSAGE_SPLIT_CHAR);
        stringBuilder.append(batteryCurrentAmps);
        stringBuilder.append(BATTERY_MESSAGE_SPLIT_CHAR);
        stringBuilder.append(motorCurrentAmps);

        sensorMeasure.set(stringBuilder.toString());
    }

    @Override
    public long getReadValueInterval() {
        return READ_VALUE_INTERVAL;
    }

    @Override
    public void close() {

    }

    @Override
    public UnitMessageType getType() {
        return UnitMessageType.Battery;
    }
}
