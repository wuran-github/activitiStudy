package learn.activiti.spring.demo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProcessController {
    @RequestMapping("process")
    public String process(){
        return "process";
    }
}
