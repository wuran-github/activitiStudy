package learn.activiti.spring.demo.demo.api;

import learn.activiti.spring.demo.demo.model.FormModel;
import learn.activiti.spring.demo.demo.model.TaskModel;
import learn.activiti.spring.demo.demo.util.SessionUtil;
import org.activiti.engine.FormService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class TaskApiController {

    @Autowired
    TaskService taskService;
    @Autowired
    FormService formService;

    @RequestMapping(value = "notAssignTaskList", method = RequestMethod.GET)
    public List<TaskModel> getNotAssignTasks(HttpServletRequest request){
        String name = (String)SessionUtil.getSessionAttr("name", request);

        List<Task> taskList = taskService
                .createTaskQuery()
                .taskCandidateUser(name)
                .taskUnassigned()
                .list();
        return buildTaskModels(taskList);
    }
    @RequestMapping(value = "assignedTaskList", method = RequestMethod.GET)
    public List<TaskModel> getAssignedTasks(HttpServletRequest request){
        String name = (String)SessionUtil.getSessionAttr("name", request);

        List<Task> taskList = taskService
                .createTaskQuery()
                .taskCandidateOrAssigned(name)
                .list();
        List<TaskModel> tasks = buildTaskModels(taskList);
        return tasks;
    }


    @GetMapping("taskDetail/{id}")
    public TaskModel getTask(@PathVariable("id") String taskId,
                        HttpServletRequest request){
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        TaskModel taskModel = new TaskModel(task);

        buildTaskForms(taskId, taskModel);
        return taskModel;
    }

    private void buildTaskForms(String taskId, TaskModel taskModel) {
        List<FormProperty> formProperties = formService
                .getTaskFormData(taskId)
                .getFormProperties();
        for (FormProperty formProperty : formProperties) {
            FormModel form = new FormModel(formProperty.getName(),
                    formProperty.getValue());
            taskModel.addForm(form);
        }
    }

    private List<TaskModel> buildTaskModels(List<Task> taskList) {
        List<TaskModel> tasks = new ArrayList<>();
        for (Task task : taskList) {
            TaskModel model = new TaskModel();
            model.setAssignee(task.getAssignee());
            model.setTaskName(task.getName());
            tasks.add(model);
        }
        return tasks;
    }
}
