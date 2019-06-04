package activiti.config;

import learn.activiti.config.Configration;
import org.activiti.engine.test.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigMDCTest {
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigMDCTest.class);

    @org.junit.Test
    @Deployment(resources = {"vacation.bpmn"})
    public static void test(){

    }
}
