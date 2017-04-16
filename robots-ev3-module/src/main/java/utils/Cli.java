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
    private static final String COLOUR_INI_FILE_OPTION = "cf";
    private static final String ROBOT_INI_FILE_OPTION = "rf";
    private static final String HELP_OPTION = "h";
    private final Options options = new Options();
    private String[] args = null;

    public Cli(final String[] args) {
        this.args = args;

        final Option optionHelp = new Option(HELP_OPTION, "help", false, "shows help and exits program.");
        options.addOption(optionHelp);

        // Add a new optionGroup to make --file
        final Option optionRobotIniFile = new Option(ROBOT_INI_FILE_OPTION, "robotfile", true, "filepath for external robot configuration file");
        final OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(false);
        optionGroup.addOption(optionRobotIniFile);
        options.addOptionGroup(optionGroup);

        final Option optionColourIniFile = new Option(COLOUR_INI_FILE_OPTION, "colourfile", true, "filepath for external colour configuration file");
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

            if (cmd.hasOption(HELP_OPTION)) {
                help();
                return null;
            }

            String robotIniFileName = "";
            if (cmd.hasOption(ROBOT_INI_FILE_OPTION)) {
                robotIniFileName = cmd.getOptionValue(ROBOT_INI_FILE_OPTION);
            }

            String colourConfigIniFileName = "";
            if (cmd.hasOption(COLOUR_INI_FILE_OPTION)) {
                colourConfigIniFileName = cmd.getOptionValue(COLOUR_INI_FILE_OPTION);
            }

            IniFiles iniFiles = new IniFiles(robotIniFileName, colourConfigIniFileName);

            if (!iniFiles.isEmpty()) {
                return iniFiles;
            }

            if (cmd.hasOption(ROBOT_INI_FILE_OPTION) && cmd.hasOption(COLOUR_INI_FILE_OPTION)) {
                logback.warn(iniFiles.toString());
            } else if (args.length > 0) {
                logback.warn("There is an unknown parameter among the following ones: " + Arrays.toString(args));
            }

        } catch (final ParseException e) {
            logback.error("Failed to parse command line properties", e);
        }

        help();
        return null;
    }

    private void help() {
        // This prints out some help
        final HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("-f filepath", options);
    }
}
