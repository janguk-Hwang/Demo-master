package com.example.demo.maincontroller;

//MainPage 클래스 매핑하기위해서 주소와(논리적인 주소 인터넷주소) 물리적인 파일 매핑
//하나의 주소에대해서 어디서 매핑 누가매핑? 후보군 클래스,속성과 메소드 -> 메소드를 통해서 매핑


import com.example.demo.components.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MailComponents mailComponents;


    @RequestMapping("/error/denied")
    public String errorDenied() {

        return "error/denied";
    }




}