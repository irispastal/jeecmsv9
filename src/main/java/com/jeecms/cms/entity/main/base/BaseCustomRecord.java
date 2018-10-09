package com.jeecms.cms.entity.main.base;

import java.io.Serializable;
/**
 * 
 * @author Administrator
 * table="jc_custom_record"
 */
public class BaseCustomRecord implements Serializable {
	public static String REF = "CustomRecord";
	public static String PROP_SITE = "site";
	public static String PROP_MODEL = "form";
	public static String PROP_CREATE_TIME = "createTime";
	public static String PROP_STATUS = "status";
	public static String PROP_ID = "id";
	
	// constructors
	public BaseCustomRecord() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCustomRecord (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}
	
	/**
	 * Constructor for required fields
	 */
	public BaseCustomRecord (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsSite site,
		java.lang.Integer status,
		java.util.Date createTime) {

		this.setId(id);
		this.setSite(site);
		this.setStatus(status);
		this.setCreateTime(createTime);
		initialize();
	}
	
	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.Integer status;
	private java.util.Date createTime;
	
	//many to one
	private com.jeecms.cms.entity.main.CustomForm form;
	private com.jeecms.core.entity.CmsUser user;
	private com.jeecms.core.entity.CmsSite site;

	//collects
	private java.util.Map<java.lang.String, java.lang.String> attr;
	private java.util.Set<com.jeecms.cms.entity.main.CustomRecordCheck> recordCheckSet;

	
	
	public java.util.Set<com.jeecms.cms.entity.main.CustomRecordCheck> getRecordCheckSet() {
		return recordCheckSet;
	}

	public void setRecordCheckSet(java.util.Set<com.jeecms.cms.entity.main.CustomRecordCheck> recordCheckSet) {
		this.recordCheckSet = recordCheckSet;
	}

	
	
	public java.util.Map<java.lang.String, java.lang.String> getAttr() {
		return attr;
	}

	public void setAttr(java.util.Map<java.lang.String, java.lang.String> attr) {
		this.attr = attr;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public com.jeecms.cms.entity.main.CustomForm getForm() {
		return form;
	}

	public void setForm(com.jeecms.cms.entity.main.CustomForm form) {
		this.form = form;
	}

	public com.jeecms.core.entity.CmsUser getUser() {
		return user;
	}

	public void setUser(com.jeecms.core.entity.CmsUser user) {
		this.user = user;
	}

	public com.jeecms.core.entity.CmsSite getSite() {
		return site;
	}

	public void setSite(com.jeecms.core.entity.CmsSite site) {
		this.site = site;
	}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.main.CustomRecord)) return false;
		else {
			com.jeecms.cms.entity.main.CustomRecord customRecord = (com.jeecms.cms.entity.main.CustomRecord) obj;
			if (null == this.getId() || null == customRecord.getId()) return false;
			else return (this.getId().equals(customRecord.getId()));
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
