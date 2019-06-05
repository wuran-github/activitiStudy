package learn.activiti.spring.demo.demo.model;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String taskName;
    private String taskId;
    private String assignee;
    private List<FormModel> forms;
    public TaskModel(){
        forms = new ArrayList<>();
    }

    public TaskModel(Task task){
        setTaskId(task.getId());
        setTaskName(task.getName());
        setAssignee(task.getAssignee());
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<FormModel> getForms() {
        return forms;
    }

    public void setForms(List<FormModel> forms) {
        this.forms = forms;
    }
    public void addForm(FormModel form){
        forms.add(form);
    }
    public void removeForm(FormModel form){
        forms.remove(form);
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
