package orwell.tank;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotFileBom;
import orwell.tank.exception.FileBomException;
import orwell.tank.exception.ParseIniException;
import orwell.tank.hardware.Colours.EnumColours;
import utils.Cli;
import utils.IniFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ColourSampler extends Thread {
    private static final Logger logback = LoggerFactory.getLogger(ColourSampler.class);
    private static final long THREAD_SLEEP_BETWEEN_SAMPLES_MS = 1;
    private static final String SAMPLES_FILE_PATH = "/home/root/samples.csv";
    private static final String COLOURS_CONFIG_FILE_PATH = "/home/root/colours.config.ini";
    private static final float SIGMA_FACTOR = 4;
    private static final int TOTAL_SAMPLE_SIZE = 20000;
    private boolean isListening;
    private boolean ready;
    private EV3ColorSensor colourSensor;
    private Path samplesPath;
    private Path coloursConfigPath;
    private EnumColours colourMode = EnumColours.NONE;
    private EnumRegisterMode registerMode = EnumRegisterMode.OFF;
    private final ArrayList<Float> redArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private final ArrayList<Float> greenArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private final ArrayList<Float> blueArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private SensorMode sensorMode;
    private float redAverage;
    private float greenAverage;
    private float blueAverage;
    private float redSigma;
    private float greenSigma;
    private float blueSigma;

    public ColourSampler(RobotFileBom robotBom) {
        initColour(robotBom.getColourSensorPort());
        initFiles();
        ready = true;
        Sound.twoBeeps();
        Button.ESCAPE.addKeyListener(new EscapeListener());
        Button.RIGHT.addKeyListener(new ColourModeListener());
        Button.ENTER.addKeyListener(new RegisterModeListener());
    }

    public static void main(String[] args) {
        final IniFiles iniFiles = new Cli(args).parse();
        if (iniFiles == null || iniFiles.robotIniFile == null) {
            logback.warn("Command Line Interface did not manage to extract a ini file orwell.tank.config. Exiting now.");
            System.exit(0);
        }
        try {
            final RobotFileBom robotBom = iniFiles.robotIniFile.parse();
            final ColourSampler colourSampler = new ColourSampler(robotBom);
            if (colourSampler.isReady()) {
                colourSampler.start();
            }
        } catch (ParseIniException e) {
            logback.error("Failed to parse the ini file. Exiting now", e);
        } catch (FileBomException e) {
            logback.error("File BOM read exception", e);
        }
    }

    private void initFiles() {
        deleteAndCreateFile(SAMPLES_FILE_PATH);
        deleteAndCreateFile(COLOURS_CONFIG_FILE_PATH);

        samplesPath = Paths.get(SAMPLES_FILE_PATH);
        coloursConfigPath = Paths.get(COLOURS_CONFIG_FILE_PATH);

        writeGlobalConfig();
    }

    public void run() {
        logback.info("Start running ColourSampler");
        try {
            startSamplingLoop();
        } catch (Exception e) {
            logback.error("Colour sampler thread run exception", e);
        }
        dispose();
        Thread.yield();
    }

    private void initColour(Port colourSensorPort) {
        if (colourSensorPort == null) {
            logback.info("No Colour Sensor configured");
            return;
        }
        colourSensor = new EV3ColorSensor(colourSensorPort);
        sensorMode = colourSensor.getRGBMode();
        logback.info("Colour init Ok");
    }

    private void startSamplingLoop() {
        try {
            isListening = true;
            while (isListening) {
                saveSample();
                sleepBetweenSamples();
            }
            isListening = false;
        } catch (Exception e) {
            logback.error("Exception during RemoteRobot run", e);
        }
    }

    private void saveSample() {
        if (registerMode == EnumRegisterMode.ON) {
            float samples[] = new float[sensorMode.sampleSize()];
            sensorMode.fetchSample(samples, 0);
            if (samples.length != 3) {
                logback.error("Colour sample has an invalid length of " + samples.length);
            }
            float red = samples[0];
            float green = samples[1];
            float blue = samples[2];

            redArray.add(red);
            greenArray.add(green);
            blueArray.add(blue);

            String data = red + "," + green + "," + blue + System.lineSeparator();
            try {
                Files.write(samplesPath, data.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                logback.error("Exception while saving colour sample file", e);
            }

            if (redArray.size() >= TOTAL_SAMPLE_SIZE) {
                logback.info("Total sample size reached");
                finalizeColour();
            }
        }
    }

    private void sleepBetweenSamples() {
        try {
            sleep(THREAD_SLEEP_BETWEEN_SAMPLES_MS);
        } catch (InterruptedException e) {
            logback.error("Exception while sleeping " + THREAD_SLEEP_BETWEEN_SAMPLES_MS + "ms in thread", e);
        }
    }

    private void finalizeColour() {
        registerMode = EnumRegisterMode.OFF;

        if (!redArray.isEmpty() && !greenArray.isEmpty() && !blueArray.isEmpty()) {
            computeAverages();
            computeSigmas();
            redArray.clear();
            greenArray.clear();
            blueArray.clear();
            writeColourConfig();
        }
        colourMode = colourMode.next();

        logback.info("Colour to register: " + colourMode);
        String data = colourMode + System.lineSeparator();
        try {
            Files.write(samplesPath, data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error("Exception while saving colour file", e);
            quit();
        }
        Sound.beepSequenceUp();
    }

    private void writeColourConfig() {
        String colourLine = "[" + colourMode + "]" + System.lineSeparator();
        String averagesLines =
                "averageRed = " + redAverage + System.lineSeparator() +
                        "averageGreen = " + greenAverage + System.lineSeparator() +
                        "averageBlue = " + blueAverage + System.lineSeparator();
        String sigmasLines =
                "sigmaRed = " + redSigma + System.lineSeparator() +
                        "sigmaGreen = " + greenSigma + System.lineSeparator() +
                        "sigmaBlue = " + blueSigma + System.lineSeparator();
        try {
            Files.write(coloursConfigPath, (colourLine + averagesLines + sigmasLines).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error("Exception while writing colour config file", e);
            quit();
        }
    }

    private void deleteAndCreateFile(String samplesFilePath) {
        File file = new File(samplesFilePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            logback.error("File creation exception", e);
            quit();
        }
    }

    private void quit() {
        dispose();
        System.exit(0);
    }

    private void computeSigmas() {
        redSigma = computeSigma(redArray, redAverage);
        greenSigma = computeSigma(greenArray, greenAverage);
        blueSigma = computeSigma(blueArray, blueAverage);
    }

    private float computeSigma(ArrayList<Float> array, float average) {
        float sum = 0f;
        for (Float value : array) {
            sum += Math.pow(value - average, 2);
        }
        return (float) Math.sqrt(sum / array.size());
    }

    private void computeAverages() {
        redAverage = computeAverage(redArray);
        greenAverage = computeAverage(greenArray);
        blueAverage = computeAverage(blueArray);
    }

    private float computeAverage(ArrayList<Float> array) {
        float sum = 0f;
        for (Float value : array) {
            sum += value;
        }
        return sum / array.size();
    }

    private void writeGlobalConfig() {
        String globalLine = "[global]" + System.lineSeparator();
        String sigmaFactorLine = "sigmaFactor = " + SIGMA_FACTOR + System.lineSeparator();
        try {
            Files.write(coloursConfigPath, (globalLine + sigmaFactorLine).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error("Exception while writing global colour config file", e);
            quit();
        }
    }

    private boolean isReady() {
        return ready;
    }

    private void dispose() {
        Sound.buzz();
        closeHardware();
        ready = false;
    }

    private void closeHardware() {
        colourSensor.close();
    }

    private enum EnumRegisterMode {
        OFF, ON;

        private static final EnumRegisterMode[] values = values();

        public EnumRegisterMode next() {
            return values[(ordinal() + 1) % values.length];
        }
    }

    private class EscapeListener implements KeyListener {

        public void keyPressed(Key k) {
        }

        public void keyReleased(Key k) {
            isListening = false;
        }
    }

    private class ColourModeListener implements KeyListener {
        @Override
        public void keyPressed(Key k) {

        }

        @Override
        public void keyReleased(Key k) {
            finalizeColour();
        }
    }

    private class RegisterModeListener implements KeyListener {
        @Override
        public void keyPressed(Key k) {

        }

        @Override
        public void keyReleased(Key k) {
            registerMode = registerMode.next();
            logback.info("Is logging data: " + registerMode);
        }
    }
}
