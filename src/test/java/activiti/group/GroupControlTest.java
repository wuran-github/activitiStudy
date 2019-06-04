package activiti.group;

import learn.activiti.demo.BeanDemo;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
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

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:activiti-context.xml")
public class GroupControlTest {
    static final Logger LOGGER = LoggerFactory.getLogger(GroupControlTest.class);
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
    private BeanDemo beanDemo;
    @Test
    @Deployment(resources = {"vacationProcess.bpmn"})
    public void test() throws SQLException {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationProcess");
        assertNotNull(processInstance);
        User student = identityService.createUserQuery().memberOfGroup("student").singleResult();
        LOGGER.info("student :{}", student.getFirstName()+student.getLastName());
        Task apply = taskService.createTaskQuery()
                .taskCandidateGroup("student")
                .singleResult();
        //分配任务，不是分配到当前实体
        LOGGER.info("任务名称:{}",apply.getName());
        taskService.claim(apply.getId(), "lisi");
        //必须重新查询一遍
        apply = taskService
                .createTaskQuery()
                .taskAssignee("lisi")
                .singleResult();
        assertNotNull(apply);
        Assert.assertEquals("lisi",apply.getAssignee());
        //
        taskService.complete(apply.getId());

        Task approve = taskService.createTaskQuery()
                .taskCandidateGroup("teacher")
                .singleResult();

        taskService.claim(approve.getId(), "zhangsan");

        //必须重新查询一遍
        approve = taskService
                .createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        assertNotNull(approve);
        Assert.assertEquals("zhangsan",approve.getAssignee());

        taskService.complete(approve.getId());

    }
    @Before
    public void setUp(){
        buildTeacher();
        buildStudent();


    }
    private void buildTeacher(){
        //id
        Group teacher = identityService.newGroup("teacher");
        teacher.setName("教师");
        identityService.saveGroup(teacher);

        User zhangsan = identityService.newUser("zhangsan");
        zhangsan.setFirstName("张");
        zhangsan.setLastName("三");
        identityService.saveUser(zhangsan);

        //第一个参数是userid ,第二个是group id
        identityService.createMembership("zhangsan","teacher");

    }
    private void buildStudent(){
        //id
        Group student = identityService.newGroup("student");
        student.setName("学生");
        identityService.saveGroup(student);

        User lisi = identityService.newUser("lisi");
        lisi.setFirstName("李");
        lisi.setLastName("四");
        identityService.saveUser(lisi);

        //第一个参数是userid ,第二个是group id
        identityService.createMembership("lisi","student");
    }
}
