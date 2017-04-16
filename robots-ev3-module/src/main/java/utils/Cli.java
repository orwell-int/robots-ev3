package utils;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotColourConfigIniFile;
import orwell.tank.config.RobotIniFile;

import java.io.IOException;
import java.util.Arrays;

public class Cli {
    private final static Logger logback = LoggerFactory.getLogger(Cli.class);
    private final Options options = new Options();
    private String[] args = null;

    public Cli(final String[] args) {
        this.args = args;

        final Option optionHelp = new Option("h", "help", false, "shows help and exits program.");
        options.addOption(optionHelp);

        // Add a new optionGroup to make --file
        final Option optionRobotIniFile = new Option("rf", "robotfile", true, "filepath for external robot configuration file");
        final OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(false);
        optionGroup.addOption(optionRobotIniFile);
        options.addOptionGroup(optionGroup);

        final Option optionColourIniFile = new Option("cf", "colourfile", true, "filepath for external colour configuration file");
        final OptionGroup optionGroup2 = new OptionGroup();
        optionGroup2.setRequired(true);
        optionGroup2.addOption(optionColourIniFile);
        options.addOptionGroup(optionGroup2);
    }

    /**
     * Extract the orwell.tank.config parameters used later by the ConfigFactory
     *
     * @return null if help is called or error happens during parsing
     */
    public IniFiles parse() {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                return help();

            String robotIniFileName = "";
            if (cmd.hasOption("rf")) {
                robotIniFileName = cmd.getOptionValue("rf");
            }

            String colourConfigIniFileName = "";
            if (cmd.hasOption("cf")) {
                colourConfigIniFileName = cmd.getOptionValue("cf");
            }

            IniFiles iniFiles = new IniFiles(robotIniFileName, colourConfigIniFileName);

            if (!iniFiles.isEmpty()) {
                return iniFiles;
            }

            if (cmd.hasOption("rf") && cmd.hasOption("cf")) {
                logback.warn(iniFiles.toString());
                return help();
            } else if (args.length > 0) {
                logback.warn("There is an unknown parameter among the following ones: " + Arrays.toString(args));
                return help();
            } else {
                return help();
            }

        } catch (final ParseException e) {
            logback.error("Failed to parse command line properties", e);
            return help();
        }
    }

    private IniFiles help() {
        // This prints out some help
        final HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("-f filepath", options);
        return null;
    }
}
