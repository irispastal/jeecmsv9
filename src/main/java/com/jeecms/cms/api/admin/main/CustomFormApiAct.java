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
import com.jeecms.cms.entity.main.CmsModel;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflow;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.manager.CmsWorkflowMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.sun.star.configuration.backend.BackendAccessException;

@Controller
public class CustomFormApiAct {
	private static final Logger log = LoggerFactory.getLogger(CustomFormApiAct.class);
	
	/**
	 * 列表
	 * @param pageNo
	 * @param pageSize
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/custom/list")
	public void list(Integer pageNo,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		CmsSite site= CmsUtils.getSite(request);
		Pagination page = manager.getPage(site.getId(),null,pageNo, pageSize);
		int totalCount = page.getTotalCount();
		List<CustomForm> list = (List<CustomForm>) page.getList();
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
	/**
	 * 表单详情
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping("/custom/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		CustomForm bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new CustomForm();
			}else{
				bean = manager.findById(id);
			}
			if (bean==null) {
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				bean.init();
				body = bean.convertToJson().toString();
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 表单添加
	 * @param bean 模型对象
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/custom/save")
	public void save(CustomForm bean,Integer workflowId,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,bean.getName()
				,bean.getPriority(),bean.getMemberMeun(),bean.getSubmitUrl(),bean.getViewUrl(),bean.getEnable(),bean.getAllSite());
		if (!errors.hasErrors()) {
				bean.init();
				bean.setSite(CmsUtils.getSite(request));
				CmsUser user=CmsUtils.getUser(request);
				bean.setUser(user);
				bean = manager.save(bean,workflowId);
				log.info("save CustomForm id={}",bean.getId());
				cmsLogMng.operating(request, "CustomForm.log.save", "id=" + bean.getId()
				+ ";name=" + bean.getName());
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse = new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 表单删除
	 * @param ids
	 * @param response
	 * @param request
	 */
	@SignValidate
	@RequestMapping("/custom/delete")
	public void delete(String ids,HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				CustomForm[] beans = manager.deleteByIds(idArray);
				for(CustomForm bean: beans){
					log.info("delete CustomForm id={}", bean.getId());
					cmsLogMng.operating(request, "CustomForm.log.delete", "id="
							+ bean.getId() + ";name=" + bean.getName());
				}
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			} catch (Exception e) {
				message = Constants.API_MESSAGE_DELETE_ERROR;
				code = ResponseCode.API_CODE_DELETE_ERROR;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request,body, message, code);
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
	@RequestMapping("/custom/priority")
	public void priority(String ids,String priorities,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids,priorities);
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
	
	/**
	 * 模型表单
	 * @param bean
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/custom/update")
	public void Update(CustomForm bean,Integer workflowId ,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request,errors,bean.getId(),bean.getName()
				,bean.getPriority(),bean.getMemberMeun(),bean.getSubmitUrl(),bean.getViewUrl(),bean.getEnable(),bean.getAllSite());
		if (!errors.hasErrors()) {
			CustomForm model = manager.findById(bean.getId());
			if (model==null) {
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				bean = manager.update(bean,workflowId);
				log.info("update CustomForm id={}",bean.getId());
				cmsLogMng.operating(request, "CustomForm.log.update", "id="
						+ bean.getId() + ";name=" + bean.getName());
				body = "{\"id\":"+"\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
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
		CustomForm entity = manager.findById(id);
		if (errors.ifNotExist(entity, CustomForm.class, id, false)) {
			return true;
		}
		return false;
	}
	
	@Autowired
	private CustomFormMng manager;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsWorkflowMng cmsWorkflowMng;
}
