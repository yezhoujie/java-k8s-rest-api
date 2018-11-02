# java-k8s-rest-api
# Java feign REST 调用 k8s https API 接口（证书模式）
## 背景
 - k8s API 全套支持 REST 方式调用
 - k8s 有java 的 sdk, 但是支持的k8s版本总是落后于最新版
 - 所以想在java中rest的方式调用 API server 中的API
 - 所测试的k8s集群 API server走的是https, 安装证书的方式
 - k8s rest 接口文档地址：https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.12/#-strong-api-overview-strong-
 
## 通过postman测试接口调用
    之前就知道k8s API Server 走的是https，里面需要证书，在正式做开始java之前，就想着用postman先来一波，如果走的通，那java应该问题不大。
 ### 关于k8s内的认证
    本次所搭建的k8s集群内部走的是基于RBAC认证机制的，即基于角色的访问控制，可以通过dashboard看到集群内部已经有很多的默认用户。那这里就从头开始，创建一个用户专门给rest接口调用的。其他相关知识请自行查看相关文档。

#### 创建用户 yaml
```
apiVersion: v1
# 创建的类型是账号
kind: ServiceAccount
metadata:
  name: restuser #用户名
  namespace: kube-system #用户所属namespace, 这里要访问API Server 所以必须是 kube-system

---

#创新的类型是 用户角色绑定
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: restuser
subjects: #账号信息
  - kind: ServiceAccount
    name: restuser
    namespace: kube-system
roleRef: #角色信息
  kind: ClusterRole
  name: cluster-admin #内置角色
  apiGroup: rbac.authorization.k8s.io

```
```
kubectl apply -f <filename.yaml>
```
完成角色创建

#### 获取这个用户的token
直接在dashboard 页面 选定namespace, 然后在Secret此单中，找到对应用户的token字符串


#### postman中调用测试
GET https://10.66.0.29:8443
在authorization选项中添加token
```
Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJyZXN0dXNlci10b2tlbi05dGtydyIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJyZXN0dXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjgyZGU2Nzk5LWRkN2YtMTFlOC1iZDM5LTAwMTYzZTBjMmY2MSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTpyZXN0dXNlciJ9.Ak1cjkibs_ldGWzONef7iau7ga5Kdlg2qqLLWaD06_uWFj_AOmbNmQBTTcnPUQX336w2JwkUT3KhmsDArHBWtOQGm8d2wLj-U0DTgML7TQwC2dVzMZtdVOMygnDhCjPWWQd7kCWUtGnrGH7YJijU1Qhhic7SKzBP6gNFeEUKhCrx6d-vJOjSZ9E-fqnOfOpj8x6lX3KU6YQVO9ortp8URvh6vlMhlYJrbPPHh9jd_1D5SrcAB-BeaRRZfpkIeYRdTvN-KdIcnFPDoHVgJL7xTQ-X5qRkVF3k7Av_quSIURyOiqaXDoG6LQtQ8Lt-uiy6aoKPpW60hJsdWeGI1GN3Zw
```
格式是 Bearer <token>

调用后发下报错，根本无法调用，原因是因为需要证书

#### 处理证书
postman 用的是浏览器中的证书，所以只要找到k8s的https证书导入到浏览器中, k8s证书一般放在 k8s node 上的 /etc/kubernetes/ssl 路径下，本例中的证书是ca.pem，将他拷贝到本地，导入到浏览器中
再次调用postman,发现调用成功了。

## JAVA 开始
- 创建一个spring-boot 项目包含web, feign.
- feignApiInterface.java
```
package com.rbx.k8shttpapi.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "k8sRestApi", url = "https://10.66.0.29:8443")
public interface ApiInterface {
    // 在头部添加认证信息
   // @RequestMapping(method = RequestMethod.GET, value = "", headers = {"authorization=Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJyZXN0dXNlci10b2tlbi05dGtydyIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJyZXN0dXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjgyZGU2Nzk5LWRkN2YtMTFlOC1iZDM5LTAwMTYzZTBjMmY2MSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTpyZXN0dXNlciJ9.Ak1cjkibs_ldGWzONef7iau7ga5Kdlg2qqLLWaD06_uWFj_AOmbNmQBTTcnPUQX336w2JwkUT3KhmsDArHBWtOQGm8d2wLj-U0DTgML7TQwC2dVzMZtdVOMygnDhCjPWWQd7kCWUtGnrGH7YJijU1Qhhic7SKzBP6gNFeEUKhCrx6d-vJOjSZ9E-fqnOfOpj8x6lX3KU6YQVO9ortp8URvh6vlMhlYJrbPPHh9jd_1D5SrcAB-BeaRRZfpkIeYRdTvN-KdIcnFPDoHVgJL7xTQ-X5qRkVF3k7Av_quSIURyOiqaXDoG6LQtQ8Lt-uiy6aoKPpW60hJsdWeGI1GN3Zw"})

    //可在feign 拦截器中统一添加认证信息， 见FeignInterceptor类
    @RequestMapping(method = RequestMethod.GET, value = "")
    public String getApis();
}

```

ApiController.java

```
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

```
注意 header 中加入了 token

- 可以使用Feign 的拦截器，为所有feign调用统一配置token

FeignInterceptor.java

```
package com.rbx.k8shttpapi;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;


/**
 * feign 请求拦截器
 * 可以在这里为所有feign 请求统一添加 认证头
 */

@Component
public class FeignInterceptor implements RequestInterceptor {

    public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header("authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJyZXN0dXNlci10b2tlbi05dGtydyIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJyZXN0dXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjgyZGU2Nzk5LWRkN2YtMTFlOC1iZDM5LTAwMTYzZTBjMmY2MSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTpyZXN0dXNlciJ9.Ak1cjkibs_ldGWzONef7iau7ga5Kdlg2qqLLWaD06_uWFj_AOmbNmQBTTcnPUQX336w2JwkUT3KhmsDArHBWtOQGm8d2wLj-U0DTgML7TQwC2dVzMZtdVOMygnDhCjPWWQd7kCWUtGnrGH7YJijU1Qhhic7SKzBP6gNFeEUKhCrx6d-vJOjSZ9E-fqnOfOpj8x6lX3KU6YQVO9ortp8URvh6vlMhlYJrbPPHh9jd_1D5SrcAB-BeaRRZfpkIeYRdTvN-KdIcnFPDoHVgJL7xTQ-X5qRkVF3k7Av_quSIURyOiqaXDoG6LQtQ8Lt-uiy6aoKPpW60hJsdWeGI1GN3Zw");
    }
}

```

跑起来， 访问 http://localhost:8080/api/apiList

报错啦，一想就知道了，证书问题

### 解决java 证书问题
 #### keytool
 ```
 sudo keytool -import -keystore "$JAVA_HOME/jre/lib/security/cacerts"  -storepass changeit -keypass changeit -alias kubernetes -file kubernetes.pem
 ```
 -storepass changeit-keypass changeit 为了保持java默认的状态，保证不做配置可以被调用
 
 -file kubernetes.pem 证书目录，我这里做了重命名，保持和证书名一致
 
 再跑一遍，成功！
 