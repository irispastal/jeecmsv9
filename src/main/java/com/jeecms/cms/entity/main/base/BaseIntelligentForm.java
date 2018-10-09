package com.jeecms.cms.entity.main.base;

import java.io.Serializable;

public abstract class BaseIntelligentForm implements Serializable {
	
	public static String REF = "IntelligentForm";
	public static String PROP_ID = "id";
	public static String PROP_NAME = "name";
	public static String PROP_TAG = "tag";
	public static String PROP_STATUS = "status";
	public static String PROP_FEED_BACK_NUM = "feedbackNum";
	public static String PROP_COLLERTION_TIME = "collectionTime";
	public static String PROP_CREATE_TIME = "createTime";


	// constructors
	public BaseIntelligentForm () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseIntelligentForm (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseIntelligentForm (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.String tag,
		java.lang.Short status,
		java.lang.Integer feedbackNum,
		java.util.Date collectionTime,
		java.util.Date createTime) {

		this.setId(id);
		this.setName(name);
		this.setTag(tag);
		this.setStatus(status);
		this.setFeedbackNum(feedbackNum);
		this.setCollectionTime(collectionTime);
		this.setCreateTime(createTime);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;
	private java.lang.String name;
	private java.lang.String tag;
	private java.lang.Short status;
	private java.lang.Integer feedbackNum;
	private java.util.Date collectionTime;
	private java.util.Date createTime;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}


	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getTag() {
		return tag;
	}

	public void setTag(java.lang.String tag) {
		this.tag = tag;
	}

	public java.lang.Short getStatus() {
		return status;
	}

	public void setStatus(java.lang.Short status) {
		this.status = status;
	}

	public java.lang.Integer getFeedbackNum() {
		return feedbackNum;
	}

	public void setFeedbackNum(java.lang.Integer feedbackNum) {
		this.feedbackNum = feedbackNum;
	}

	public java.util.Date getCollectionTime() {
		return collectionTime;
	}

	public void setCollectionTime(java.util.Date collectionTime) {
		this.collectionTime = collectionTime;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.main.IntelligentForm)) return false;
		else {
			com.jeecms.cms.entity.main.IntelligentForm intelligentForm = (com.jeecms.cms.entity.main.IntelligentForm) obj;
			if (null == this.getId() || null == intelligentForm.getId()) return false;
			else return (this.getId().equals(intelligentForm.getId()));
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
