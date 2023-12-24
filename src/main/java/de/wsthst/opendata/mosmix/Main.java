package de.wsthst.opendata.mosmix;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.ZoneOffset.UTC;

public final class Main {

    private String[] stationIds;
    private Path kmlFile;
    private Path outFolder;

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    private void run(String[] args) throws Exception {
        parseCommandLine(args);

        Instant modelRunTime = parseModelRuntime();
        List<PointTimeForecast> ptfcs;
        try (InputStream kmlStream = new BufferedInputStream(Files.newInputStream(kmlFile))) {
            ptfcs = new MosmixKmlReader().read(kmlStream, modelRunTime, stationIds);
        }

        for (PointTimeForecast ptfc : ptfcs) {
            if (outFolder != null) {
                Path outFile = outFolder.resolve("mosmix_" + ptfc.getStationId() + ".csv");
                try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(outFile, StandardCharsets.UTF_8))) {
                    new CsvWriter().write(ptfc, out);
                }
            } else {
                System.out.println(ptfc.getStationId());
                PrintWriter out = new PrintWriter(System.out);
                new CsvWriter().write(ptfc, out);
                out.println("");
                out.flush();
            }
        }
    }

    private Instant parseModelRuntime() {
        String modelrunTimeString = kmlFile.getFileName().toString();
        if(modelrunTimeString.contains(File.separator)) modelrunTimeString = StringUtils.substringAfterLast(modelrunTimeString, File.separator);
        modelrunTimeString = StringUtils.split(modelrunTimeString, '_')[2];
        return LocalDateTime.parse(modelrunTimeString, DateTimeFormatter.ofPattern("yyyyMMddHH")).atZone(UTC).toInstant();
    }

    private void parseCommandLine(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder()
            .longOpt("kml")
            .argName("KML File")
            .desc("MOSMIX KML file, underscore delimites. Model run time yyyyMMddHH has to be at third position.")
            .hasArg()
            .required()
            .build());
        options.addOption(Option.builder()
            .longOpt("stations")
            .argName("station1,station2,...")
            .desc("Comma delimited station identifiers, whose data will be extracted.")
            .hasArg()
            .required()
            .build());
        options.addOption(Option.builder()
            .longOpt("out")
            .argName("Output directory")
            .desc("Output directory for the CSV file, else output to console standard out.")
            .hasArg()
            .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.out.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("mosmix-kml-tool", options, true);
            System.exit(-1);
        }

        stationIds = cmdLine.getOptionValue("stations").split(",");
        kmlFile = resolve(cmdLine.getOptionValue("kml"));
        if (cmdLine.hasOption("out")) outFolder = resolve(cmdLine.getOptionValue("out"));
    }

    private Path resolve(String filename) {
        Path p = Paths.get(filename);
        if (p.isAbsolute()) return p;
        return Paths.get(System.getProperty("user.dir")).resolve(p);
    }

}
