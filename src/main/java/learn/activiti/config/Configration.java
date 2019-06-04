package learn.activiti.config;

import learn.activiti.demo.Main;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configration {
    static final Logger LOGGER = LoggerFactory.getLogger(Configration.class);

    public static ProcessEngine buildProcessEngine() {
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();
        ProcessEngine engine = engineConfiguration.buildProcessEngine();
        String name = engine.getName();
        String version = ProcessEngine.VERSION;

        LOGGER.info("name : {} , version: {}", name, version);
        return engine;
    }
}
