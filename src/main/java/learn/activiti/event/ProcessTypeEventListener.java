package learn.activiti.event;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTypeEventListener implements ActivitiEventListener {
    /** logger */
    private final static Logger LOGGER  = LoggerFactory.getLogger(ProcessTypeEventListener.class);
    public void onEvent(ActivitiEvent activitiEvent) {
        ActivitiEventType eventType = activitiEvent.getType();

            LOGGER.info("this is typeEventListener {} process started, {}",
                    activitiEvent.getProcessInstanceId(),
                    eventType);


    }

    public boolean isFailOnException() {
        return false;
    }
}
