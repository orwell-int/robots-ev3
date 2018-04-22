package orwell.tank.hardware;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


/**
 * Created by Michaël Ludmann on 06/09/16.
 */
public class SensorMeasureTest {
    private static final Logger logback = LoggerFactory.getLogger(SensorMeasureTest.class);

    private static final Float FLOAT_VALUE = 42.0f;

    @Test
    public void testNullOnInit() {
        SensorMeasure<Float> floatMeasure = new SensorMeasure<>();
        assertNull(floatMeasure.get());
    }

    @Test
    public void testSetGet() {
        SensorMeasure<Float> floatMeasure = new SensorMeasure<>();
        floatMeasure.set(FLOAT_VALUE);
        assertEquals(FLOAT_VALUE, floatMeasure.get());
    }

    @Test
    public void testSetGet_VolatileMultithread() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int NUM_LOOPS = 100;
        int MAX_TIME_PER_LOOP = 200;
        long timeLimit = startTime + NUM_LOOPS * MAX_TIME_PER_LOOP;

        for (int i = 0; i < NUM_LOOPS; i++) {
            ThreadTest threadTest = new ThreadTest();
            threadTest.sensorMeasure.set(FLOAT_VALUE - 1);
            new Thread(threadTest).start();

            Thread.sleep(1);

            threadTest.sensorMeasure.set(FLOAT_VALUE + 1);

            while (threadTest.sensorMeasure.get() > FLOAT_VALUE) {

                long currentTime = System.currentTimeMillis();
                if (currentTime >= timeLimit) {
                    assertFalse(true);
                }
            }
        }
    }

    private static class ThreadTest implements Runnable {

        public SensorMeasure<Float> sensorMeasure;

        public ThreadTest() {
            sensorMeasure = new SensorMeasure<>();
        }


        @Override
        public void run() {
            while (sensorMeasure.get() < FLOAT_VALUE) {
            }
            sensorMeasure.set(FLOAT_VALUE);
        }
    }
}
