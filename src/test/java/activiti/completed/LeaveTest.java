package activiti.completed;

import learn.activiti.demo.BeanDemo;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:activiti-context.xml")
public class LeaveTest {
    static final Logger LOGGER = LoggerFactory.getLogger(LeaveTest.class);
    @Rule
    @Autowired
    public ActivitiRule activitiRule;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FormService formService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private BeanDemo beanDemo;

    private String leaderApproveId = "leaderApprove";
    private String hrApproveId = "HRApprove";
    private String cancelVacationId = "cancelVacation";
    @Test
    @Deployment(resources = {"leaveProcess.bpmn"})
    public void test() throws SQLException {
        //设置用户
        User employee = identityService
                .createUserQuery()
                .memberOfGroup("employee")
                .singleResult();

        User leader = identityService
                .createUserQuery()
                .memberOfGroup("leader")
                .singleResult();
        User hr = identityService
                .createUserQuery()
                .memberOfGroup("hr")
                .singleResult();

        LOGGER.info("员工 {} ", employee.getFirstName()+employee.getLastName());
        LOGGER.info("领导 {} ", leader.getFirstName()+leader.getLastName());
        LOGGER.info("hr {} ", hr.getFirstName()+hr.getLastName());

        //设置当前用户
        identityService.setAuthenticatedUserId(employee.getId());

        //启动流程
        ProcessInstance processInstance = StartProcessInstance();
        Map<String, Object> map;

        //领导审批
        Task leaderApprove = taskService.createTaskQuery()
                .taskCandidateGroup("leader")
                .singleResult();

        taskService.claim(leaderApprove.getId(), leader.getId());
        leaderApprove = taskService
                .createTaskQuery()
                .taskAssignee(leader.getId())
                .singleResult();

        Assert.assertEquals("lisi", leaderApprove.getAssignee());

        printTaskInfo(leaderApprove);

        //两种方法完成任务

        //1 提交表单。完成任务
//        variables = new HashMap<String, String>();
//        variables.put("approveResult", "true");
//        formService.submitTaskFormData(leaderApprove.getId(),variables);

        //2 使用taskService完成任务
        map = new HashMap<String, Object>();
        map.put("approveResult", "true");
        taskService.complete(leaderApprove.getId(), map);

        //hr审批
        //选择
        Task hrApprove = taskService.createTaskQuery().active().singleResult();
        //分配任务的两个办法
        hrApprove.setAssignee(hr.getId());
        Assert.assertEquals("wangwu", hrApprove.getAssignee());

        printTaskInfo(hrApprove);

        map = new HashMap<String, Object>();
        map.put("hrApproveResult", "true");
        taskService.complete(hrApprove.getId(), map);

        //销假
        //不能使用processInstance.getActivityId,果然每次都得重新读取一次processInstance
        Task cancel = taskService.createTaskQuery()
                .active()
                .singleResult();
        printTaskInfo(cancel);

        taskService.complete(cancel.getId());

        //可以通过验证有没有active的task来看是否结束
        long count = taskService.createTaskQuery().active().count();
        LOGGER.info("当前活动的任务数:{}", count);

        //验证流程是否结束
        //singleResult 结果多于1会报错
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .finished()
                .singleResult();
        count = historyService
                .createHistoricProcessInstanceQuery()
                .finished()
                .count();
        LOGGER.info("count: {}",count);
        assertNotNull(historicProcessInstance);
        LOGGER.info("历史任务: {}",historicProcessInstance.getProcessDefinitionName());



    }

    private ProcessInstance StartProcessInstance() {
        //因为启动需要填写自动，所以先获取流程定义，填入表单
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                .singleResult();
        // 输出
        LOGGER.info("开始");
        List<FormProperty> list = formService
                .getStartFormData(processDefinition.getId())
                .getFormProperties();
        printFormData(list);

        Map<String, String> variables = new HashMap<String, String>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        variables.put("startDate", "2019-06-14");
        variables.put("endDate", "2019-06-24");
        variables.put("reason", "结婚");

        Map<String, Object> map = new HashMap<String, Object>();
        calendar.set(2019,06,14);
        map.put("startDate", calendar.getTime());
        calendar.add(10,Calendar.DATE);
        map.put("endDate", calendar);
        map.put("reason", "结婚");
        //也可以通过这个来开始流程
        //runtimeService.startProcessInstanceByKey("leave", map);
        //提交开始表单，开始流程
        ProcessInstance processInstance = formService
                .submitStartFormData(processDefinition.getId(), variables);
        assertNotNull(processInstance);
        return processInstance;
    }

    private void printTaskInfo(Task task) {
        LOGGER.info("当前流程：{}", task.getName());
        List<FormProperty> formProperties = formService
                .getTaskFormData(task.getId())
                .getFormProperties();
        printFormData(formProperties);
    }

    private void printFormData(List<FormProperty> formProperties) {
        for (FormProperty formProperty : formProperties) {
            LOGGER.info("{} : {}", formProperty.getName(), formProperty.getValue());
        }
    }

    @Before
    public void setUp(){
        buildEmployee();
        buildHr();
        buildLeader();
    }
    private void buildEmployee(){
        //id
        Group employee = identityService.newGroup("employee");
        employee.setName("员工");
        identityService.saveGroup(employee);

        User zhangsan = identityService.newUser("zhangsan");
        zhangsan.setFirstName("张");
        zhangsan.setLastName("三");
        identityService.saveUser(zhangsan);

        //第一个参数是userid ,第二个是group id
        identityService.createMembership("zhangsan","employee");
    }

    private void buildHr(){
        //id
        Group hr = identityService.newGroup("hr");
        hr.setName("hr");
        identityService.saveGroup(hr);

        User wangwu = identityService.newUser("wangwu");
        wangwu.setFirstName("王");
        wangwu.setLastName("五");
        identityService.saveUser(wangwu);

        //第一个参数是userid ,第二个是group id
        identityService.createMembership("wangwu","hr");

    }
    private void buildLeader(){
        //id
        Group leader = identityService.newGroup("leader");
        leader.setName("领导");
        identityService.saveGroup(leader);

        User lisi = identityService.newUser("lisi");
        lisi.setFirstName("李");
        lisi.setLastName("四");
        identityService.saveUser(lisi);

        //第一个参数是userid ,第二个是group id
        identityService.createMembership("lisi","leader");
    }
}
