package com.t4cloud.t.base.aspect;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.t4cloud.t.base.annotation.AutoLog;
import com.t4cloud.t.base.entity.LoginUser;
import com.t4cloud.t.base.entity.T4Log;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.utils.IPUtil;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.service.service.IT4CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;

import static com.t4cloud.t.base.constant.MqConstant.TOPIC_LOG;

/**
 * 系统日志，切面处理类
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/12 11:27
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AutoLogAspect {

    private final IT4CommonService service;
    private final Environment environment;

    @Lazy
    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;


    @Pointcut("@annotation(com.t4cloud.t.base.annotation.AutoLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result;
        try {
            //执行方法
            result = point.proceed();

            //执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;

            //保存日志
            saveSysLog(point, result, time);

        } catch (Exception e) {
            //执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            //保存日志
            saveSysLog(point, e, time);

            throw e;
        }


        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, Object result, long time) {
        long beginTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        T4Log t4Log = new T4Log();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if (syslog != null) {
            //注解上的描述,操作日志内容
            t4Log.setLogContent(syslog.value());
            t4Log.setLogType(syslog.logType());
            t4Log.setOperateType(syslog.operateType());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        t4Log.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONUtil.toJsonStr(args);
            t4Log.setRequestParam(params);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //这三种类型是接口，获取request相关的数据
        boolean apiLog = syslog.logType() == 1 || syslog.logType() == 2 || syslog.logType() == 3;
        if (apiLog) {
            try {
                //获取request
                HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
                //设置IP地址
                t4Log.setIp(IPUtil.getIpAddr(request));
                t4Log.setRequestUrl(request.getRequestURL().toString());
                t4Log.setRequestType(request.getMethod());
                //获取请求头
                JSONObject object = new JSONObject();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String key = (String) headerNames.nextElement();
                    String value = request.getHeader(key);
                    object.set(key, value);
                }
                t4Log.setRequestHeader(object.toString());

                //获取登录用户信息
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (sysUser != null) {
                    t4Log.setUserId(sysUser.getId());
                    t4Log.setUsername(sysUser.getUsername());
                }

            } catch (Exception e) {
                //此处是因为万一别人用错了，不影响正常正常业务
                log.debug("AutoLog注解无法获取到Request数据，请检查是否API接口方法上。若不是-请使用合适的LogType");
            }
        }

        //耗时
        t4Log.setCostTime(time);
        t4Log.setCreateTime(new Date());
        if (result instanceof T4CloudException) {
            t4Log.setResult(((T4CloudException) result).getMessage());
            t4Log.setResultType(0);
        } else if (result instanceof Exception) {
            Throwable cause = ((Exception) result).getCause();
            if (cause != null) {
                t4Log.setResult(((Exception) result).getCause().getMessage());
            } else {
                t4Log.setResult(JSONUtil.toJsonStr(((Exception) result).getStackTrace()));
            }
            t4Log.setResultType(0);
        } else {
            t4Log.setResult(JSONUtil.toJsonStr(result));
            t4Log.setResultType(1);
        }

        log.info("========================= START " + t4Log.getLogContent() + " START =========================");
        log.debug("Method  \t:  \t" + t4Log.getMethod());
        if (apiLog) {
            log.info("URL     \t:  \t" + t4Log.getRequestType() + ":" + t4Log.getRequestUrl());
            log.debug("Header   \t:  \t" + t4Log.getRequestHeader());
            log.debug("Param   \t:  \t" + t4Log.getRequestParam());
            log.debug("User    \t:  \t" + t4Log.getUsername() + "," + t4Log.getUserId());
        }
        log.info("Result  \t:  \t" + t4Log.getResult());


        //保存系统日志
        //切换到MQ方式 -by TeaR  -2020/2/17-14:52
        try {
            //获取日志处理方式
            Integer logType = Integer.parseInt(environment.getProperty("t4cloud.log-type"));
            t4Log.setCreateBy("直接入库");

            if ((logType == null || logType == 1) && rocketMQTemplate != null) {
                //默认或指定采用MQ处理日志
                rocketMQTemplate.convertAndSend(TOPIC_LOG, t4Log);
            } else if (logType == -1) {
                //不保留日志
            } else if (logType == 2) {
                /** TODO 采用ES处理日志
                 *
                 * -by TeaR  -2020/9/27-15:03
                 */
            } else {
                //其他项都采用直接入库的方式
                service.saveLog(t4Log);
            }

        } catch (Exception e) {
            //如果MQ异常，采取直接交互数据库的方式
            service.saveLog(t4Log);
        }
        log.info("TIME(ms)    \t:  \t 业务耗时：" + t4Log.getCostTime() + ", 日志耗时：" + (System.currentTimeMillis() - beginTime));
        log.info("========================= END " + t4Log.getLogContent() + " END =========================");

    }

}

