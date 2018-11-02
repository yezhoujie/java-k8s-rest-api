package com.rbx.k8shttpapi.controller;

import com.rbx.k8shttpapi.api.ApiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ApiController {

    @Autowired
    ApiInterface apiInterface;

    @GetMapping("apiList")
    public String apiList(){
       //以下注释的为手动为程序添加https 证书信息， 如果使用keytool 添加在证书不在java默认证书目录下
//        System.setProperty("javax.net.ssl.trustStore", "/Users/cy/crtPem");
//        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        return  apiInterface.getApis();
    }
}
