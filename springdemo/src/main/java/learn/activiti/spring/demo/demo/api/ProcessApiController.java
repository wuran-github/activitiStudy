package learn.activiti.spring.demo.demo.api;

import learn.activiti.spring.demo.demo.model.ProcessModel;
import learn.activiti.spring.demo.demo.model.TaskModel;
import learn.activiti.spring.demo.demo.util.SessionUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProcessApiController {
    @Autowired
    RepositoryService repositoryService;

    @RequestMapping(value = "processList", method = RequestMethod.GET)
    public void getProcesses(HttpServletRequest request){
        String name = (String) SessionUtil.getSessionAttr("name", request);

        List<ProcessModel> processes = new ArrayList<>();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .startableByUser(name)
                .list();
        for (ProcessDefinition processDefinition : processDefinitions) {
            ProcessModel processModel = new ProcessModel(
                    processDefinition.getId(),
                    processDefinition.getName(),
                    processDefinition.getVersion()
                    );
            processes.add(processModel);
        }

    }
    @PostMapping(value = "beginProcess")
    public Map<String, Object> beginProcess(@RequestBody Map<String, Object> variables,
            HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        if(variables.containsKey("processId") && variables.containsKey("variables")){

        }
        result.put("status",1);
        result.put("message","参数不正确");
        return result;
    }

}
