package com.jeecms.cms.api.admin.assist;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentShareCheck;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.ContentShareCheckMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;

@Controller
public class CmsContentShareApiAct {
	
	@RequestMapping("/content/share_tree")
	public void tree(String root,HttpServletRequest request,HttpServletResponse response){
		boolean isRoot;
		Integer cid = null;
		Integer sid = null;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
			// 站点id以s_开头
			if (root.startsWith("s_")) {
				sid = Integer.parseInt(root.split("s_")[1]);
			} else {
				cid = Integer.parseInt(root);
			}
		}
		List<CmsSite> siteList = cmsSiteMng.getList();
		// 共享针对的是将本站信息共享到其他站点
		siteList.remove(CmsUtils.getSite(request));
		List<Channel> list = null;
		JSONArray jsonArray = new JSONArray();
		if (!isRoot) {
			if (sid != null) {
				list = channelMng.getTopList(sid, true);
			} else {
				list = channelMng.getChildList(cid, true);
			}
			if (list!=null && list.size()>0) {
				for(int i = 0 ; i < list.size(); i++){
					jsonArray.put(i,createChannelJson(list.get(i)));
				}
			}
		}else{
			if (siteList!=null && siteList.size()>0) {
				for(int i = 0 ; i < siteList.size(); i++){
					jsonArray.put(i,createSiteJson(siteList.get(i)));
				}
			}
		}
		JSONArray siteArr = new JSONArray();
		if (siteList!=null && siteList.size()>0) {
			for(int i = 0 ; i < siteList.size(); i++){
				siteArr.put(i,createSiteJson(siteList.get(i)));
			}
		}
		JSONObject json = new JSONObject();
		json.put("list", jsonArray);
		json.put("siteList", siteArr);
		String body = json.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/content/push")
	public void push(String ids,String channelIds,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids , channelIds);
		boolean result = false;
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			Integer[] channelIdArr = StrUtils.getInts(channelIds);
			errors = validatePush(errors, idArr, channelIdArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				ContentShareCheck shareCheck;
				Content content;
				for (Integer contentId : idArr) {
					content = manager.findById(contentId);
					for (Integer channelId : channelIdArr) {
						List<ContentShareCheck> li = contentShareCheckMng
								.getList(contentId, channelId);
						shareCheck = new ContentShareCheck();
						shareCheck.setCheckStatus(ContentShareCheck.PUSHED);
						shareCheck.setShareValid(true);
						if (li == null || li.size() <= 0) {
							contentShareCheckMng.save(shareCheck, content,channelMng.findById(channelId),CmsUtils.getUser(request));
						}
						//添加副栏目
						manager.updateByChannelIds(contentId, channelIdArr,Content.CONTENT_CHANNEL_ADD);
					}
					result = true;
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}
			}
		}
		body = "{\"result\":"+result+"}";
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/content/share_list")
	public void list(String title, Byte status, Integer siteId,
			Integer targetSiteId, Integer pageNo,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		CmsSite site = CmsUtils.getSite(request);
		if(siteId!=null&&siteId.equals(0)){
			siteId=null;
		}
		if(targetSiteId!=null&&targetSiteId.equals(0)){
			targetSiteId=null;
		}
		if(status!=null&&status==-1){
			status=null;
		}
		if (pageNo==null) {
			pageNo=1;
		}
		if (pageSize==null) {
			pageSize=10;
		}
		Pagination page = contentShareCheckMng.getPageForShared(title, status, siteId, targetSiteId, 
				site.getId(), pageNo, pageSize);
		int totalCount = page.getTotalCount();
		List<ContentShareCheck> list = (List<ContentShareCheck>) page.getList();
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
	@RequestMapping("/content/share_check")
	public void check(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = Constants.API_MESSAGE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		ContentShareCheck shareCheck;
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			errors = validateExist(errors, idArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				if(idArr!=null&&idArr.length>0){
					for(Integer id:idArr){
						shareCheck=contentShareCheckMng.findById(id);
						//非本站源内容 而且是待审核的共享信息
						if(!shareCheck.getContent().getSite().equals(CmsUtils.getSite(request))&&shareCheck.getCheckStatus()==ContentShareCheck.CHECKING){
							shareCheck.setCheckStatus(ContentShareCheck.CHECKED);
							shareCheck.setShareValid(true);;
							//添加副栏目
							if(shareCheck.getContent()!=null&&shareCheck.getChannel()!=null){
								manager.updateByChannelIds(shareCheck.getContent().getId(), 
										new Integer[]{shareCheck.getChannel().getId()},Content.CONTENT_CHANNEL_ADD);
							}
						}
						contentShareCheckMng.update(shareCheck);
					}
				}
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/content/share_delete")
	public void delete(String ids,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = Constants.API_MESSAGE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, ids);
		if (!errors.hasErrors()) {
			Integer[] idArr = StrUtils.getInts(ids);
			errors = validateExist(errors, idArr);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_NOT_FOUND;
			}else{
				contentShareCheckMng.deleteByIds(idArr);
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private WebErrors validateExist(WebErrors errors,Integer[] idArr){
		if (idArr!=null) {
			for (int i = 0; i < idArr.length; i++) {
				ContentShareCheck bean = contentShareCheckMng.findById(idArr[i]);
				if (bean==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		return errors;
	}
	
	private WebErrors validatePush(WebErrors errors,Integer[] idArr,Integer[] channelIdArr){
		if (idArr!=null&&idArr.length>0) {
			for (int i = 0; i < idArr.length; i++) {
				Content content = manager.findById(idArr[i]);
				if (content==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		if (channelIdArr!=null&&channelIdArr.length>0) {
			for (int i = 0; i < channelIdArr.length; i++) {
				Channel channel = channelMng.findById(channelIdArr[i]);
				if (channel==null) {
					errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
					return errors;
				}
			}
		}
		return errors;
	}
	
	private JSONObject createSiteJson(CmsSite site){
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
		if (site.getChild()!=null) {
			json.put("hasChild", true);
			Set<CmsSite> set = site.getChild();
			JSONArray jsonArray = new JSONArray();
			for (CmsSite s : set) {
				jsonArray.put(createSiteJson(s));
			}
			json.put("child", jsonArray);
		}else{
			json.put("hasChild", false);
		}
		return json;
	}
	
	private JSONObject createChannelJson(Channel channel) {
		JSONObject json = new JSONObject();
		if (channel.getId()!=null) {
			json.put("id", channel.getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(channel.getName())) {
			json.put("name", channel.getName());
		}else{
			json.put("name", "");
		}
		if (channel.getChild()!=null) {
			json.put("hasChild", true);
			Set<Channel> set = channel.getChild();
			JSONArray jsonArray = new JSONArray();
			for (Channel c : set) {
				jsonArray.put(createChannelJson(c));
			}
			json.put("child", jsonArray);
		}else{
			json.put("hasChild", false);
		}
		return json;
	}
	
	@Autowired
	private CmsSiteMng cmsSiteMng;
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private ContentMng manager;
	@Autowired
	private ContentShareCheckMng contentShareCheckMng;
}
