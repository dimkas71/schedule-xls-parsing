package ua.prettl;

import org.apache.commons.cli.*;

public class CommonsCliDemo {
    public static void main(String[] args) {
        System.out.println("Fuck off world");

        Options options = new Options();

        options.addOption(
                Option.builder("t")
                            .argName("t")
                            .longOpt("long option")
                            .desc("description")
                            .required(false)
                            .build());

        CommandLineParser clp = new DefaultParser();

        String[] args1 = {"-t", "value"};

        try {
            CommandLine cl = clp.parse(options, args1);

            System.out.println(cl.hasOption("t"));
            System.out.println(cl.getOptionValue("t"));

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
