package com.jeecms.cms.api.admin.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ibm.db2.jcc.am.uf;
import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.cms.manager.main.CustomRecordMng;
import com.jeecms.cms.staticpage.ContentStatusChangeThread;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;

@Controller
public class CustomRecordApiAct {
	private static final Logger log = LoggerFactory.getLogger(CustomRecordApiAct.class);
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/record/page")
	public void list(Integer formId,Integer status, Integer pageNo,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		if (formId==null) {
			formId=0;
		}
		Pagination page = manager.getPage(formId,status,null,pageNo, pageSize);
		int totalCount = page.getTotalCount();
		List<CustomRecord> list = (List<CustomRecord>) page.getList();

		JSONObject keyArray = new JSONObject();	//获取展示字段
		if (formId!=null) {
			for(CustomFormFiled filed: customFormMng.findById(formId).getFileds()){
				if (filed.getDisplayInList()) {
					keyArray.put(filed.getLabel(), "attr_"+filed.getField());
				}
			}
		}
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {			
				jsonArray.put(i,list.get(i).convertToJson());
			}
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		String body = jsonArray.toString()+",\"totalCount\":"+totalCount+",\"keys\":"+keyArray.toString();
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/record/list")
	public void list(Integer formId,
			HttpServletRequest request,HttpServletResponse response){
	
		List<CustomRecord> list = manager.getList(formId);
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i).convertToJson());
				System.out.println(list.get(i).getAttr().size());
			}
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		String body = jsonArray.toString();
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/record/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		CustomRecord bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new CustomRecord();
			}else{
				bean = manager.findById(id);
			}
			if (bean!=null) {
				bean.init();
				body = bean.convertToJson().toString();
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}else{
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/record/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors= WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				CustomRecord[] beans = manager.deleteByIds(idArray);
				for (CustomRecord bean : beans) {
					log.info("delete CustomRecord id={}", bean.getId());
				}
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			} catch (Exception e) {
				message = Constants.API_MESSAGE_DELETE_ERROR;
				code = ResponseCode.API_CODE_DELETE_ERROR;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 审核
	 * @param ids
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/record/check")
	public void check(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		CmsUser user = CmsUtils.getUser(request);
		boolean result = false;
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			CustomRecord[] beans = manager.check(idArr, user);
			result = true;
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		body = "{\"result\":"+result+"}";
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	
	
	
	@Autowired
	private CustomRecordMng manager;
	@Autowired
	private CustomFormMng customFormMng;
}
