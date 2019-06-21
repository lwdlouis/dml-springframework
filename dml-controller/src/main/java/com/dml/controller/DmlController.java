package com.dml.controller;


import com.dml.model.UserInfo;
import com.dml.service.DmlService;
import com.dml.spring.framework.annotation.*;
import com.dml.spring.framework.ui.Model;

@Controller
public class DmlController {

    @Autowire
    private DmlService dmlService;

    @RequestMapping("/index")
    public String index(@RequestParam("name") String name,
                        Model model) {

        String fullName = dmlService.getFullName(name);

        model.addAttribute("name", fullName);

        return "index";
    }


    @ResponseBody
    @RequestMapping("/index/user/info")
    public UserInfo index(@RequestParam("name") String name) {

        UserInfo userInfo = dmlService.getUserInfoByName(name);

        return userInfo;
    }
}
