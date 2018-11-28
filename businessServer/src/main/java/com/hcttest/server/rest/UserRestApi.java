package com.hcttest.server.rest;

import com.hcttest.server.model.core.User;
import com.hcttest.server.model.request.LoginUserRequest;
import com.hcttest.server.model.request.RegisterUserRequest;
import com.hcttest.server.model.request.UpdateUserRequest;
import com.hcttest.server.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

//用于处理User相关
@Controller
@RequestMapping("/rest/users")
public class UserRestApi {

    //用户注册功能 /rest/users/register
    private UserService userService;

    /**
     * 访问 url:// /rest/users/register?username=abc&password=abc
     * 返回 JSON {success:true}
     * @param username
     * @param password
     * @param model
     * @return
     */
    @RequestMapping(path = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(@RequestParam("username") String username,@RequestParam("password") String password, Model model){

        model.addAttribute("success",userService.registerUser(new RegisterUserRequest(username,password)));
        return  model;
    }

    //登陆功能  /rest/users/login

    /**
     * 访问 url:// /rest/users/login?username=abc&password=abc
     * 返回 JSON {success:true}
     * @param username
     * @param password
     * @param model
     * @return
     */
    @RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model loginUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model){

        model.addAttribute("success",userService.loginUser(new LoginUserRequest(username,password)));
        return  model;
    }

    /**
     * //需要能够添加用户的影片类别
     * 访问 url:// /rest/users/genres?username=abc&genres=a|b|c|d
     * 返回 JSON {success:true}
     * @param username
     * @param genres
     * @param model
     * @return
     */
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addGenres(@RequestParam("username") String username, @RequestParam("genres") String genres, Model model){
        List<String> genresList = new ArrayList<>();
        for (String str:genres.split("|")) {
            genresList.add(str);
        }
        userService.updateUserGenres(new UpdateUserRequest(username,genresList));
        return null;
    }
}
