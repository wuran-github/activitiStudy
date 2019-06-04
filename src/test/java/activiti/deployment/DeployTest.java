package activiti.deployment;

import learn.activiti.demo.BeanDemo;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:activiti-context.xml")
public class DeployTest {
    static final Logger LOGGER = LoggerFactory.getLogger(DeployTest.class);
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
    public void test() throws SQLException {
        String bpmnClassPath = "VacationProcess.bpmn";

        //创建部署构建器
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        //添加资源
        deploymentBuilder.addClasspathResource(bpmnClassPath);

        //执行部署
        deploymentBuilder.deploy();

        //验证是否部署成功
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery();

        long count = processDefinitionQuery.processDefinitionKey("vacationProcess").count();
        Assert.assertEquals(true, count > 0);


    }
    @Test
    public void inputStreamTest() throws FileNotFoundException {
        String path ="D:\\study\\code\\java\\activitiStudy\\src\\main\\resources\\VacationProcess.bpmn";

        FileInputStream stream = new FileInputStream(path);

        repositoryService
                .createDeployment()
                .addInputStream("VacationProcess.bpmn", stream)
                .deploy();

        //验证是否部署成功
        long count = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("vacationProcess")
                .count();
        Assert.assertEquals(true, count > 0);

    }
    @Test
    public void stringTest(){
        String content = "";
        repositoryService
                .createDeployment()
                .addString("VacationProcess.bpmn",content)
                .deploy();
    }

    @Test
    public void zipTest() throws FileNotFoundException {
        String bpmnClassPath = "D:\\study\\code\\java\\activitiStudy\\src\\main\\resources\\bpmn.zip";
        FileInputStream stream = new FileInputStream(bpmnClassPath);

        repositoryService
                .createDeployment()
                .addZipInputStream(new ZipInputStream(stream))
                .deploy();
        //验证是否部署成功
        long count = repositoryService
                .createProcessDefinitionQuery()
                .count();
        Assert.assertEquals(true, count == 2);


    }

    @Test
    public void deleteTest(){
        String bpmnClassPath = "VacationProcess.bpmn";

        //创建部署构建器
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        //添加资源
        deploymentBuilder.addClasspathResource(bpmnClassPath);

        //执行部署
        Deployment deployment = deploymentBuilder.deploy();
        //验证是否部署成功
        long count = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("vacationProcess")
                .count();
        Assert.assertEquals(true, count > 0);

        //删除部署 true
        repositoryService.deleteDeployment(deployment.getId(), true);
    }
}
