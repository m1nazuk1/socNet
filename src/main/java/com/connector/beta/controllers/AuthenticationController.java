/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connector.beta.controllers;

import com.connector.beta.entities.Image;
import com.connector.beta.entities.MyUser;
import com.connector.beta.entities.Role;
import com.connector.beta.repos.ImageRepo;
import com.connector.beta.repos.RoleRepo;
import com.connector.beta.repos.UserRepo;
import com.connector.beta.services.UserServiceImpl;
import com.connector.beta.services.UserServiceInterface;
import com.connector.beta.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author m1nazuk1
 */

@Controller
public class AuthenticationController {

//    @Autowired
//    UserServiceInterface userServiceInterface;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserValidator userValidator;
    @Autowired
    ImageRepo imageRepo;

    @InitBinder
    private void InitBinder(WebDataBinder binder) {
        binder.addValidators(userValidator);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        dateFormat.setLenient(false);
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @GetMapping("/register")
    public String registerPage(ModelMap mm) {
        mm.addAttribute("newUser", new MyUser());
        System.out.println(MyUser.class);
        return "register";
    }

    @PostMapping("/doregister")
    public String registrationSubmit(ModelMap mm,
                                     @Valid @ModelAttribute("newUser") MyUser myUser,
                                     BindingResult bindingResult) {

        System.out.println(bindingResult);
//        Test Comment

        if (bindingResult.hasErrors()) {
            return "register";
        }


        String encodedPass = passwordEncoder.encode(myUser.getPassword());
        myUser.setPassword(encodedPass);

        List<Role> roles = new ArrayList<>();
//        Hard Coding User role for every new Registration
        Role userRole = roleRepo.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role was not found"));
        roles.add(userRole);

        //        System.out.println(userRole.getClass());
        myUser.setRoles(roles);

        Image image = imageRepo.findById(149).orElseThrow(()->new IllegalArgumentException("image not found"));
        Image newImage= new Image();
        newImage.setFile(image.getFile());
        newImage.setTitle(image.getTitle());
        newImage.setSize(image.getSize());
        newImage.setType(image.getType());
        myUser.setImage(imageRepo.save(newImage));
        userRepo.save(myUser);
        return "redirect:successPage";
    }

    @GetMapping("/successPage")
    public String successPage() {
        return "success";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


}
