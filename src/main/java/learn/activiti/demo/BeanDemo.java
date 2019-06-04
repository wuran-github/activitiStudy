package learn.activiti.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanDemo {
    /** logger */
    private final static Logger LOGGER  = LoggerFactory.getLogger(BeanDemo.class);
    public String helloBean(){
        LOGGER.info("hello bean");
        return "hello bean";
    }
}
