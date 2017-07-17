package ua.prettl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class JCommanderDemo {
    public static void main(String[] args) {

        String[] commandLineArgs = {"-verbose", "32", "-l", "222", "-hosts", "192.168.1.1, 192.168.1.3, 192.168.1.2"};

        System.out.println("Hello world");

        Args args1 = new Args();

        Args1 args2 = new Args1();

        JCommander.newBuilder()
                .addObject(args1)
                .addObject(args2)
                .build()
                .parse(commandLineArgs);


        System.out.println(args1.getVerbose());

        System.out.println(args2.hosts);

    }

}

class Args {

    @Parameter(names = {"-verbose", "-v"}, description = "Level of verbosity")
    private Integer verbose = 1;

    public Integer getVerbose() {
        return verbose;
    }
}

class Args1 {
    @Parameter(names = {"--length", "-l"})
    Integer length;

    @Parameter(names = {"-hosts"})
    List<String> hosts = new ArrayList<String>();

}

