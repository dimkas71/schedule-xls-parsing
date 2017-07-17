package ua.prettl;

import com.google.gson.Gson;
import ua.prettl.ua.prettl.app.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Application55 {

    public static void main(String[] args) {

        Path source = Paths.get(System.getProperty("user.dir")).resolve("configuration.json");


        Gson gson = new Gson();

        try {
            byte[] allBytes = Files.readAllBytes(source);

            String result = new String(allBytes);

            Configuration[] configs = gson.fromJson(result, Configuration[].class);

            System.out.println(Arrays.toString(configs));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
