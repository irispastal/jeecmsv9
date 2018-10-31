package com.jeecms.cms.api.member;

import com.alibaba.fastjson.JSON;
import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.*;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserIdcard;
import com.jeecms.core.manager.CmsUserIdcardMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class IdcardApiAct {
    static final boolean NeedSignValidation = true;;

    @SignValidate(need = NeedSignValidation)
    @RequestMapping(value = "/idcard/add", method = RequestMethod.POST)
    public void add(String smsCode,
                    CmsUserIdcard card,
                    HttpServletRequest request,
                    HttpServletResponse response) {
        CmsUser user = CmsUtils.getUser(request);
        WebErrors errors = WebErrors.create(request);

        String message = Constants.API_MESSAGE_SUCCESS;
        String code = ResponseCode.API_CODE_CALL_SUCCESS;
        String body = "\"\"";

        // 参数非空校验
        ApiValidate.validateRequiredParams(request, errors, card.getIdcard(), card.getMobile(), card.getRealname(), smsCode);

        if (user != null) {
            card.setUser(user);
            if (!errors.hasErrors()) {
                ValidationUtil.validateSmsCode(5, smsCode, errors, request, response, session);

                if (!errors.hasErrors()) {
                    cmsUserIdcardMng.save(card);
                } else {
                    // 短信验证码错误
                    message = Constants.API_MESSAGE_SMS_ERROR;
                    code = ResponseCode.API_CODE_SMS_ERROR;
                }
            } else {
                message = Constants.API_MESSAGE_PARAM_REQUIRED;
                code = ResponseCode.API_CODE_PARAM_REQUIRED;
            }
        } else {
            message = Constants.API_MESSAGE_USER_NOT_LOGIN;
            code = ResponseCode.API_CODE_USER_NOT_LOGIN;
        }

        ApiResponse apiResponse=new ApiResponse(request, body, message,code);
        ResponseUtils.renderApiJson(response, request, apiResponse);
    }

    @SignValidate(need = NeedSignValidation)
    @RequestMapping(value = "/idcard/delete", method = RequestMethod.POST)
    public void delete(Integer id, HttpServletRequest request, HttpServletResponse response) {
        CmsUser user = CmsUtils.getUser(request);
        WebErrors errors = WebErrors.create(request);

        String message = Constants.API_MESSAGE_SUCCESS;
        String code = ResponseCode.API_CODE_CALL_SUCCESS;
        String body = "\"\"";

        // 参数非空校验
        ApiValidate.validateRequiredParams(request, errors, id);

        if (!errors.hasErrors()) {
            CmsUserIdcard idcard = cmsUserIdcardMng.findById(id);
            if (idcard != null) {
                if (user.getUserIdcardSet().contains(idcard)) {
                    cmsUserIdcardMng.deleteById(id);
                } else {
                    message = Constants.API_MESSAGE_USER_NOT_HAS_PERM;
                    code = ResponseCode.API_CODE_USER_NOT_HAS_PERM;
                }
            } else {
                message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
                code = ResponseCode.API_CODE_NOT_FOUND;
            }
        } else {
            message = Constants.API_MESSAGE_PARAM_REQUIRED;
            code = ResponseCode.API_CODE_PARAM_REQUIRED;
        }

        ApiResponse apiResponse=new ApiResponse(request, body, message,code);
        ResponseUtils.renderApiJson(response, request, apiResponse);
    }

    @SignValidate(need = NeedSignValidation)
    @RequestMapping(value = "/idcard/update", method = RequestMethod.POST)
    public void update(CmsUserIdcard card, HttpServletRequest request, HttpServletResponse response) {
        CmsUser user = CmsUtils.getUser(request);
        WebErrors errors = WebErrors.create(request);

        String message = Constants.API_MESSAGE_SUCCESS;
        String code = ResponseCode.API_CODE_CALL_SUCCESS;
        String body = "\"\"";

        // 参数非空校验
        ApiValidate.validateRequiredParams(request, errors, card.getId(), card.getIdcard(), card.getMobile(), card.getRealname());

        if (user != null) {
            if (contain(user.getUserIdcardSet(), card.getId())) {
                card.setUser(user);
                if (!errors.hasErrors()) {
                    cmsUserIdcardMng.update(card);
                } else {
                    message = Constants.API_MESSAGE_PARAM_REQUIRED;
                    code = ResponseCode.API_CODE_PARAM_REQUIRED;
                }
            } else {
                message = Constants.API_MESSAGE_USER_NOT_HAS_PERM;
                code = ResponseCode.API_CODE_USER_NOT_HAS_PERM;
            }

        } else {
            message = Constants.API_MESSAGE_USER_NOT_LOGIN;
            code = ResponseCode.API_CODE_USER_NOT_LOGIN;
        }

        ApiResponse apiResponse=new ApiResponse(request, body, message,code);
        ResponseUtils.renderApiJson(response, request, apiResponse);
    }

    private boolean contain(Set<CmsUserIdcard> idcards, Integer id) {
        for (CmsUserIdcard idcard : idcards) {
            if (idcard.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/idcard/page", method = RequestMethod.POST)
    public void page(Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response) {
        Integer userid = CmsUtils.getUserId(request);

        String message = Constants.API_MESSAGE_SUCCESS;
        String code = ResponseCode.API_CODE_CALL_SUCCESS;
        String body = "\"\"";

        if (pageNo == null) {
            pageNo = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        if (userid != null) {
            Map<String, Object> result = new HashMap<>();

            Pagination pagination = cmsUserIdcardMng.page(userid, pageNo, pageSize);
            List<CmsUserIdcard> idcards = (List<CmsUserIdcard>) pagination.getList();
            Integer total = pagination.getTotalCount();

            List<Object> jsonarray = new ArrayList<>();

            for (CmsUserIdcard idcard : idcards) {
                jsonarray.add(idcard.convertoJSON());
            }

            result.put("idcards", jsonarray);
            result.put("total", total);

            body = JSON.toJSONString(result);
        } else {
            message = Constants.API_MESSAGE_USER_NOT_LOGIN;
            code = ResponseCode.API_CODE_USER_NOT_LOGIN;
        }

        ApiResponse apiResponse=new ApiResponse(request, body, message,code);
        ResponseUtils.renderApiJson(response, request, apiResponse);
    }

    @Autowired
    CmsUserIdcardMng cmsUserIdcardMng;

    @Autowired
    CmsUserMng cmsUserMng;

    @Autowired
    private SessionProvider session;
}
