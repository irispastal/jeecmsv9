package com.jeecms.cms.api.admin.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.jeecms.cms.entity.main.ApiAccount;
import com.jeecms.cms.manager.main.ApiAccountMng;
import com.jeecms.common.util.AES128Util;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsOss;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.manager.CmsOssMng;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;

@Controller
public class CmsStatApiAct {
	private static final Logger log = LoggerFactory.getLogger(CmsStatApiAct.class);
	
	/**
	 * stat列表
	 * @param request
	 * @param response
	 */
	@RequestMapping("/stat/list")
	public void list(Integer siteId,HttpServletRequest request,HttpServletResponse response){
		List<Map<String, Object>> list = siteMng.getAttrListBySiteId(siteId);
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for(int i = 0 ; i<list.size();i++){
				jsonArray.put(i,list.get(i));
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	/**
	 * 统计新增和编辑
	 * @Title: save   
	 * @Description: TODO
	 * @param: @param siteId 
	 * @param: @param userName
	 * @param: @param password
	 * @param: @param tjToken
	 * @param: @param tjSiteId 百度统计的站点ID
	 * @param: @param request
	 * @param: @param response      
	 * @return: void
	 * @throws Exception 
	 */
	@SignValidate
	@RequestMapping("/stat/save")
	public void config(Integer siteId,String siteName,String userName,String password,
			String tjToken,String tjSiteId,HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors,siteId,siteName,userName,password,tjToken,tjSiteId);
		if (!errors.hasErrors()) {
			Map<String,String>tjMap=new HashMap<String,String>();
			ApiAccount account=apiManager.getApiAccount(request);
			//tjToken tjSiteId解密保存
			String token = AES128Util.decrypt(tjToken, account.getAesKey(), account.getIvKey());
			tjMap.put(com.jeecms.core.Constants.TONGJI_TOKEN, token);
			String tongjiSiteid = AES128Util.decrypt(tjSiteId, account.getAesKey(), account.getIvKey());
			tjMap.put(com.jeecms.core.Constants.TONGJI_SITEID, tongjiSiteid);
			tjMap.put(com.jeecms.core.Constants.USERNAME, userName);
			tjMap.put(com.jeecms.core.Constants.PASSWORD, password);
			tjMap.put(com.jeecms.core.Constants.SITENAME, siteName);
			siteMng.updateAttr(siteId, tjMap);
			cmsLogMng.operating(request, "stat.log.save", "id=" + siteId+ ";name=" + siteName);
			body = "{\"id\":"+"\""+siteId+"\"}";
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}		
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * stat删除
	 * @param ids stat编号组
	 * @param request
	 * @param response
	 */
	@SignValidate
	@RequestMapping("/stat/delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body="\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			try {
				Integer[] idArray = StrUtils.getInts(ids);
				Integer count = siteMng.deleteAttrListBySiteId(idArray);
				body = "{\"count\":"+"\""+count+"\"}";
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

	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private ApiAccountMng apiManager;
	@Autowired
	private CmsSiteMng siteMng;
}
