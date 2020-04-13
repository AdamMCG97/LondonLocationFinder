package tech.amcg.springbootpractice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @RequestMapping("/default")
    public String defaultResponse(){
        return "This is the default message.";
    }
}
