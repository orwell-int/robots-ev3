package utils;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotColourConfigIniFile;
import orwell.tank.config.RobotIniFile;

import java.io.IOException;

/**
 * Created by Michaël Ludmann on 09/09/16.
 */
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

            IniFiles iniFiles = new IniFiles();

            if (cmd.hasOption("rf")) {
                iniFiles.robotIniFile = robotConfigurationFromFile(cmd.getOptionValue("rf"));
            }

            if (cmd.hasOption("cf")) {
                iniFiles.colourConfigIniFile = colourConfigurationFromFile(cmd.getOptionValue("cf"));
            }

            if (!iniFiles.isEmpty()) {
                return iniFiles;
            }

            if (args.length > 0) {
                logback.warn("Unknown parameter: " + args[0]);
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

    private RobotIniFile robotConfigurationFromFile(final String filePath) {
        try {
            return new RobotIniFile(filePath);
        } catch (final IOException e) {
            logback.error(e.getMessage());
            return null;
        }
    }

    private RobotColourConfigIniFile colourConfigurationFromFile(final String filePath) {
        try {
            return new RobotColourConfigIniFile(filePath);
        } catch (final IOException e) {
            logback.error(e.getMessage());
            return null;
        }
    }
}
