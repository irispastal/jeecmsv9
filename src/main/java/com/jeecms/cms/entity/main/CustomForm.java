package com.jeecms.cms.entity.main;

import static com.jeecms.common.web.Constants.SPT;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.cms.entity.main.base.BaseCustomForm;
import com.jeecms.core.entity.CmsSite;

public class CustomForm extends BaseCustomForm {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获得URL地址
	 * 
	 * @return
	 */
	public String getUrl() {
		if (getSite()!=null) {
			if(!StringUtils.isBlank(getSite().getDomainAlias())){
				return getUrlDynamic(null);
			}else{
				return getUrlDynamic(true);
			}
		}else {
			return null;
		}
		
	}
	
	public String getUrlDynamic(Boolean whole) {
	
		CmsSite site = getSite();
		StringBuilder url = site.getUrlBuffer(true, whole, false);
		if(site.getConfig().getInsideSite()){
			url.append("/").append(site.getAccessPath());
		}
		url.append(SPT).append("customForm");
		url.append(SPT).append(getId()).append(site.getDynamicSuffix());
		return url.toString();
	}
	
	/**
	 * 获得展示URL地址
	 * 
	 * @return
	 */
	public String getListUrl() {
		if (getSite()!=null) {
			if(!StringUtils.isBlank(getSite().getDomainAlias())){
				return getListUrlDynamic(null);
			}else{
				return getListUrlDynamic(true);
			}
		}else {
			return null;
		}
		
	}
	
	public String getListUrlDynamic(Boolean whole) {
	
		CmsSite site = getSite();
		StringBuilder url = site.getUrlBuffer(true, whole, false);
		if(site.getConfig().getInsideSite()){
			url.append("/").append(site.getAccessPath());
		}
		url.append(SPT).append("customRecord");
		url.append(SPT).append(getId()).append(site.getDynamicSuffix());
		return url.toString();
	}
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(getName())) {
			json.put("name", getName());
		}else{
			json.put("name", "");
		}
		if (getMemberMeun()!=null) {
			json.put("memberMeun", getMemberMeun());
		}else{
			json.put("memberMeun", false);
		}
		if(getPriority()!=null){
			json.put("priority", getPriority());
		}else{
			json.put("priority", "");
		}
		if(getCreateTime()!=null){
			json.put("createTime", getCreateTime());
		}else{
			json.put("createTime", "");
		}
		if(getSubmitUrl()!=null){
			json.put("submitUrl", getSubmitUrl());
		}else{
			json.put("submitUrl", "");
		}	
		if(getViewUrl()!=null){
			json.put("viewUrl", getViewUrl());
		}else{
			json.put("viewUrl", "");
		}
		if (getAllSite()!=null) {
			json.put("allSite", getAllSite());
		}else{
			json.put("allSite", false);
		}
		if (getEnable()!=null) {
			json.put("enable", getEnable());
		}else{
			json.put("enable", false);
		}
		if(getStartTime()!=null){
			json.put("startTime", getStartTime());
		}else{
			json.put("startTime", "");
		}
		if(getEndTime()!=null){
			json.put("endTime", getEndTime());
		}else{
			json.put("endTime", "");
		}
		if (getWorkflow()!=null) {
			json.put("workflowId", getWorkflow().getId());
		}else{
			json.put("workflowId", "");
		}
		if (getDayLimit()!=null) {
			json.put("dayLimit", getDayLimit());
		}else{
			json.put("dayLimit", 0);
		}
		String urlPrefix="";
		CmsSite site=getSite();
		if (StringUtils.isNotBlank(getUrl())) {
			json.put("url", getUrl());
			urlPrefix=site.getUrlPrefixWithNoDefaultPort();
		}else{
			json.put("url", "");
		}
		if (StringUtils.isNotBlank(getListUrl())) {
			json.put("listUrl", getListUrl());		
		}else{
			json.put("listUrl", "");
		}
		return json;
	}
	
	
	public void init(){
		if (getMemberMeun()==null) {
			setMemberMeun(false);
		}
		if (getAllSite()==null) {
			setAllSite(false);
		}
		if (getEnable()==null) {
			setEnable(false);
		}
		if (getDayLimit()==null) {
			setDayLimit(0);
		}
		if (getCreateTime() == null) {
			setCreateTime(new Timestamp(System.currentTimeMillis()));
		}
	}
	
	public CustomForm () {
		super();
	}
	
	/**
	 * Constructor for primary key
	 */
	public CustomForm (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CustomForm (
			java.lang.Integer id,
			java.lang.Integer priority,
			java.lang.Integer dayLimit,
			java.lang.String name,
			java.lang.String submitUrl,
			java.lang.Boolean adminMeun,
			java.lang.String viewUrl,
			java.util.Date startTime,
			java.util.Date endTime,
			java.lang.Boolean allSite,
			java.lang.Boolean enable,
			java.util.Date createTime)  {

		super (
			id,
			priority,
			dayLimit,
			name,
			submitUrl,
			adminMeun,
			viewUrl,
			startTime,
			endTime,
			allSite,
			enable,
			createTime);
		}
	
}
