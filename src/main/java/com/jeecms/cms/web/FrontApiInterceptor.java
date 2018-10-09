package com.jeecms.cms.web;

import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.cms.api.ValidationUtil;
import com.jeecms.cms.manager.main.ApiAccountMng;
import com.jeecms.cms.manager.main.ApiRecordMng;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.web.WebErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Front Api 拦截器
 * 判断是否存在@SignValidate注解，并验签
 */
public class FrontApiInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        boolean pass = false;
        boolean needValidateSign = false;
        boolean strict = true;
        WebErrors errors = WebErrors.create(request);
        String code = ResponseCode.API_CODE_CALL_SUCCESS;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        SignValidate annotation = method.getAnnotation(SignValidate.class);
        if (annotation != null) {
            needValidateSign=annotation.need();
        }

        if (needValidateSign) {
            // 验证签名
            String sign=request.getParameter(Constants.COMMON_PARAM_SIGN);
            String appId=request.getParameter(Constants.COMMON_PARAM_APPID);
            int flag = ValidationUtil.validateSign(apiRecordMng, apiAccountMng, request, errors, appId, sign);

            switch (flag) {
                case 1:
                    code = ResponseCode.API_CODE_PARAM_REQUIRED;
                    break;
                case 2:
                    code = ResponseCode.API_CODE_APP_PARAM_ERROR;
                    break;
                case 3:
                    code = ResponseCode.API_CODE_SIGN_ERROR;
                    break;
                case 4:
                    code = ResponseCode.API_CODE_REQUEST_REPEAT;
                    break;
            }

            if (strict) {
                if (flag == 0) {
                    pass = true;
                    // api调用记录
                    apiRecordMng.callApiRecord(RequestUtils.getIpAddr(request), appId, request.getRequestURI(), sign);
                }
            } else {
                // 非严格模式下，不记录api调用，也不进行请求重复校验
                if (flag == 0 || flag == 4) {
                    pass = true;
                }
            }
        } else {
            pass = true;
        }

        if (!pass) {
            // 校验不通过
            String body = "\"\"";
            String message = body;
            if (errors.hasErrors()) {
                message = errors.getErrors().get(0);
            }
            ApiResponse apiResponse=new ApiResponse(request, body, message,code);
            ResponseUtils.renderApiJson(response, request, apiResponse);
        }

        return pass;
    }

    @Autowired
    ApiAccountMng apiAccountMng;

    @Autowired
    ApiRecordMng apiRecordMng;
}
