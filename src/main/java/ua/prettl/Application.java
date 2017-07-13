package ua.prettl;


import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {


    public static void main(String[] args) {

        for (String arg : args) {
            System.out.println(arg);
        }

        String sourceDir = System.getProperty("source.dir");

        if (null == sourceDir) {
            System.out.println("run program with the key -Dsource.dir=c:/temp ");
        } else {



            if (!Files.exists(Paths.get(sourceDir))) {
                System.out.println(String.format("Path %s doesn't exist", sourceDir));
            } else {
                System.out.println("good");
            }


        }



       /* String sourceDir = System.getProperty("user.dir");

        Path path = Paths.get(sourceDir).resolve("application.properties");


        if (!Files.exists(path)) {
            System.out.println("File application.properties doesn't found");
        }  else {
            Properties properties = new Properties();
            try {
                properties.load(new InputStreamReader(Application.class.getClassLoader().getResourceAsStream(path.toString()), "UTF-8"));
                String sourceDir1 = properties.getProperty("source.dir");

                Path path1 = Paths.get(sourceDir1);
                if (!Files.exists(path1)) {
                    System.out.println("pathspath1.toString" + path1.toString());
                } else {
                    for (File f : new File(path1.toFile().toURI()).listFiles()) {
                        System.out.println(f.getName());
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }


        }
*/

//        for (File f : new File(path.to)) {
//            System.out.println(f.getName());
//        }



    }
}
