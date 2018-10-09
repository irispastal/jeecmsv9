package com.jeecms.cms.api.admin.main;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.jeecms.cms.entity.main.CmsModel;
import com.jeecms.cms.entity.main.CmsModelItem;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.cms.manager.main.CustomFormFiledMng;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.core.web.WebErrors;


@Controller
public class CustomFiledApiAct {
	private static final Logger log = LoggerFactory
			.getLogger(CustomFiledApiAct.class);
	
	@RequestMapping("/filed/list")
	public void list(Integer formId,
			HttpServletRequest request,HttpServletResponse response){
		List<CustomFormFiled> list = manager.getList(null,formId);
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for(int i = 0 ; i < list.size() ; i++){
				jsonArray.put(i,list.get(i).convertToJsonList());
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/filed/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		CustomFormFiled bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new CustomFormFiled();
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
	@RequestMapping("/filed/save")
	public void save(CustomFormFiled bean,Integer formId,HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean fieldExit = false;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,bean.getDataType(), bean.getField(),formId,
				bean.getLabel(),bean.getPriority(),bean.getRequired());
		if (!errors.hasErrors()) {
			if (!StringUtils.isBlank(bean.getOptValue())) {
				bean.setOptValue(replaceLocaleSplit(bean.getOptValue(), request));
			}
			List<CustomFormFiled> list = manager.getList(null,formId);
			if (list!=null&&list.size()>0) {
				for(int i=0;i<list.size();i++){
					if (list.get(i).getField().equals(bean.getField())) {
						fieldExit = true;
						break;
					}
				}
			}
			if (fieldExit) {//判断字段是否已存在
				message = Constants.API_MESSAGE_FIELD_EXIST;
				code = ResponseCode.API_CODE_FIELD_EXIST;
			}else{
				bean.init();
				bean = manager.save(bean,formId);
				log.info("save CustomFormFiled id={}.", bean.getId());
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/filed/update")
	public void update(CustomFormFiled bean,Integer formId,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		WebErrors errors = WebErrors.create(request);		
		errors = ApiValidate.validateRequiredParams(request, errors,bean.getDataType(), bean.getField(),formId,
				bean.getLabel(),bean.getPriority(),bean.getRequired());

		if (!errors.hasErrors()) {
			List<CustomFormFiled> list = manager.getList(null,formId);
			if (list!=null&&list.size()>0) {
				for(int i=0;i<list.size();i++){
					if (list.get(i).getField().equals(bean.getField())
							&&!list.get(i).getId().equals(bean.getId())) {
						result = true;
						break;
					}
				}
			}
			if (result) {
				message = Constants.API_MESSAGE_FIELD_EXIST;
				code = ResponseCode.API_CODE_FIELD_EXIST;
			}else{
				bean = manager.updateByUpdater(bean);
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/filed/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors= WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				CustomFormFiled[] beans = manager.deleteByIds(idArray);
				for (CustomFormFiled bean : beans) {
					log.info("delete CustomFormFiled id={}", bean.getId());
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
	
	
	@RequestMapping("/filed/check_field")
	public void checkField(Integer formId,String field,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, formId,field);
		JSONObject json = new JSONObject();
		boolean result = false;
		if (!errors.hasErrors()) {
			List<CustomFormFiled> list = manager.getList(null,formId);
			if (list!=null&&list.size()>0) {
				for(int i=0;i<list.size();i++){
					if (list.get(i).equals(field)) {
						result = true;
						break;
					}
				}
			}
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		json.put("result", result);
		body = json.toString();
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 表单批量保存
	 * @param ids
	 * @param priorities
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/filed/priority")
	public void priority(String ids,String priorities,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,ids,priorities);
		if (!errors.hasErrors()) {
			Integer[] wids = StrUtils.getInts(ids);
			Integer[] priority = StrUtils.getInts(priorities);
			errors = validatePriority(wids, priority, request);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_CALL_FAIL;
			}else{
				manager.updatePriority(wids, priority);
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	
	private String replaceLocaleSplit(String s, HttpServletRequest request) {
		String split = MessageResolver.getMessage(request,
				"cmsModelItem.optValue.split");
		return StringUtils.replace(s, split, ",");
	}
	
	private WebErrors validatePriority(Integer[] wids, Integer[] priority,HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (wids.length != priority.length) {
			String s = Constants.API_MESSAGE_PARAM_ERROR;
			errors.addErrorString(s);
			return errors;
		}
		for (int i = 0, len = wids.length; i < len; i++) {
			if (vldExist(wids[i], errors)) {
				errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
				return errors;
			}
			if (priority[i] == null) {
				priority[i] = 0;
			}
	
		}
		return errors;
	}
	
	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id", false)) {
			return true;
		}
		CustomFormFiled entity = manager.findById(id);
		if (errors.ifNotExist(entity, CustomFormFiled.class, id, false)) {
			return true;
		}
		return false;
	}
	
	@Autowired
	private CustomFormFiledMng manager;
	
}
