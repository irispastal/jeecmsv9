package com.jeecms.cms.api;

import com.jeecms.cms.entity.main.ApiAccount;
import com.jeecms.cms.entity.main.ApiRecord;
import com.jeecms.cms.manager.main.ApiAccountMng;
import com.jeecms.cms.manager.main.ApiRecordMng;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.web.WebErrors;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Date;

public class ValidationUtil {

    public static void validateSmsCode(Integer type, String smsCode, WebErrors errors, HttpServletRequest request, HttpServletResponse response, SessionProvider session) {
        String code;
        String time;

        switch (type) {
            case 1:
                code = "AUTO_CODE";
                time = "AUTO_CODE_CREAT_TIME";
                break;
            case 2:
                code = "FORGOTPWD_AUTO_CODE";
                time = "FORGOTPWD_AUTO_CODE_CREAT_TIME";
                break;
            case 3:
                code = "RESETPWD_AUTO_CODE";
                time = "RESETPWD_AUTO_CODE_CREAT_TIME";
                break;
            default:
                code = "LOGIN_AUTO_CODE";
                time = "LOGIN_AUTO_CODE_CREAT_TIME";
        }

        Serializable autoCodeTime = session.getAttribute(request, time);// 验证码有效时间
        Serializable autoCode = session.getAttribute(request, code);// 验证码值
        // 判断验证码是否在有效时间范围
        if (autoCodeTime != null && autoCode != null) {
            Long effectiveTime = Long.parseLong(autoCodeTime.toString());
            if (effectiveTime > new Date().getTime()) {
                // 验证码验证码是否正确
                if (smsCode.equals(autoCode.toString())) {
                    session.setAttribute(request, response, time, null);
                } else {
                    // 验证码不正确
                    errors.addErrorString("error.invalidSmsCode");
                }
            } else {
                // 验证码失效
                errors.addErrorString("error.invalidSmsCode");
            }
        } else {
            // 验证码错误
            errors.addErrorString("error.invalidSmsCode");
        }
    }

    /**
     *
     * @param request
     * @param errors
     * @param appId
     * @param sign
     * @return
     * 			0-验签通过
     * 			1-参数缺失
     * 			2-api 账号失效
     * 			3-签名错误
     * 			4-请求重复
     */

    public static int validateSign(ApiRecordMng apiRecordMng, ApiAccountMng apiAccountMng, HttpServletRequest request, WebErrors errors, String appId, String sign) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(sign)) {
            errors.addErrorString(Constants.API_MESSAGE_PARAM_REQUIRED);
            return 1;
        }

        // api account校验
        ApiAccount apiAccount = apiAccountMng.findByAppId(appId);
        ApiValidate.validateApiAccount(request, errors, apiAccount);
        if (errors.hasErrors()) {
            errors.addErrorString("error.apiAccountDisable");
            return 2;
        }
        // 签名校验
        ApiValidate.validateSign(request, errors, apiAccount, sign);
        if (errors.hasErrors()) {
            errors.addErrorString("error.signInvalid");
            return 3;
        }

        // 签名数据不可重复
        ApiRecord record=apiRecordMng.findBySign(sign, appId);
        if(record!=null){
            errors.addErrorString("error.requestRepeat");
            return 4;
        }

        return 0;
    }
}
