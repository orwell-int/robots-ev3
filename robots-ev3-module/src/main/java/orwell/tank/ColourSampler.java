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
import orwell.tank.exception.ParseIniException;
import orwell.tank.exception.FileBomException;
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

/**
 * Created by Michaël Ludmann on 27/11/16.
 */
public class ColourSampler extends Thread {
    private final static Logger logback = LoggerFactory.getLogger(ColourSampler.class);
    private static final long THREAD_SLEEP_BETWEEN_SAMPLES_MS = 1;
    private static final String SAMPLES_FILE_PATH = "/home/root/samples.csv";
    private static final String COLOURS_CONFIG_FILE_PATH = "/home/root/colours.config.ini";
    private static final float SIGMA_FACTOR = 5;
    private final RobotFileBom robotBom;
    private boolean isListening = false;
    private boolean ready = false;
    private EV3ColorSensor colourSensor;
    private Path samplesPath;
    private Path coloursConfigPath;
    private EnumColours colorMode = EnumColours.NONE;
    private EnumRegisterMode registerMode = EnumRegisterMode.OFF;
    private ArrayList<Float> redArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private ArrayList<Float> greenArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private ArrayList<Float> blueArray = new ArrayList<>(TOTAL_SAMPLE_SIZE);
    private static final int TOTAL_SAMPLE_SIZE = 20000;
    private SensorMode sensorMode;
    private float redAverage;
    private float greenAverage;
    private float blueAverage;
    private float redSigma;
    private float greenSigma;
    private float blueSigma;

    public ColourSampler(RobotFileBom robotBom) {
        this.robotBom = robotBom;
        initColour(robotBom.getColorSensorPort());
        initFiles();
        ready = true;
        Sound.twoBeeps();
        Button.ESCAPE.addKeyListener(new EscapeListener());
        Button.RIGHT.addKeyListener(new ColorModeListener());
        Button.ENTER.addKeyListener(new RegisterModeListener());
    }

    public static void main(String[] args) throws IOException {
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
            logback.error("Failed to parse the ini file. Exiting now");
        } catch (FileBomException e) {
            logback.error(e.getMessage());
        }
    }

    private void initFiles() {
        deleteAndCreateFile(SAMPLES_FILE_PATH);
        deleteAndCreateFile(COLOURS_CONFIG_FILE_PATH);

        samplesPath = Paths.get(SAMPLES_FILE_PATH);
        coloursConfigPath = Paths.get(COLOURS_CONFIG_FILE_PATH);

        writeGlobalConfig();
    }

    private void deleteAndCreateFile(String samplesFilePath) {
        File file = new File(samplesFilePath);
        if(file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            logback.error(e.getMessage());
            quit();
        }
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

    public void run() {
        logback.info("Start running ColourSampler");
        try {
            startSamplingLoop();
        } catch (Exception e) {
            logback.error(e.getMessage());
        }
        dispose();
        Thread.yield();
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
            logback.error("Exception during RemoteRobot run: " + e.getMessage());
        }
    }

    private void saveSample() {
        if (registerMode == EnumRegisterMode.ON) {
            float samples[] = new float[sensorMode.sampleSize()];
            sensorMode.fetchSample(samples, 0);
            if (samples.length != 3) {
                logback.error("Color sample has an invalid length of " + samples.length);
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
                logback.error(e.getMessage());
            }

            if(redArray.size() >= TOTAL_SAMPLE_SIZE) {
                logback.info("Total sample size reached");
                finalizeColour();
            }
        }
    }

    private void finalizeColour() {
        registerMode = EnumRegisterMode.OFF;

        if(!redArray.isEmpty() && !greenArray.isEmpty() && !blueArray.isEmpty()) {
            computeAverages();
            computeSigmas();
            redArray.clear();
            greenArray.clear();
            blueArray.clear();
            writeColorConfig();
        }
        colorMode = colorMode.next();

        logback.info("Color to register: " + colorMode.toString());
        String data = colorMode + System.lineSeparator();
        try {
            Files.write(samplesPath, data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error(e.getMessage());
            quit();
        }
        Sound.beepSequenceUp();
    }

    private void writeColorConfig() {
        String colorLine = "[" + colorMode + "]" + System.lineSeparator();
        String averagesLines = 
                    "averageRed = " + redAverage + System.lineSeparator() +
                    "averageGreen = " + greenAverage + System.lineSeparator() +
                    "averageBlue = " + blueAverage + System.lineSeparator();
        String sigmasLines =
                "sigmaRed = " + redSigma + System.lineSeparator() +
                "sigmaGreen = " + greenSigma + System.lineSeparator() +
                "sigmaBlue = " + blueSigma + System.lineSeparator();
        try {
            Files.write(coloursConfigPath, (colorLine + averagesLines + sigmasLines).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error(e.getMessage());
            quit();
        }
    }

    private void writeGlobalConfig() {
        String globalLine = "[global]" + System.lineSeparator();
        String sigmaFactorLine = "sigmaFactor = " + SIGMA_FACTOR + System.lineSeparator();
        try {
            Files.write(coloursConfigPath, (globalLine + sigmaFactorLine).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logback.error(e.getMessage());
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
            sum+= Math.pow(value - average, 2);
        }
        return (float) Math.sqrt(sum/array.size());
    }

    private void computeAverages() {
        redAverage = computeAverage(redArray);
        greenAverage = computeAverage(greenArray);
        blueAverage = computeAverage(blueArray);
    }

    private float computeAverage(ArrayList<Float> array) {
        float sum = 0f;
        for (Float value : array) {
            sum+= value;
        }
        return sum/array.size();
    }

    private void sleepBetweenSamples() {
        try {
            sleep(THREAD_SLEEP_BETWEEN_SAMPLES_MS);
        } catch (InterruptedException e) {
            logback.error(e.getMessage());
        }
    }

    public boolean isReady() {
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

        private static EnumRegisterMode[] vals = values();

        public EnumRegisterMode next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    private class EscapeListener implements KeyListener {

        public void keyPressed(Key k) {
        }

        public void keyReleased(Key k) {
            isListening = false;
        }
    }

    private class ColorModeListener implements KeyListener {
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
            logback.info("Is logging data: " + registerMode.toString());
        }
    }
}
