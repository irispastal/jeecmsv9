package com.jeecms.cms.api.admin.main;

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
import com.jeecms.cms.entity.main.IntelligentForm;
import com.jeecms.cms.manager.main.IntelligentFormMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.web.WebErrors;

@Controller
public class IntelligentFormApiAct {
	
	private static final Logger log = LoggerFactory.getLogger(IntelligentFormApiAct.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/intelligentForm/list")
	public void list(Integer pageNo,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		Pagination page = manager.getPage(pageNo, pageSize);
		int totalCount = page.getTotalCount();
		List<IntelligentForm> list = (List<IntelligentForm>) page.getList();
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
	
	@RequestMapping("/intelligentForm/listAll")
	public void listAll(HttpServletRequest request,HttpServletResponse response){
		List<IntelligentForm> list = manager.getList();
		int totalCount = list.size();
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
	
	@RequestMapping("/intelligentForm/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		IntelligentForm bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new IntelligentForm();
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
	@RequestMapping("/intelligentForm/delete")
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
					IntelligentForm[] beans = manager.deleteByIds(idArr);
					for (IntelligentForm bean : beans) {
						log.info("delete IntelligentForm id={}", bean.getId());
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
		IntelligentForm entity = manager.findById(id);
		if(errors.ifNotExist(entity, IntelligentForm.class, id, false)) {
			return true;
		}
		return false;
	}

	@Autowired
	private IntelligentFormMng manager;

}
