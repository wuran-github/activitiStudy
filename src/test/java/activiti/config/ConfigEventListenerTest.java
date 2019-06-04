package activiti.config;

import learn.activiti.config.Starter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class ConfigEventListenerTest {
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigEventListenerTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_eventListener.cfg.xml");
    @org.junit.Test
    @Deployment(resources = {"my-process.bpmn20.xml"})
    public void test() throws SQLException {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        assertNotNull(processInstance);


        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        activitiRule.getTaskService().complete(task.getId());

//手动添加监视
        activitiRule.getRuntimeService().addEventListener(new ActivitiEventListener() {
            public void onEvent(ActivitiEvent activitiEvent) {
                ActivitiEventType eventType = activitiEvent.getType();
                LOGGER.info("{} is add listener,type {}",
                        activitiEvent.getProcessInstanceId(),
                        eventType);
            }

            public boolean isFailOnException() {
                return false;
            }
        }, ActivitiEventType.PROCESS_STARTED);
        //manual dispatch
        //task id 会为空
        activitiRule.getRuntimeService().dispatchEvent(new ActivitiEventImpl(ActivitiEventType.PROCESS_STARTED));
    }
}
