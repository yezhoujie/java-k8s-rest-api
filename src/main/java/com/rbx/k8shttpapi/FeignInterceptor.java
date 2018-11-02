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
