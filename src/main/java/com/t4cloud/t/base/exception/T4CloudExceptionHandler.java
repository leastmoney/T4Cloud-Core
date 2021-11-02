package com.t4cloud.t.base.exception;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netflix.client.ClientException;
import com.t4cloud.t.base.entity.dto.R;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.service.entity.SysPermission;
import com.t4cloud.t.service.mapper.CommonPermissionMapper;
import feign.FeignException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 全局异常处理器
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 16:00
 */
@RestControllerAdvice
@Slf4j
public class T4CloudExceptionHandler {

    @Autowired
    private CommonPermissionMapper permissionMapper;

    // ----------------------------------------------- 前端异常 -----------------------------------------------

    @ExceptionHandler(NoHandlerFoundException.class)
    public R<?> handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error(404, "资源/路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(T4CloudValidException.class)
    public R<?> handlerT4CloudValidException(T4CloudValidException e) {
        log.error(e.getMessage(), e);
        return R.error(400, "调用参数异常：" + e.getMessage());
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return R.error("文件大小超出指定的大小限制, 请压缩或降低文件质量! ");
    }

    @ExceptionHandler({AuthorizationException.class, UnauthorizedException.class})
    public R<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage(), e);

        String error = e.getMessage();
        String value = error.split("\\[")[1].replace("]" , "");

        QueryWrapper<SysPermission> sysPermissionQueryWrapper = new QueryWrapper<>();

        sysPermissionQueryWrapper.lambda()
                .eq(SysPermission::getPerms, value)
                .eq(SysPermission::getStatus, 1)
                .eq(SysPermission::getMenuType, 2);

        SysPermission sysPermission = permissionMapper.selectOne(sysPermissionQueryWrapper);

        if (sysPermission == null) {
            return R.noAuth("不存在相关权限，请检查是否数据异常！权限编码：" + value);
        } else {
            String name = sysPermission.getName();
            return R.noAuth("用户无相关权限，请检查！权限信息：" + name + " [ " + value + " ]");
        }


    }

    // ----------------------------------------------- 服务异常 -----------------------------------------------


    @ExceptionHandler(NullPointerException.class)
    public R<?> handlerNullPointerException(NullPointerException e) {
        log.error(e.getMessage(), e);
        return R.error(500, "空指针处理异常，请联系开发人员捕捉该异常");
    }

    @ExceptionHandler(PoolException.class)
    public R<?> handlePoolException(PoolException e) {
        log.error(e.getMessage(), e);
        return R.error("Redis 连接异常!");
    }

    @ExceptionHandler(MinioException.class)
    public R<?> handleMinioException(MinioException e) {
        log.error(e.getMessage(), e);
        return R.error("Minio异常!" + e.getMessage());
    }

    @ExceptionHandler(T4CloudNotFoundException.class)
    public R<?> handleException(T4CloudNotFoundException e) {
        log.error(e.getMessage(), e);
        return R.notFound("404!" + e.getMessage());
    }


    // ----------------------------------------------- 数据库异常 -----------------------------------------------

    @ExceptionHandler(DuplicateKeyException.class)
    public R<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return R.error("唯一属性冲突，数据库中已存在该记录");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public R<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return R.error("SQL数据处理异常,请检查：" + e.getMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public R<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        return R.error("SQL 查询异常，建议检查实体类属性");
    }

    @ExceptionHandler({TooManyResultsException.class})
    public R<?> handleTooManyResultsException(TooManyResultsException e) {
        log.error(e.getMessage(), e);
        return R.error("存在多个符合条件的对象，请检查查询条件或换用list方法");
    }

    @ExceptionHandler({MyBatisSystemException.class})
    public R<?> handleTooManyResultsException(MyBatisSystemException e) {
        log.error(e.getMessage(), e);
        return R.error("Mybatis 异常，请检查SQL执行结果");
    }

    // ----------------------------------------------- 服务请求异常 -----------------------------------------------

    /**
     * 处理Feign异常
     */
    @ExceptionHandler(FeignException.class)
    public R<?> FeignException(FeignException e) {
        log.error(e.getMessage(), e);
        return R.error("微服务调用异常：" + e.getMessage());
    }

    /**
     * 处理Feign异常
     */
    @ExceptionHandler(ClientException.class)
    public R<?> ClientException(ClientException e) {
        log.error(e.getMessage(), e);
        return R.error("该微服务暂不可用：" + e.getMessage());
    }


    /**
     * 请求异常
     */
    @ExceptionHandler(T4CloudBadRequestException.class)
    public R<?> handleT4CloudBadRequestException(T4CloudBadRequestException e) {
        log.error(e.getMessage());
        return R.error(400, e.getMessage());
    }

    /**
     * 请求异常,打印完整请求头
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        //获取request
        HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        //获取请求头
        JSONObject object = new JSONObject();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            object.set(key, value);
        }
//        log.debug(String.format("HttpRequestMethodNotSupportedException : {%s}",object.toString()));

        String method = request.getMethod();
        String htttpType = request.getProtocol();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String uri = request.getRequestURI();
        String url = request.getRequestURL().toString();
        String remoteAddr = request.getRemoteAddr();

        log.debug("===============================================================================================");
        log.debug(String.format("HttpRequestMethodNotSupportedException详细，响应方法: {%s},通信协议: {%s},项目名称: {%s}," + "Servlet项目名: {%s},URI资源定位: {%s}, URL路径定位: {%s},请求方的ip: {%s}",method,htttpType,contextPath,servletPath,uri,url,remoteAddr));
        log.debug(String.format("HttpRequestMethodNotSupportedException参数记录 : {%s}",object.toString()));
        log.debug("===============================================================================================");

        return R.error(400, "接口调用方法异常："+e.getMessage());
    }

    // ----------------------------------------------- 全部异常捕获 -----------------------------------------------

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(T4CloudServiceException.class)
    public R<?> handleT4CloudServiceException(T4CloudServiceException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(T4CloudException.class)
    public R<?> handleT4CloudException(T4CloudException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error("操作失败，" + e.getMessage());
    }


}
