package learn.activiti.spring.demo.demo.controller;

import learn.activiti.spring.demo.demo.util.SessionUtil;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

public class LoginController {
    @Autowired
    private IdentityService identityService;
    @GetMapping("login")
    public String loginPage(){
        return "login";
    }
    @PostMapping("login")
    public String login(@PathVariable("name") String name,
                      @PathVariable("password") String password,
                      HttpServletRequest request){
        setUp();
        SessionUtil.setSessionAttr("name", name, request);
        return "redirect:task";
    }

    public void setUp(){
        long count = identityService
                .createGroupQuery()
                .groupId("employee")
                .count();
        if(count > 0) {
            return;
        }
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
