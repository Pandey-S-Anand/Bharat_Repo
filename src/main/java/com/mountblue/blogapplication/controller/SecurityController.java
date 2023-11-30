package com.mountblue.blogapplication.controller;



import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    @Autowired
    public UserRepository userRepository;

    @GetMapping("/showMyLoginPage")
    public String showMyLoginPage(){
        return "loginPage";
    }

    @RequestMapping("/access-denied")
    public String showAccessDenied(){
        return "access-denied";
    }

    @RequestMapping("/register")
    public String registerUser(){
        return "signUpPage";
    }

    @RequestMapping("/addUser")
    public String saveRegisteredUser(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String password){
        if(userRepository.findByEmail(email)!=null){
            return "signUpPage";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("{noop}"+password);
        user.setRole("author");
        userRepository.save(user);
        return "redirect:/";
    }

}
