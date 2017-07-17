package ua.prettl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application3 {
    private static final Logger LOG = LoggerFactory.getLogger(Application3.class.getSimpleName());

    public static void main(String[] args) {

        LOG.info("Hello {}", "Dimkas");
        LOG.warn("Hello {}", " some warning happened");
        LOG.error("Hello {}", " some error happened");

//        Gson gson = new Gson();
//
//        Configuration configuration = new Configuration();
//
//        configuration.setPath("/home/dimkas71/temp1");
//        configuration.setTeam("11 бригада");
//        configuration.setFrom(7);
//        configuration.setTo(22);
//
//        configuration.setColFio(1);
//        configuration.setColNumber(2);
//        configuration.setColPositon(3);
//        configuration.setColFirstDay(4);
//
//        Configuration[] configs = new Configuration[2];
//        configs[0] = configuration;
//        configs[1] = configuration;
//
//
//        String userDir = System.getProperty("user.dir");
//
//        Path dest = Paths.get(userDir).resolve("configuration.json");
//
//        try {
//
//            FileOutputStream fos = new FileOutputStream(new File(String.valueOf(dest.toFile())));
//
//            fos.write(gson.toJson(configs).getBytes());
//
//            fos.close();
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
