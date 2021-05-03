package io.benlewis.wtw;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Application {

    static final Logger logger = Logger.getLogger(Application.class);

    private final File input;
    private final File output;
    private Map<String, ClaimsBlock> blocks;
    private int earliestOriginYear;
    private int greatestSpan;

    /**
     * Construct Application with input and output file paths.
     * @param inputPath input path
     * @param outputPath output path
     */
    public Application(String inputPath, String outputPath){

        this(new File(inputPath), new File(outputPath));

    }

    /**
     * Construct Application with input and output files.
     * @param input file
     * @param output file
     */
    public Application(File input, File output){

        this.input = input;
        this.output = output;
        // Use a Linked HashMap to preserve insertion order
        this.blocks = new LinkedHashMap<>();
        this.earliestOriginYear = Integer.MAX_VALUE;
        this.greatestSpan = 0;

    }

    /**
     * Load data from the applications input file.
     */
    public void loadInput(){

        Scanner scanner = null;

        try {

            scanner = new Scanner(input);

            // Iterate lines in input file
            while (scanner.hasNextLine()){

                // Parse each line for data
                parseData(scanner.nextLine());

            }

        }
        catch (FileNotFoundException e){

            logger.error("Could not find input file \"" + input.getName() + "\"!", e);

        }
        finally {

            // Close resources
            if (scanner != null) scanner.close();

        }

    }

    /**
     * Parse data from a formatted CSV line and add to respective claims block.
     * @param line to parse
     */
    private void parseData(String line){

        String[] data = line.split(",");

        // Ensure line is valid CSV format
        if (data.length != 4){

            logger.warn("Invalid format, discarding line: \"" + line + "\"");
            return;

        }

        try {

            // Extract data
            String product = data[0].trim();
            int originYear = Integer.parseInt(data[1].trim());
            int developmentYear = Integer.parseInt(data[2].trim());
            double payment = Double.parseDouble(data[3].trim());

            // Ensure product exists in claims blocks
            if (!blocks.containsKey(product))
                blocks.put(product, new ClaimsBlock(product));

            // Add payment to claims block
            blocks.get(product).addPayment(originYear, developmentYear, payment);

            logger.info("Loaded payment of " + payment + " from origin year " + originYear + " and development year " +
                    developmentYear + " into claims block for product " + product);

            // Update earliest origin year
            if (originYear < earliestOriginYear){
                earliestOriginYear = originYear;
                logger.info("Updated earliest origin year to " + originYear);
            }

            // Update greatest span
            int span = developmentYear - originYear + 1;
            if (span > greatestSpan){
                greatestSpan = span;
                logger.info("Updated greatest span to "  + span);
            }

        }
        catch (NumberFormatException e){

            logger.warn("Invalid data, discarding line: \"" + line + "\"");

        }

    }

    /**
     * Write out all claims blocks to applications output file.
     */
    public void writeOutput(){

        PrintWriter printer = null;

        try {

            printer = new PrintWriter(output);

            String header = earliestOriginYear + ", " + greatestSpan;
            printer.println(header);
            logger.info("Wrote header to output: " + header);

            for (ClaimsBlock block : blocks.values()){

                String output = block + ", " + block.generateTriangle(earliestOriginYear, greatestSpan).accumulate();
                printer.println(output);
                logger.info("Wrote accumulated triangle to output: " + output);

            }

        }
        catch (FileNotFoundException e){

            logger.error("Could not find output file \"" + output.getName() + "\"!", e);

        }
        finally {

            // Close resources
            if (printer != null) printer.close();

        }

    }

    public static void main(String[] args){

        Application app;

        // Construct new application based on default values or passed args
        if (args.length != 2){

            logger.warn("No arguments provided for input and output files, using defaults");
            app = new Application("input.csv", "output.csv");

        }
        else app = new Application(args[0], args[1]);

        logger.info("Input file set to " + app.input.getName() + ", output file set to " + app.output.getName());

        // Load input from file
        app.loadInput();

        // Write calculated output to file
        app.writeOutput();

    }

    /**
     * Test harness.
     */
    public static void test(){

        Application app = new Application("input.csv", "output.csv");

        app.parseData("hello,world");

        app.parseData("Non-Comp, 1990, 19t90, 45.2");

        app.loadInput();

        app.writeOutput();

    }

}
