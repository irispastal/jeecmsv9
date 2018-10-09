package com.jeecms.cms.api.admin.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsOss;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsConfigMng;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.manager.CmsOssMng;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;



@Controller
public class CmsSiteApiAct {
	private static final Logger log = LoggerFactory.getLogger(CmsSiteApiAct.class);
	
	@RequestMapping("/site/list")
	public void list(Integer root,HttpServletRequest request,HttpServletResponse response){
		List<CmsSite> list = null;
		JSONArray jsonArray = new JSONArray();
		if (root==null) {
			list = manager.getTopList();
		}else{
			list = manager.getListByParent(root);
		}
		if (list!=null) {
			for(int i = 0 ; i<list.size();i++){
				jsonArray.put(i,list.get(i).convertToJson());
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/site/get")
	public void get(Integer id,Integer root,HttpServletRequest request,HttpServletResponse response){
		CmsSite site = null;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, id);
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		if (!errors.hasErrors()) {
			if (id.equals(0)) {
				site = new CmsSite();
				site.init();
			}else{
				site = manager.findById(id);;
			}
			if (site!=null) {
				JSONObject json = site.convertToJson();
				json.put("root", root);
				body = json.toString();
				code = ResponseCode.API_CODE_CALL_SUCCESS;
				message = Constants.API_MESSAGE_SUCCESS;
			}else{
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/site/save")
	public void save(CmsSite bean,Integer ossId,Integer syncPageFtpId,Integer uploadFtpId,Integer root,
			HttpServletRequest request,HttpServletResponse response) throws IOException{
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, bean.getName(),
				bean.getDomain(),bean.getPath());
		if (!errors.hasErrors()) {
			CmsSite site = CmsUtils.getSite(request);
			CmsUser user = CmsUtils.getUser(request);
			if (root!=null) {
				CmsSite parent = manager.findById(root);
				if (parent!=null) {
					bean.setParent(parent);
				}
			} 
			if (ossId!=null) {
				CmsOss oss = ossMng.findById(ossId);
				if (oss==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					code = ResponseCode.API_CODE_NOT_FOUND;
				}else{
					bean.setUploadOss(oss);
				}
			}
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
			}else{
				// 加上config信息
				bean.setConfig(configMng.get());
				bean.init();
				//判断是否为内网，是内网则判断访问路径是否已存在，外网判断域名是否存在
				CmsConfig config = configMng.get();
				if (!config.getInsideSite()) {
					CmsSite checkDomain = manager.findByDomain(bean.getDomain());
					if (checkDomain!=null) {
						errors.addErrorString(Constants.API_MESSAGE_DOMAIN_EXIST);
					}
				}
				if(StringUtils.isNotBlank(bean.getAccessPath())){
					CmsSite checkAccess = manager.findByAccessPath(bean.getAccessPath());
					if (checkAccess!=null) {
						errors.addErrorString(Constants.API_MESSAGE_ACCESSPATH_EXIST);
					}
				}
				if (errors.hasErrors()) {
					message = errors.getErrors().get(0);
					if (message.equals(Constants.API_MESSAGE_DOMAIN_EXIST)) {
						code = ResponseCode.API_CODE_DOMAIN_EXIST;
					}else if (message.equals(Constants.API_MESSAGE_ACCESSPATH_EXIST)) {
						code = ResponseCode.API_CODE_ACCESSPATH_EXIST;
					}
				}else{
					bean = manager.save(site, user, bean,uploadFtpId,syncPageFtpId);
					log.info("save CmsSite id={}",bean.getId());
					cmsLogMng.operating(request, "cmsSite.log.save", "id=" + bean.getId()
					+ ";name=" + bean.getName());
					body = "{\"id\":"+"\""+bean.getId()+"\"}";
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/site/update")
	public void update(Integer root,CmsSite bean,Integer ossId,Integer uploadFtpId,Integer syncPageFtpId
			,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,bean.getId(), bean.getName(),
				bean.getDomain(),bean.getPath());
		if (!errors.hasErrors()) {
			//判断是否为内网，是内网则判断访问路径是否已存在，外网判断域名是否存在
			CmsConfig config = configMng.get();
			if (!config.getInsideSite()) {
				CmsSite checkDomain = manager.findByDomain(bean.getDomain());
				if (checkDomain!=null) {
					//若已存在，修改操作需要判断id是否相同
					if (!checkDomain.getId().equals(bean.getId())) {
						errors.addErrorString(Constants.API_MESSAGE_DOMAIN_EXIST);
					}
				}
			}
			if(StringUtils.isNotBlank(bean.getAccessPath())){
				CmsSite checkAccess = manager.findByAccessPath(bean.getAccessPath());
				if (checkAccess!=null) {
					//若已存在，修改操作需要判断id是否相同
					if (!checkAccess.getId().equals(bean.getId())) {
						errors.addErrorString(Constants.API_MESSAGE_ACCESSPATH_EXIST);
					}
				}
			}
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				if (message.equals(Constants.API_MESSAGE_DOMAIN_EXIST)) {
					code = ResponseCode.API_CODE_DOMAIN_EXIST;
				}else if (message.equals(Constants.API_MESSAGE_ACCESSPATH_EXIST)) {
					code = ResponseCode.API_CODE_ACCESSPATH_EXIST;
				}
			}else{
				if (ossId!=null) {
					CmsOss oss = ossMng.findById(ossId);
					if (oss==null) {
						errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
						code = ResponseCode.API_CODE_NOT_FOUND;
					}
				}
				if (errors.hasErrors()) {
					message = errors.getErrors().get(0);
				}else{
					bean = manager.update(bean, uploadFtpId, syncPageFtpId,ossId);
					log.info("update CmsSite id={}",bean.getId());
					cmsLogMng.operating(request, "cmsSite.log.update", "id=" + bean.getId()
					+ ";name=" + bean.getName());
					body = "{\"id\":"+"\""+bean.getId()+"\"}";
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/site/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				CmsSite[] deleteByIds = manager.deleteByIds(idArray);
				for(int i = 0 ; i <deleteByIds.length ; i++){
					log.info("delete CmsSite id={}",deleteByIds[i].getId());
					cmsLogMng.operating(request, "cmsSite.log.delete", "id="
							+ deleteByIds[i].getId() + ";name=" + deleteByIds[i].getName());
				}
				message=Constants.API_MESSAGE_SUCCESS;
				code=ResponseCode.API_CODE_CALL_SUCCESS;
			} catch (Exception e) {
				message = Constants.API_MESSAGE_DELETE_ERROR;
				code = ResponseCode.API_CODE_DELETE_ERROR;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/site/refer")
	public void refer(Integer id,String ids,HttpServletRequest request,
			HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, id,ids);
		if (!errors.hasErrors()) {
			Integer[] referIds = StrUtils.getInts(ids);
			CmsSite site = manager.findById(id);
			if (site==null) {
				message = Constants.API_MESSAGE_OBJECT_NOT_FOUND;
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				manager.updateRefers(id, referIds);
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/site/tree")
	public void getTree(String root,Boolean all,HttpServletRequest request,HttpServletResponse response){
		List<CmsSite> siteList;
		JSONArray jsonArray = new JSONArray();
		if (all!=null&&all) {
			siteList=manager.getList();
		}else{
			if (StringUtils.isBlank(root)||"source".equals(root)) {
				siteList = manager.getTopList();
			}else{
				siteList = manager.getListByParent(Integer.parseInt(root));
			}
		}
		if (siteList!=null) {
			for(int i=0;i<siteList.size();i++){
				JSONObject json = getJson(siteList.get(i));
				jsonArray.put(i,json);
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/site/check_master")
	public void checkMaster(Integer siteId,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		List<CmsSite> list = manager.getListByMaster(true);
		if (list.size()>0) {
			if (siteId!=null) {
				CmsSite site = manager.findById(siteId);
				if (list.contains(site)) {
					result = true;
				}else{
					result = false;
				}
			}else{
				result = false;
			}
		}else{
			result = true;
		}
		JSONObject json = new JSONObject();
		json.put("result", result);
		message = Constants.API_MESSAGE_SUCCESS;
		code = ResponseCode.API_CODE_CALL_SUCCESS;
		body = json.toString();
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/site/check_accessPath")
	public void checkAccessPath(Integer siteId,String accessPath,
			HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		JSONObject json = new JSONObject();
		WebErrors errors = WebErrors.create(request);
		CmsSite currSite=CmsUtils.getSite(request);
		CmsConfig config=currSite.getConfig();
		if(config.getInsideSite()){
			errors = ApiValidate.validateRequiredParams(request, errors,accessPath);
			if (!errors.hasErrors()) {
				CmsSite site = manager.findByAccessPath(accessPath);
				if (site==null) {
					result=true;
				}else{
					if (site.getId().equals(siteId)) {
						result=true;
					}
				}
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}else{
			if (StringUtils.isBlank(accessPath)) {
				result = true;
			}else{
				CmsSite site = manager.findByAccessPath(accessPath);
				if (site==null) {
					result=true;
				}else{
					if (site.getId().equals(siteId)) {
						result=true;
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
	
	@RequestMapping("/site/check_domain")
	public void checkDomain(Integer siteId,String domain,
			HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		JSONObject json = new JSONObject();
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,domain);
		if (!errors.hasErrors()) {
			CmsConfig config = configMng.get();
			if (config.getInsideSite()) {
				result=true;
			}else{
				CmsSite site = manager.findByDomain(domain);
				if (site==null) {
					result=true;
				}else{
					if (site.getId().equals(siteId)) {
						result=true;
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
	
	private JSONObject getJson(CmsSite site){
		JSONObject json = new JSONObject();
		if (site.getId()!=null) {
			json.put("id", site.getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(site.getName())) {
			json.put("name", site.getName());
		}else{
			json.put("name", "");
		}
		if (StringUtils.isNotBlank(site.getShortName())) {
			json.put("shortName", site.getShortName());
		}else{
			json.put("shortName", "");
		}
		if (site.getChild()!=null&&site.getChild().size()>0) {
			JSONArray jsonArray = new JSONArray();
			Set<CmsSite> child = site.getChild();
			List<CmsSite>childList=new ArrayList<CmsSite>(child);
			Collections.sort(childList, new Comparator<CmsSite>() {
	            @Override
	            public int compare(CmsSite o1, CmsSite o2) {
	                return o1.getId()<o2.getId() ? -1 :1;
	            }
	        });
			for (int j = 0; j < childList.size(); j++) {
				CmsSite c = childList.get(j);
				jsonArray.put(j,getJson(c));
			}
			json.put("hasChild", true);
			json.put("child", jsonArray);
		}else{
			json.put("hasChild", false);
		}
		if(site.getParent()!=null){
			json.put("parentId", site.getParent().getId());
		}else{
			json.put("parentId", "");
		}
		return json;
	}
	
	@Autowired
	private CmsOssMng ossMng;
	@Autowired
	private CmsConfigMng configMng;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsSiteMng manager;
}
