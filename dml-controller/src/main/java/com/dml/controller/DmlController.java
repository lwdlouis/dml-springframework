package com.dml.controller;


import com.dml.spring.framework.annotation.Controller;
import com.dml.spring.framework.annotation.RequestMapping;
import com.dml.spring.framework.annotation.RequestParam;
import com.dml.spring.framework.ui.Model;

@Controller
public class DmlController {


    @RequestMapping("/index")
    public String index(@RequestParam("name") String name,
                        Model model) {

        model.addAttribute("name", name);

        return "index";
    }
}
