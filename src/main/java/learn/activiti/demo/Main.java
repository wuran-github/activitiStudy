package learn.activiti.demo;

import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {
    static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    //必须引入h2 和log 依赖包
    public static void main(String[] args) throws ParseException {
        LOGGER.info("启动程序");
        //创建流程引擎
        ProcessEngine engine = getProcessEngine();

        //部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition(engine);

        //启动运行流程
        RuntimeService runtimeService = engine.getRuntimeService();
        ProcessInstance processInstance = getProcessInstance(processDefinition, runtimeService);

        //处理流程任务
        handleTask(engine, runtimeService, processInstance);


        LOGGER.info("结束程序");
    }

    private static ProcessInstance getProcessInstance(ProcessDefinition processDefinition, RuntimeService runtimeService) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        LOGGER.info("启动流程:{}", processInstance.getProcessDefinitionKey());
        return processInstance;
    }

    private static void handleTask(ProcessEngine engine, RuntimeService runtimeService, ProcessInstance processInstance) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        while(processInstance != null && !processInstance.isEnded()) {
            TaskService taskService = engine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            for (Task task : list) {
                LOGGER.info(" 待处理任务:{}", task.getName());
                Map<String, Object> variables = getFormVariables(engine, scanner, task);
                //提交任务
                taskService.complete(task.getId(), variables);

                //开始下一个流程
                processInstance = runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
            }
            LOGGER.info("待处理任务数量：{}", list.size());

        }
    }

    private static Map<String, Object> getFormVariables(ProcessEngine engine, Scanner scanner, Task task) throws ParseException {

        FormService formService = engine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties = taskFormData.getFormProperties();

        //输入form
        Map<String, Object> variables = new HashMap<String, Object>();
        String value = "";
        for (FormProperty formProperty : formProperties) {
            if (StringFormType.class.isInstance(formProperty.getType())){

                LOGGER.info("请输入{}", formProperty.getName());
                value = scanner.nextLine();
                variables.put(formProperty.getId(), value);

            }
            else if(DateFormType.class.isInstance(formProperty.getType())){

                LOGGER.info("请输入{}, 格式(yyyy-MM-dd)", formProperty.getName());
                value = scanner.nextLine();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(value);
                variables.put(formProperty.getId(), date);

            }
            else{
                LOGGER.info("类型暂不支持{}", formProperty.getType());
            }
            LOGGER.info("你输入的是[{}]", value);
        }
        return variables;
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine engine) {
        RepositoryService repositoryService = engine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("vacation.bpmn");
        Deployment deployment = deploymentBuilder.deploy();//部署
        String id = deployment.getId();
        String deploymentName = deployment.getName();

        LOGGER.info("deployment id: {}, name : {}",id, deploymentName);

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(id).singleResult();
        LOGGER.info("流程定义文件:{}, 流程ID:{}", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine engine = engineConfiguration.buildProcessEngine();
        String name = engine.getName();
        String version = ProcessEngine.VERSION;

        LOGGER.info("name : {} , version: {}", name, version);
        return engine;
    }
}
