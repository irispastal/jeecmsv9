package com.jeecms.cms.api.admin.main;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSmsRecord;
import com.jeecms.core.manager.CmsSmsRecordMng;
import com.jeecms.core.web.WebErrors;

/***
 * 
 * @Description:短信发送记录
 * @author: SirFan
 * @date:   Mar 3, 2018 1:32:47 PM     
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Controller
public class CmsSmsRecordApiAct {
	private static final Logger log = LoggerFactory.getLogger(CmsSmsRecordApiAct.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/smsRecord/list")
	public void list(Integer pageNo,Integer pageSize,Byte sms,String phone,
			Integer validateType,String username,Date drawTimeBegin,Date drawTimeEnd,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		Pagination page = manager.getPage(sms, pageNo, pageSize,phone,validateType,username,drawTimeBegin,drawTimeEnd);
		int totalCount = page.getTotalCount();
		List<CmsSmsRecord> list = (List<CmsSmsRecord>) page.getList();
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i).convertToJson());
			}
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		String body = jsonArray.toString()+",\"totalCount\":"+totalCount;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/smsRecord/delete")
	public void delete(String ids,HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			errors = validateDelete(errors,idArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				try {
					CmsSmsRecord[] beans = manager.deleteByIds(idArr);
					for (CmsSmsRecord bean : beans) {
						log.info("delete CmsSmsRecord id={}", bean.getId());
					}
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				} catch (Exception e) {
					message = Constants.API_MESSAGE_DELETE_ERROR;
					code = ResponseCode.API_CODE_DELETE_ERROR;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private WebErrors validateDelete(WebErrors errors, Integer[] idArr) {
		if (idArr!=null) {
			for (int i = 0; i < idArr.length; i++) {
				vldExist(idArr[i], errors);
				if (errors.hasErrors()) {
					return errors;
				}
			}
		}
		return errors;
	}
	
	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id", false)) {
			return true;
		}
		CmsSmsRecord entity = manager.findById(id);
		if(errors.ifNotExist(entity, CmsSmsRecord.class, id, false)) {
			return true;
		}
		return false;
	}
	
	@Autowired
	private CmsSmsRecordMng manager;
}
