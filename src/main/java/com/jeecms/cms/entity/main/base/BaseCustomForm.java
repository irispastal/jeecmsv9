package com.jeecms.cms.entity.main.base;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * table="jc_custom_form"
 */
public class BaseCustomForm implements Serializable {
	public static String REF = "CustomForm";
	public static String PROP_SUBMIT_URL = "submitUrl";
	public static String PROP_VIEW_URL = "viewUrl";
	public static String PROP_MEMBER_MEUN = "memberMeun";
	public static String PROP_NAME = "name";
	public static String PROP_ID = "id";
	public static String PROP_CREATE_TIME = "createTime";
	public static String PROP_START_TIME = "startTime";
	public static String PROP_END_TIME = "endTime";
	public static String PROP_ALL_SITE = "allSite";
	public static String PROP_ENABLE = "enable";
	
	public BaseCustomForm () {
		initialize();
	}
	
	public BaseCustomForm (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}
	
	public BaseCustomForm (
			java.lang.Integer id,
			java.lang.Integer priority,
			java.lang.Integer dayLimit,
			java.lang.String name,
			java.lang.String submitUrl,
			java.lang.Boolean memberMeun,
			java.lang.String viewUrl,
			java.util.Date startTime,
			java.util.Date endTime,
			java.lang.Boolean allSite,
			java.lang.Boolean enable,
			java.util.Date createTime) {

			this.setId(id);
			this.setPriority(priority);			
			this.setName(name);
			this.setSubmitUrl(submitUrl);
			this.setMemberMeun(memberMeun);
			this.setViewUrl(viewUrl);
			this.setStartTime(startTime);
			this.setEnable(enable);
			this.setAllSite(allSite);
			this.setEndTime(endTime);	
			this.setDayLimit(dayLimit);
			this.setCreateTime(createTime);
			initialize();
		}
	
	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String name;
	private java.lang.Integer priority;
	private java.lang.String submitUrl;
	private java.lang.Boolean memberMeun;
	private java.util.Date createTime;
	private java.util.Date startTime;
	private java.util.Date endTime;
	private java.lang.Boolean allSite;
	private java.lang.Boolean enable;
	private java.lang.String viewUrl;
	private java.lang.Integer dayLimit;
	// many to one
	private com.jeecms.core.entity.CmsSite site;
	private com.jeecms.core.entity.CmsUser user;
	private com.jeecms.core.entity.CmsWorkflow workflow;

	// one to many
	private java.util.Set<com.jeecms.cms.entity.main.CustomFormFiled> fileds;
	private java.util.Set<com.jeecms.cms.entity.main.CustomRecord> records;
	
	public java.util.Set<com.jeecms.cms.entity.main.CustomRecord> getRecords() {
		return records;
	}

	public void setRecords(java.util.Set<com.jeecms.cms.entity.main.CustomRecord> records) {
		this.records = records;
	}

	public java.util.Set<com.jeecms.cms.entity.main.CustomFormFiled> getFileds() {
		return fileds;
	}

	public void setFileds(java.util.Set<com.jeecms.cms.entity.main.CustomFormFiled> fileds) {
		this.fileds = fileds;
	}

	public com.jeecms.core.entity.CmsSite getSite() {
		return site;
	}

	public void setSite(com.jeecms.core.entity.CmsSite site) {
		this.site = site;
	}

	public com.jeecms.core.entity.CmsUser getUser() {
		return user;
	}

	public void setUser(com.jeecms.core.entity.CmsUser user) {
		this.user = user;
	}

	public com.jeecms.core.entity.CmsWorkflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(com.jeecms.core.entity.CmsWorkflow workflow) {
		this.workflow = workflow;
	}

	
	public java.lang.Integer getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(java.lang.Integer dayLimit) {
		this.dayLimit = dayLimit;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	
	public java.lang.String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(java.lang.String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.Integer getPriority() {
		return priority;
	}

	public void setPriority(java.lang.Integer priority) {
		this.priority = priority;
	}

	public java.lang.String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(java.lang.String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public java.lang.Boolean getMemberMeun() {
		return memberMeun;
	}

	public void setMemberMeun(java.lang.Boolean memberMeun) {
		this.memberMeun = memberMeun;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	
	
	public java.util.Date getStartTime() {
		return startTime;
	}

	public void setStartTime(java.util.Date startTime) {
		this.startTime = startTime;
	}

	public java.util.Date getEndTime() {
		return endTime;
	}

	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public java.lang.Boolean getAllSite() {
		return allSite;
	}

	public void setAllSite(java.lang.Boolean allSite) {
		this.allSite = allSite;
	}

	public java.lang.Boolean getEnable() {
		return enable;
	}

	public void setEnable(java.lang.Boolean enable) {
		this.enable = enable;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.main.CustomForm)) return false;
		else {
			com.jeecms.cms.entity.main.CustomForm customForm = (com.jeecms.cms.entity.main.CustomForm) obj;
			if (null == this.getId() || null == customForm.getId()) return false;
			else return (this.getId().equals(customForm.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}
	
}	

