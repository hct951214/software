package com.hcttest.server.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//用于处理User相关
@Controller
@RequestMapping("/rest/users")
public class UserRestApi {

    //用户注册功能 /rest/users/register
    @RequestMapping(path = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(String username, String password, Model model){
        if (username == ""){

        }

        return  null;
    }

    //登陆功能  /rest/users/register
    @RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model loginUser(String username, String password, Model model){
        if (username == ""){

        }

        return  null;
    }

    //需要能够添加用户的影片类别
    public Model addGenres(String username, String genres, Model model){
        return null;
    }
}
