package com.jeecms.cms.api.admin.main;

import java.util.List;
import java.util.Set;

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
import com.jeecms.cms.entity.assist.CmsGuestbookCtg;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.manager.assist.CmsGuestbookCtgMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsDepartment;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsDepartmentMng;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;

@Controller
public class CmsDepartmentApiAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsDepartmentApiAct.class);
	
	/**
	 * 部门管理列表
	 * @param root
	 * @param request
	 * @param response
	 */
	@RequestMapping("/department/list")
	public void list(Integer root,HttpServletRequest request,HttpServletResponse response){
		List<CmsDepartment> list = manager.getList(root, false);
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for(int i=0;i<list.size();i++){
				jsonArray.put(i,list.get(i).convertToJson(true));
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 部门管理详情
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping("/department/get")
	public void get(Integer id,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		CmsDepartment bean = null;
		if (id!=null) {
			if (id.equals(0)) {
				bean = new CmsDepartment();
			}else{
				bean = manager.findById(id);
			}
			if (bean!=null) {
				bean.init();
				body = bean.convertToJson(false).toString();
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 添加部门
	 * @param bean
	 * @param pid
	 * @param channelIds
	 * @param controlChannelIds
	 * @param ctgIds
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/department/save")
	public void save(CmsDepartment bean,Integer pid,String channelIds,
			String controlChannelIds,String ctgIds,HttpServletRequest request,
			HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, bean.getName(),
				bean.getPriority());
		if (!errors.hasErrors()) {
			bean.init();
			if (pid!=null&&!pid.equals(0)) {
				CmsDepartment parent = manager.findById(pid);
				bean.setParent(parent);
			}
			Integer[] channelArr = StrUtils.getInts(channelIds);
			Integer[] controlChannelArr = StrUtils.getInts(controlChannelIds);
			Integer[] ctgArr = StrUtils.getInts(ctgIds);
			errors = validate(errors,channelArr,controlChannelArr,ctgArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				bean = manager.save(bean, channelArr, controlChannelArr, ctgArr);
				log.info("save CmsDepartment id={}", bean.getId());
				cmsLogMng.operating(request, "cmsdepartment.log.save", "id="
						+ bean.getId() + ";name=" + bean.getName());
				body = "{\"id\":\""+bean.getId()+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 修改部门
	 * @param bean
	 * @param pid
	 * @param channelIds
	 * @param controlChannelIds
	 * @param ctgIds
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/department/update")
	public void update(CmsDepartment bean,Integer pid,String channelIds,
			String controlChannelIds,String ctgIds,HttpServletRequest request,
			HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, bean.getId(),bean.getName(),bean.getPriority());
		if (!errors.hasErrors()) {
			CmsDepartment department = manager.findById(bean.getId());
			if (department==null) {
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				if(pid!=null){
					if(pid.equals(0)){
						bean.setParent(null);
					}else{
						CmsDepartment parent=manager.findById(pid);
						bean.setParent(parent);
					}
				}
				Integer[] channelArr = StrUtils.getInts(channelIds);
				Integer[] controlChannelArr = StrUtils.getInts(controlChannelIds);
				Integer[] ctgArr = StrUtils.getInts(ctgIds);
				errors = validate(errors,channelArr,controlChannelArr,ctgArr);
				if (errors.hasErrors()) {
					message = errors.getErrors().get(0);
					code = ResponseCode.API_CODE_NOT_FOUND;
				}else{
					bean = manager.update(bean,channelArr,controlChannelArr,ctgArr);
					log.info("update CmsDepartment id={}.", bean.getId());
					cmsLogMng.operating(request, "cmsdepartment.log.update", "id="
							+ bean.getId() + ";name=" + bean.getName());
					body = "{\"id\":\""+bean.getId()+"\"}";
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	/**
	 * 删除部门
	 * @param ids
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/department/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
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
					CmsDepartment[] beans = manager.deleteByIds(idArr);
					for (CmsDepartment bean : beans) {
						log.info("delete department id={}", bean.getId());
						cmsLogMng.operating(request, "cmsdepartment.log.delete", "id="
								+ bean.getId() + ";name=" + bean.getName());
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
	
	/**
	 * 批量保存
	 * @param ids
	 * @param priorities
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/department/priority")
	public void priority(String ids,String priorities ,
			HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids,priorities);
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			Integer[] priorityArr = StrUtils.getInts(priorities);
			errors = validatePriority(errors,idArr,priorityArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = errors.getErrors().get(1);
			}else{
				manager.updatePriority(idArr, priorityArr);
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 部门左侧tree
	 * @param root
	 * @param request
	 * @param response
	 */
	@RequestMapping("/department/tree")
	public void tree(String root,HttpServletRequest request,HttpServletResponse response){
		boolean isRoot;
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		List<CmsDepartment> departList;
		if (isRoot) {
			departList = manager.getList(null, false);
		}else{
			departList = manager.getList(Integer.parseInt(root), false);
		}
		JSONArray jsonArray = new JSONArray();
		if (departList!=null&&departList.size()>0) {
			for (int i = 0; i < departList.size(); i++) {
				jsonArray.put(i,jsonToTree(departList.get(i)));
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
//	/**
//	 * 校验部门名称是否可用
//	 * @param name
//	 * @param request
//	 * @param response
//	 */
//	@RequestMapping("/department/check_name")
//	public void checkName(String name,Integer id,HttpServletRequest request,HttpServletResponse response){
//		String body = "\"\"";
//		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
//		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
//		WebErrors errors = WebErrors.create(request);
//		errors = ApiValidate.validateRequiredParams(request, errors, name);
//		boolean result = false;
//		if (!errors.hasErrors()) {
//			//是否为添加操作(添加操作不需要给id)
//			if (id==null) {
//				CmsDepartment department = manager.findByName(name);
//				if (department==null) {
//					result = true;
//				}
//			}else{
//				CmsDepartment department = manager.findByName(name);
//				if (department!=null) {
//					//判断id是否相同
//					if (id.equals(department.getId())) {
//						result = true;
//					}
//				}else{
//					result = true;
//				}
//			}
//			message = Constants.API_MESSAGE_SUCCESS;
//			code = ResponseCode.API_CODE_CALL_SUCCESS;
//		}
//		body = "{\"result\":"+result+"}";
//		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
//		ResponseUtils.renderApiJson(response, request, apiResponse);
//	}
	
	/**
	 * 成员管理
	 * @param departId 部门编号
	 * @param root
	 * @param pageNo
	 * @param pageSize
	 * @param https
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/department/member_list")
	public void memberList(Integer departId,Integer pageNo,Integer pageSize,Integer https,
			HttpServletRequest request,HttpServletResponse response){
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize == null) {
			pageSize=20;
		}
		if (https==null) {
			https = Constants.URL_HTTP;
		}
		Pagination page = userMng.getAdminsByDepartId(departId, pageNo, pageSize);
		List<CmsUser> list = (List<CmsUser>) page.getList();
		int totalCount = page.getTotalCount();
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i).convertToJson(CmsUtils.getSite(request), https,null));
			}
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		String body = jsonArray.toString()+",\"totalCount\":"+totalCount;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private JSONObject jsonToTree(CmsDepartment bean){
		JSONObject json = new JSONObject();
		if (bean.getId()!=null) {
			json.put("id", bean.getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(bean.getName())) {
			json.put("name", bean.getName());
		}else{
			json.put("name", "");
		}
		if (bean.getChild()!=null&&bean.getChild().size()>0) {
			JSONArray jsonArray = new JSONArray();
			Set<CmsDepartment> set = bean.getChild();
			int index = 0 ;
			for (CmsDepartment department : set) {
				jsonArray.put(index,jsonToTree(department));
			}
			json.put("hasChild", true);
			json.put("child", jsonArray);
		}else{
			json.put("hasChild", false);
		}
		return json;
	}
	
	private WebErrors validatePriority(WebErrors errors, Integer[] idArr, Integer[] priorityArr) {
		if (idArr!=null) {
			for (int i = 0; i < idArr.length; i++) {
				CmsDepartment department = manager.findById(idArr[i]);
				if (department==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					errors.addErrorString(ResponseCode.API_CODE_NOT_FOUND);
					return errors;
				}
			}
		}
		if (idArr!=null&& priorityArr!=null) {
			if (idArr.length!=priorityArr.length) {
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				errors.addErrorString(ResponseCode.API_CODE_PARAM_ERROR);
				return errors;
			}
		}
		return errors;
	}

	private WebErrors validateDelete(WebErrors errors, Integer[] idArr) {
		if (idArr!=null) {
			for (int i = 0; i < idArr.length; i++) {
				CmsDepartment department = manager.findById(idArr[i]);
				if (department==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		return errors;
	}

	private WebErrors validate(WebErrors errors, Integer[] channelArr, Integer[] controlChannelArr, Integer[] ctgArr) {
		if (channelArr!=null) {
			for (int i = 0; i < channelArr.length; i++) {
				Channel channel = channelMng.findById(channelArr[i]);
				if (channel==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		if (controlChannelArr!=null) {
			for (int i = 0; i < controlChannelArr.length; i++) {
				Channel channel = channelMng.findById(controlChannelArr[i]);
				if (channel==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		if (ctgArr!=null) {
			for (int i = 0; i < ctgArr.length; i++) {
				CmsGuestbookCtg ctg = guestBookCtgMng.findById(ctgArr[i]);
				if (ctg==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		return errors;
	}


	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsDepartmentMng manager;
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private CmsGuestbookCtgMng guestBookCtgMng;
	@Autowired
	private CmsUserMng userMng;
	
	
}
