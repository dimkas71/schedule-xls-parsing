package ua.prettl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application3 {
    private static final Logger LOG = LoggerFactory.getLogger(Application3.class.getSimpleName());

    public static void main(String[] args) {

        LOG.info("Hello {}", "Dimkas");
        LOG.warn("Hello {}", " some warning happened");
        LOG.error("Hello {}", " some error happened");

    }
}
