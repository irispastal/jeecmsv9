package com.jeecms.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jeecms.core.entity.base.BaseCmsConfig;


public class CmsConfig extends BaseCmsConfig {
	private static final long serialVersionUID = 1L;
	public static final String VERSION = "version";
	public static final String REWARD_FIX_PREFIX = "reward_fix_";
	
	public JSONObject convertToJson(JSONArray jsonArray) {
		JSONObject json = new JSONObject();
		json.put("jsonArray", jsonArray);
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(getContextPath())) {
			json.put("contextPath", getContextPath());
		}else{
			json.put("contextPath", "");
		}
		if (StringUtils.isNotBlank(getServletPoint())) {
			json.put("servletPoint", getServletPoint());
		}else{
			json.put("servletPoint", "");
		}
		if (StringUtils.isNotBlank(getDbFileUri())) {
			json.put("dbFileUri", getDbFileUri());
		}else{
			json.put("dbFileUri", "");
		}
		if (getPort()!=null) {
			json.put("port", getPort());
		}else{
			json.put("port", "");
		}
		if (getUploadToDb()!=null) {
			json.put("uploadToDb", getUploadToDb());
		}else{
			json.put("uploadToDb", "");
		}
		if (StringUtils.isNotBlank(getDefImg())) {
			json.put("defImg", getDefImg());
		}else{
			json.put("defImg", "");
		}
		if (StringUtils.isNotBlank(getLoginUrl())) {
			json.put("loginUrl", getLoginUrl());
		}else{
			json.put("loginUrl", "");
		}
		if (StringUtils.isNotBlank(getProcessUrl())) {
			json.put("processUrl", getProcessUrl());
		}else{
			json.put("processUrl", "");
		}
		if (getCountClearTime()!=null) {
			json.put("countClearTime", getCountClearTime());
		}else{
			json.put("countClearTime", "");
		}
		if (getCountCopyTime()!=null) {
			json.put("countCopyTime", getCountCopyTime());
		}else{
			json.put("countCopyTime", "");
		}
		if (StringUtils.isNotBlank(getDownloadCode())) {
			json.put("downloadCode", getDownloadCode());
		}else{
			json.put("downloadCode", "");
		}
		if (getDownloadTime()!=null) {
			json.put("downloadTime", getDownloadTime());
		}else{
			json.put("downloadTime", "");
		}
		if (getValidateType()!=null) {
			json.put("validateType",getValidateType());
		}else{
			json.put("validateType", 0);
		}
		if (StringUtils.isNotBlank(getOfficeHome())) {
			json.put("officeHome", getOfficeHome());
		}else{
			json.put("officeHome", "");
		}
		if (getOfficePort()!=null) {
			json.put("officePort", getOfficePort());
		}else{
			json.put("officePort", "");
		}
		if (StringUtils.isNotBlank(getSwftoolsHome())) {
			json.put("swftoolsHome", getSwftoolsHome());
		}else{
			json.put("swftoolsHome", "");
		}
		if (getViewOnlyChecked()!=null) {
			json.put("viewOnlyChecked", getViewOnlyChecked());
		}else{
			json.put("viewOnlyChecked", "");
		}
		if (getInsideSite()!=null) {
			json.put("insideSite", getInsideSite());
		}else{
			json.put("insideSite", "");
		}
		if (getFlowClearTime()!=null) {
			json.put("flowClearTime", getFlowClearTime());
		}else{
			json.put("flowClearTime", "");
		}
		if (getChannelCountClearTime()!=null) {
			json.put("channelCountClearTime", getChannelCountClearTime());
		}else{
			json.put("channelCountClearTime", "");
		}
		if (getCommentOpen()!=null) {
			json.put("commentOpen", getCommentOpen());
		}else{
			json.put("commentOpen", "");
		}
		if (getCommentDayLimit()!=null) {
			json.put("commentDayLimit", getCommentDayLimit());
		}else{
			json.put("commentDayLimit", "");
		}
		if (getGuestbookOpen()!=null) {
			json.put("guestbookOpen", getGuestbookOpen());
		}else{
			json.put("guestbookOpen", "");
		}
		if (getGuestbookNeedLogin()!=null) {
			json.put("guestbookNeedLogin", getGuestbookNeedLogin());
		}else{
			json.put("guestbookNeedLogin", "");
		}
		if (getGuestbookDayLimit()!=null) {
			json.put("guestbookDayLimit", getGuestbookDayLimit());
		}else{
			json.put("guestbookDayLimit", "");
		}
		if (getDayCount()!=null) {
			json.put("dayCount", getDayCount());
		}else{
			json.put("dayCount", 0);
		}
		if (getSmsID()!=null) {
			json.put("smsID", getSmsID());
		}else{
			json.put("smsID", getSmsID());
		}
		return json;
	}

	public String getVersion() {
		return getAttr().get(VERSION);
	}
	
	public Boolean getSsoEnable(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getSsoEnable();
	}
	
	public Boolean getFlowSwitch(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getFlowSwitch();
	}
	
	public Boolean getCommentOpen(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getCommentOpen();
	}
	
	public Boolean getGuestbookOpen(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getGuestbookOpen();
	}

	public Boolean getGuestbookNeedLogin(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getGuestbookNeedLogin();
	}
	
	public Map<String,String> getSsoAttr() {
		Map<String,String>ssoMap=new HashMap<String, String>();
		Map<String,String>attr=getAttr();
		for(String ssoKey:attr.keySet()){
			if(ssoKey.startsWith("sso_")){
				ssoMap.put(ssoKey, attr.get(ssoKey));
			}
		}
		return ssoMap;
	}
	
	public List<String> getSsoAuthenticateUrls() {
		Map<String,String>ssoMap=getSsoAttr();
		List<String>values=new ArrayList<String>();
		for(String key:ssoMap.keySet()){
			values.add(ssoMap.get(key));
		}
		return values;
	}
	
	public Map<String,String> getRewardFixAttr() {
		Map<String,String>attrMap=new HashMap<String, String>();
		Map<String,String>attr=getAttr();
		for(String fixKey:attr.keySet()){
			if(fixKey.startsWith(REWARD_FIX_PREFIX)){
				attrMap.put(fixKey, attr.get(fixKey));
			}
		}
		return attrMap;
	}
	
	public Object[] getRewardFixValues() {
		Map<String,String>attrMap=getRewardFixAttr();
		Collection<String>fixStrings=attrMap.values();
		return fixStrings.toArray();
	}
	
	
	public MemberConfig getMemberConfig() {
		MemberConfig memberConfig = new MemberConfig(getAttr());
		return memberConfig;
	}

	public void setMemberConfig(MemberConfig memberConfig) {
		getAttr().putAll(memberConfig.getAttr());
	}
	
	public CmsConfigAttr getConfigAttr() {
		CmsConfigAttr configAttr = new CmsConfigAttr(getAttr());
		return configAttr;
	}

	public void setConfigAttr(CmsConfigAttr configAttr) {
		getAttr().putAll(configAttr.getAttr());
	}
	
	public Boolean getQqEnable(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getQqEnable();
	}
	
	public Integer getCommentDayLimit(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getCommentDayLimit();
	}
	
	public Integer getGuestbookDayLimit(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getGuestbookDayLimit();
	}
	
	public Boolean getSinaEnable(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getSinaEnable();
	}
	
	public Boolean getQqWeboEnable(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getQqWeboEnable();
	}
	
	public String getQqID(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getQqID();
	}
	
	public String getSinaID(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getSinaID();
	}
	
	public String getQqWeboID(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getQqWeboID();
	}
	
	public Boolean getWeixinEnable(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinEnable();
	}
	
	public String getWeixinID(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinID();
	}
	
	public String getWeixinKey(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinKey();
	}
	
	public String getWeixinAppId(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinAppId();
	}
	
	public String getWeixinAppSecret(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinAppSecret();
	}
	
	public String getWeixinLoginId(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinLoginId();
	}
	
	public String getWeixinLoginSecret(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getWeixinLoginSecret();
	}
	
	public Integer getContentFreshMinute(){
		CmsConfigAttr configAttr=getConfigAttr();
		return configAttr.getContentFreshMinute();
	}

	public void blankToNull() {
		// oracle varchar2把空串当作null处理，这里为了统一这个特征，特做此处理。
		if (StringUtils.isBlank(getProcessUrl())) {
			setProcessUrl(null);
		}
		if (StringUtils.isBlank(getContextPath())) {
			setContextPath(null);
		}
		if (StringUtils.isBlank(getServletPoint())) {
			setServletPoint(null);
		}
	}

	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsConfig() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsConfig(java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsConfig(java.lang.Integer id, java.lang.String dbFileUri,
			java.lang.Boolean uploadToDb, java.lang.String defImg,
			java.lang.String loginUrl, java.util.Date countClearTime,
			java.util.Date countCopyTime, java.lang.String downloadCode,
			java.lang.Integer downloadTime) {

		super(id, dbFileUri, uploadToDb, defImg, loginUrl, countClearTime,
				countCopyTime, downloadCode, downloadTime);
	}

	public void init() {
		if (getUploadToDb()==null) {
			setUploadToDb(false);
		}
		if (getValidateType()==null) {
			setValidateType(1);
		}
		if (getViewOnlyChecked()==null) {
			setViewOnlyChecked(false);
		}
		if (getInsideSite()==null) {
			setInsideSite(true);
		}
	}

	/* [CONSTRUCTOR MARKER END] */

}