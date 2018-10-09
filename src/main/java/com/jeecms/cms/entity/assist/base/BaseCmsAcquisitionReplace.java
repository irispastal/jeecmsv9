package com.jeecms.cms.entity.assist.base;

import java.io.Serializable;

public abstract class BaseCmsAcquisitionReplace implements Serializable {
	public static String REF = "CmsAcquisitionReplace";
	public static String PROP_KEYWORD = "keyword";
	public static String PROP_REPLACE_WORD = "replaceWord";
	public static String PROP_ACQUISTION = "acquistion";
	
	// constructors
	public BaseCmsAcquisitionReplace () {
		initialize();
	}
		
	/**
	 * Constructor for primary key
	 */
	public BaseCmsAcquisitionReplace (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}
	
	/**
	 * Constructor for required fields
	 */
	public BaseCmsAcquisitionReplace (
		java.lang.Integer id,
		com.jeecms.cms.entity.assist.CmsAcquisition acquisition,
		java.lang.String keyword,
		java.lang.String replaceWord) {

		this.setId(id);
		this.setAcquisition(acquisition);
		this.setKeyword(keyword);
		this.setReplaceWord(replaceWord);
		initialize();
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;
	
	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String keyword;
	private java.lang.String replaceWord;
	
	// many to one	
	private com.jeecms.cms.entity.assist.CmsAcquisition acquisition;

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getKeyword() {
		return keyword;
	}

	public void setKeyword(java.lang.String keyword) {
		this.keyword = keyword;
	}

	public java.lang.String getReplaceWord() {
		return replaceWord;
	}

	public void setReplaceWord(java.lang.String replaceWord) {
		this.replaceWord = replaceWord;
	}

	public com.jeecms.cms.entity.assist.CmsAcquisition getAcquisition() {
		return acquisition;
	}

	public void setAcquisition(com.jeecms.cms.entity.assist.CmsAcquisition acquisition) {
		this.acquisition = acquisition;
	}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.assist.CmsAcquisitionReplace)) return false;
		else {
			com.jeecms.cms.entity.assist.CmsAcquisitionReplace cmsAcquisitionReplace = (com.jeecms.cms.entity.assist.CmsAcquisitionReplace) obj;
			if (null == this.getId() || null == cmsAcquisitionReplace.getId()) return false;
			else return (this.getId().equals(cmsAcquisitionReplace.getId()));
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
