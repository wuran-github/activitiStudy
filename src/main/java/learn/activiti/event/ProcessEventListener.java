package learn.activiti.event;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessEventListener implements ActivitiEventListener {
    /** logger */
    private final static Logger LOGGER  = LoggerFactory.getLogger(ProcessEventListener.class);
    public void onEvent(ActivitiEvent activitiEvent) {
        ActivitiEventType eventType = activitiEvent.getType();

        if(ActivitiEventType.PROCESS_STARTED.equals(eventType)){
            LOGGER.info("{} process started", activitiEvent.getProcessInstanceId());
        }
        else if(ActivitiEventType.PROCESS_COMPLETED.equals(eventType)){
            LOGGER.info("{} process ended", activitiEvent.getProcessInstanceId());

        }
    }

    public boolean isFailOnException() {
        return false;
    }
}
