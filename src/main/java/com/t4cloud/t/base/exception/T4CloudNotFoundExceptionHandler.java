package com.t4cloud.t.base.exception;

import com.t4cloud.t.base.entity.dto.R;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理404异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 16:08
 */
@Controller
public class T4CloudNotFoundExceptionHandler implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = {"/error"})
    @ResponseBody
    public Object error(HttpServletRequest request) {
        Object exception = request.getAttribute("javax.servlet.error.exception");
        if (exception == null || !(exception instanceof Exception)) {
            return R.error(404, "路径不存在，请检查路径是否正确,url:" + request.getRequestURI());
        } else {
            return R.noLogin(((Exception) exception).getMessage());
        }
    }

    @RequestMapping(value = {"/401"})
    @ResponseBody
    public Object noAuthz(HttpServletRequest request) {
        Object exception = request.getAttribute("javax.servlet.error.exception");
        if (exception == null || !(exception instanceof Exception)) {
            return R.error(404, "路径不存在，请检查路径是否正确,url:" + request.getRequestURI());
        } else {
            return R.noLogin(((Exception) exception).getMessage());
        }
    }

}