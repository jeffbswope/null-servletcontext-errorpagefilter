# Security Bug Reproduction

(Repo named after original bug that is now fixed, now used to generally minimally reproduce other 
issues related to security/error page integration.)

## Current Issue

In `3.0.0-M4` we now get a problem where a `403` error becomes a `401` error upon trying to hit the 
error page when using stateless sessions/basic auth.

Demonstrated in failure of test: 
`NullServletcontextErrorpagefilterApplicationTests.noAuthIs403` 

Hitting an endpoint without the proper role should be `403`.

Moving from `3.0.0-M3` to `3.0.0-M4` this became a `401`.

Changing the `SessionCreationPolicy` away from `NEVER`/`STATELESS` appears to fix the problem. 

## Previous Issues

### Null ServletContext with multiple filter chains

https://github.com/spring-projects/spring-boot/issues/29564

We often use a library-provided `SecurityFilterChain` targeting actuator endpoints to provide standardized
authentication and behavior of the actuator across our services, irrespective of the applications' own
security setup.

Applications then include their own `SecurityFilterChain`.

In `3.0.0-M1` an error would occur because `ServletContext` was null on error page hit.

See `5ee3264093d7fd3cf6ae213aced7cf636e8c33c7` for repro.

Failure occurs in tests where error page is to be hit.

Disabling the separate `ActuatorSecurityConfig` solves the problem.

```
java.lang.IllegalArgumentException: ServletContext must not be null
	at org.springframework.util.Assert.notNull(Assert.java:201) ~[spring-core-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(WebApplicationContextUtils.java:112) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(WebApplicationContextUtils.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(WebApplicationContextUtils.java:83) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.boot.security.servlet.ApplicationContextRequestMatcher.matches(ApplicationContextRequestMatcher.java:58) ~[spring-boot-3.0.0-M1.jar:3.0.0-M1]
	at org.springframework.security.web.DefaultSecurityFilterChain.matches(DefaultSecurityFilterChain.java:67) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.RequestMatcherDelegatingWebInvocationPrivilegeEvaluator.getDelegate(RequestMatcherDelegatingWebInvocationPrivilegeEvaluator.java:115) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.RequestMatcherDelegatingWebInvocationPrivilegeEvaluator.isAllowed(RequestMatcherDelegatingWebInvocationPrivilegeEvaluator.java:66) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.boot.web.servlet.filter.ErrorPageSecurityFilter.isAllowed(ErrorPageSecurityFilter.java:83) ~[spring-boot-3.0.0-M1.jar:3.0.0-M1]
	at org.springframework.boot.web.servlet.filter.ErrorPageSecurityFilter.doFilter(ErrorPageSecurityFilter.java:71) ~[spring-boot-3.0.0-M1.jar:3.0.0-M1]
	at org.springframework.boot.web.servlet.filter.ErrorPageSecurityFilter.doFilter(ErrorPageSecurityFilter.java:65) ~[spring-boot-3.0.0-M1.jar:3.0.0-M1]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:327) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:106) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:81) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:122) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:116) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:87) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:81) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:109) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:149) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:103) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:89) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:110) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:80) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:211) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:183) ~[spring-security-web-6.0.0-M1.jar:6.0.0-M1]
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:351) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:267) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:101) ~[spring-web-6.0.0-M2.jar:6.0.0-M2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:185) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:158) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:691) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:443) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:367) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:295) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.StandardHostValve.custom(StandardHostValve.java:387) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.StandardHostValve.status(StandardHostValve.java:233) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:155) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:355) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:866) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1708) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-10.0.16.jar:10.0.16]
	at java.base/java.lang.Thread.run(Thread.java:833) ~[na:na]
```