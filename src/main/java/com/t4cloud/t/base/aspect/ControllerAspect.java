package com.t4cloud.t.base.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.t4cloud.t.base.annotation.NoDict;
import com.t4cloud.t.base.annotation.RSA;
import com.t4cloud.t.base.constant.RequestConstant;
import com.t4cloud.t.base.constant.ResultConstant;
import com.t4cloud.t.base.entity.dto.R;
import com.t4cloud.t.base.exception.T4CloudBadRequestException;
import com.t4cloud.t.base.utils.DictUtil;
import com.t4cloud.t.base.utils.RSAUtil;
import com.t4cloud.t.base.utils.RedisUtil;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.service.service.IT4CommonService;
import io.swagger.annotations.ApiModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器切面处理
 * 1.翻译字典
 * 2.参数校验
 * 3.RSA自动加解密实现
 * 4.过滤重复请求和过期请求
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/9 12:44
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ControllerAspect {

    private final IT4CommonService service;
    private final DictUtil dictUtil;
    @Value("${t4cloud.valid-request}")
    private Boolean validRequest;
    @Value("#{'${t4cloud.valid-filter: }'.split(',')}")
    private List<String> validFilter;

    /**
     * 定义切点Pointcut
     */
    @Pointcut("execution(public * com.t4cloud.t.*.controller.*Controller.*(..))")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long time1 = System.currentTimeMillis();

        HttpServletRequest request = null;
        try {
            request = SpringContextUtil.getHttpServletRequest();

        } catch (Exception e) {
            log.debug("It is not recommended to define common methods in your controller……");
        }

        /**
         * 请求校验
         */
        if (request != null && validRequest) {
            validRequest(request, time1);
        }

        /**
         * 判断参数异常
         */
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                service.valid((BindingResult) arg);
            }
        }

        /**
         * 处理RSA自动加解密
         */
        handleRSA(pjp, args);

        //进入接口
        Object result = pjp.proceed(args);
        long time2 = System.currentTimeMillis();
        log.debug("接口业务 耗时：" + (time2 - time1) + "ms");

        /**
         * 开始字典翻译（无返回值的不翻译）
         */
        if (request != null && result != null) {
            //判断是否FEIGN请求，feign请求不翻译
            String clientType = request.getHeader(ResultConstant.T_CLIENT_TYPE);
            //获取NoDict注解，若存在则不翻译
            Method method = null;
            NoDict noDict = null;
            try {
                method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), ((MethodSignature) pjp.getSignature()).getParameterTypes());
                if (method != null) {
                    noDict = method.getAnnotation(NoDict.class);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //开始翻译
            if (StrUtil.isBlank(clientType) && noDict == null) {
                long start = System.currentTimeMillis();
                this.parseDictText(result);
                long end = System.currentTimeMillis();
                log.debug("解析字典数据  耗时" + (end - start) + "ms");
            }
        }

        log.debug("总 耗时：" + (System.currentTimeMillis() - time1) + "ms");

        return result;
    }

    /**
     * 检测返回的对象是否为LIST或者是Page,如果是单独的对象则判断是否有API MODEL注解，如果满足条件，则尝试翻译
     *
     * @param result 返回的对象
     *               <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/9 12:47
     */
    private void parseDictText(Object result) {

        //空结果处理，理论上不会有
        if (result == null) {
            return;
        }

        Map<String, String> dictTemp = new HashMap<>();

        log.debug("result" + JSONUtil.toJsonStr(result));

        if (result instanceof R) {

            //空data则忽略
            if (((R) result).getResult() == null) {
                return;
            }

            log.debug("result.getResult" + JSONUtil.toJsonStr(((R) result).getResult()));

            //翻译IPAGE字典
            if (((R) result).getResult() instanceof IPage) {
                ((IPage) ((R) result).getResult()).setRecords(dictUtil.parseListDict(((IPage) ((R) result).getResult()).getRecords(), dictTemp));
            }

            //尝试翻译List字典
            if (((R) result).getResult() instanceof List) {
                ((R) result).setResult(dictUtil.parseListDict((List) ((R) result).getResult(), dictTemp));
            }

            //尝试翻译单个实体类，说明是entity实体，也需要解析
            if (((R) result).getResult().getClass().getAnnotation(ApiModel.class) != null) {
                ((R) result).setResult(dictUtil.parseEntityDict(((R) result).getResult(), dictTemp));
            }

        }

        dictTemp = null;

    }


    /**
     * 处理RSA自动加解密
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/3/9 22:52
     */
    private void handleRSA(ProceedingJoinPoint pjp, Object[] args) throws NoSuchMethodException, IllegalAccessException {
        //RSA自动解密
        Signature sig = pjp.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        Annotation[][] parameterAnnotations = currentMethod.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            //为空直接跳过
            if (args[i] == null) {
                continue;
            }

            //获取参数是否有RSA注解
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation.annotationType() == RSA.class && args[i] instanceof String) {
                    //参数是字符串，直接转换
                    args[i] = RSAUtil.decrypt(args[i].toString().replace(" ", "+"));
                } else if (annotation.annotationType() == RSA.class) {
                    //参数是对象，在对象中寻找加密字段
                    for (Field field : FieldUtils.getFieldsWithAnnotation(args[i].getClass(), RSA.class)) {
                        field.setAccessible(true);
                        if (field.get(args[i]) == null) {
                            continue;
                        }
                        field.set(args[i], RSAUtil.decrypt((field.get(args[i]).toString()).replace(" ", "+")));
                    }
                }
            }
        }
    }

    /**
     * 校验请求有效性
     * 1.放重复提交
     * 2.丢弃超时请求
     * 3.可以拓展做接口限流，但是一般会放在其他服务上做限流
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/3/20 14:55
     */
    private void validRequest(HttpServletRequest request, long time1) {
        //普通方法不校验
        if (request == null) return;

        //排除file接口
        if (CollectionUtil.isNotEmpty(validFilter) &&
                validFilter.stream().filter(item -> item.equalsIgnoreCase(request.getRequestURI()) || (item.endsWith("**") && request.getRequestURI().startsWith(item.replace("**", ""))))
                        .count() > 0) {
            return;
        }

        Integer timeOffset = 60 * 5;

        String token = request.getHeader(RequestConstant.T_ACCESS_TOKEN);
        String uuid = request.getHeader(RequestConstant.T_UUID);
        String time = request.getHeader(RequestConstant.T_TIME);
        String id = StrUtil.isNotBlank(token) ? "request:token:" + token : "request:uuid" + uuid;
        //校验用户标识和时间戳有效性
        if (StrUtil.isBlank(token) && StrUtil.isBlank(uuid)) {
            throw new T4CloudBadRequestException("请求需要携带客户端标识信息");
        }
        if (StrUtil.isBlank(time)) {
            throw new T4CloudBadRequestException("请求需要携带时间戳");
        }

        Long timeRequest = null;
        try {
            timeRequest = Long.parseLong(time);
        } catch (Exception e) {
            throw new T4CloudBadRequestException("时间戳解析异常！请检查T-TIME参数！");
        }

        long l = (time1 - timeRequest) / 1000;
        //判断是否有效,允许误差，提前30秒或延迟30秒
        if (l > timeOffset || l < -1 * timeOffset) {
            throw new T4CloudBadRequestException("请求已失效，误差" + l + "秒,建议检查网络情况或检查当前系统（手机)时间是否异常……");
        }

        //生成KEY
        String redisKey = id + StrUtil.C_COLON + request.getRequestURI() + StrUtil.C_COLON + timeRequest;
        String param = JSONUtil.toJsonStr(request.getParameterMap());

        //进缓存校验
        Object o = RedisUtil.get(redisKey);
        if (o != null && o.toString().equalsIgnoreCase(param)) {
            throw new T4CloudBadRequestException("请勿重复提交……");
        } else {
            /** TODO 如果有必要的话，还可以在此处再添加一层校验来限流，例如5秒内不能重复请求
             *
             * -by TeaR  -2020/3/25-17:46
             */
            //通过校验，将本次请求写入缓存
            RedisUtil.set(redisKey, param, 15);
        }
    }

}
