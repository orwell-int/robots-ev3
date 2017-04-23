package orwell.tank.hardware.Camera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Camera implements Runnable {
    private final static Logger logback = LoggerFactory.getLogger(Camera.class);
    private static final long THREAD_SLEEP_BETWEEN_RUNS_MS = 5;

    private final String startCameraScriptAddress;
    private final String killCameraScriptAddress;
    private volatile boolean shouldRun;
    private Process runningProcess;

    public Camera(final String startCameraScriptAddress, final String killCameraScriptAddress) {
        this.startCameraScriptAddress = startCameraScriptAddress;
        this.killCameraScriptAddress = killCameraScriptAddress;
    }

    public void start() {
        shouldRun = true;
        new Thread(this).start();
    }

    public void stop() {
        shouldRun = false;
        if (runningProcess != null) {
            runningProcess.destroy();
        }
        try {
            Runtime.getRuntime().exec(killCameraScriptAddress);
        } catch (IOException e) {
            logback.error(e.getStackTrace().toString());
        }
        logback.info("Camera stopped");
    }

    @Override
    public void run() {
        while (shouldRun) {
            try {
                logback.info("Camera process will now start");
                runningProcess = Runtime.getRuntime().exec(startCameraScriptAddress);
                runningProcess.waitFor();
                try {
                    Thread.sleep(THREAD_SLEEP_BETWEEN_RUNS_MS);
                } catch (InterruptedException e) {
                }
            } catch (IOException e) {
                logback.error(e.getStackTrace().toString());
            } catch (InterruptedException e) {
                logback.error("Camera thread interrupted: " + e.getStackTrace().toString());
            }
        }
    }
}
