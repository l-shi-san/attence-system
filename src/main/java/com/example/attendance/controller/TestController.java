package com.example.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test1")
    public String test1(Model model)
    {
        model.addAttribute("title","Title aaaaa bbb");
        return "test1";
    }
}
