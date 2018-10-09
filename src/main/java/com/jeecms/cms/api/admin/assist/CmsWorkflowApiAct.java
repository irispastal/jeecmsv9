package com.jeecms.cms.api.admin.assist;

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
import com.jeecms.core.entity.CmsRole;
import com.jeecms.core.entity.CmsWorkflow;
import com.jeecms.core.manager.CmsRoleMng;
import com.jeecms.core.manager.CmsWorkflowMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;

@Controller
public class CmsWorkflowApiAct {
	private static final Logger log = LoggerFactory.getLogger(CmsWorkflowApiAct.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/workflow/list")
	public void list(Integer pageNo,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		Integer siteId = CmsUtils.getSiteId(request);
		Pagination page = manager.getPage(siteId, pageNo, pageSize);
		int totalCount = page.getTotalCount();
		List<CmsWorkflow> list = (List<CmsWorkflow>) page.getList();
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for(int i = 0 ; i <list.size(); i++){
				jsonArray.put(i,list.get(i).convertToJson());
			}
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		String body = jsonArray.toString()+",\"totalCount\":"+totalCount;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/workflow/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		CmsWorkflow bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new CmsWorkflow();
			}else{
				bean = manager.findById(id);
			}
			if (bean!=null) {
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
	@RequestMapping("/workflow/save")
	public void save(CmsWorkflow bean,String roleIds,String countersigns,
			HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, bean.getName(),bean.getPriority()
				,bean.getCross());
		if (!errors.hasErrors()) {
			Integer[] idArray = StrUtils.getInts(roleIds);
			Boolean[] counterArray = strToBooleanArr(countersigns);
			errors = validate(errors, idArray, counterArray);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_PARAM_ERROR;
			}else{
				if (bean.getSite()==null) {
					bean.setSite(CmsUtils.getSite(request));
				}
				bean = manager.save(bean, idArray, counterArray);
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/workflow/update")
	public void update(CmsWorkflow bean,String roleIds,String countersigns,
			HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, bean.getId(),bean.getName(),bean.getPriority(),bean.getCross());
		if (!errors.hasErrors()) {
			Integer[] roleIdArray = StrUtils.getInts(roleIds);
			Boolean[] counterArray = strToBooleanArr(countersigns);
			errors = validate(errors, roleIdArray, counterArray);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_PARAM_ERROR;
			}else{
				bean = manager.update(bean, roleIdArray, counterArray);
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/workflow/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				errors = validateDelete(errors,idArray);
				if (errors.hasErrors()) {
					message = errors.getErrors().get(0);
					code = ResponseCode.API_CODE_NOT_FOUND;
				}else{
					CmsWorkflow[] beans = manager.deleteByIds(idArray);
					for (CmsWorkflow bean : beans) {
						log.info("delete CmsWorkflow id={}", bean.getId());
					}
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}
			} catch (Exception e) {
				message = Constants.API_MESSAGE_DELETE_ERROR;
				code = ResponseCode.API_CODE_DELETE_ERROR;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/workflow/priority")
	public void priority(String ids, String priorities,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids , priorities);
		if (!errors.hasErrors()) {
			Integer[] idArray = StrUtils.getInts(ids);
			Integer[] priority = StrUtils.getInts(priorities);
			errors = validatePriority(errors, idArray, priority);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_PARAM_ERROR;
			}else{
				manager.updatePriority(idArray, priority);
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private WebErrors validateDelete(WebErrors errors,Integer[] arr){
		if (arr!=null&&arr.length>0) {
			for (int i = 0; i < arr.length; i++) {
				CmsWorkflow workflow = manager.findById(arr[i]);
				if (workflow==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		return errors;
	}
	
	private WebErrors validatePriority(WebErrors errors,Integer[] arr1 , Integer[] arr2){
		if (arr1!=null&&arr2!=null) {
			if (arr1.length!=arr2.length) {
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				return errors;
			}
			if (arr1.length>0) {
				for (int i = 0; i < arr1.length; i++) {
					CmsWorkflow workflow = manager.findById(arr1[i]);
					if (workflow==null) {
						errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
						return errors;
					}
				}
			}
		}
		return errors;
	}
	
	private WebErrors validate(WebErrors errors,Integer[] arr1 , Boolean[] arr2){
		if (arr1!=null&&arr2!=null) {
			if (arr1.length!=arr2.length) {
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				return errors;
			}
			if (arr1.length>0) {
				for (int i = 0; i < arr1.length; i++) {
					CmsRole role = roleMng.findById(arr1[i]);
					if (role==null) {
						errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
						return errors;
					}
				}
			}
		}
		return errors;
	}
	
	private Boolean[] strToBooleanArr(String arr){
		if (arr==null||arr.length()<=0) {
			return null;
		}
		String[] split = arr.split(",");
		Boolean[] booleans = new Boolean[split.length];
		for (int i = 0; i < split.length; i++) {
			booleans[i] = Boolean.parseBoolean(split[i]);
		}
		return booleans;
	}
	
	@Autowired
	private CmsWorkflowMng manager;
	@Autowired
	private CmsRoleMng roleMng;
}
