package com.jeecms.cms.entity.main.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the jc_content_doc table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content_doc"
 */

public abstract class BaseContentDoc  implements Serializable {

	public static String REF = "ContentDoc";
	public static String PROP_FILE_SUFFIX = "fileSuffix";
	public static String PROP_IS_OPEN = "isOpen";
	public static String PROP_AVG_SCORE = "avgScore";
	public static String PROP_DOWN_NEED = "downNeed";
	public static String PROP_GRAIN = "grain";
	public static String PROP_ID = "id";
	public static String PROP_CONTENT = "content";
	public static String PROP_SWF_PATH = "swfPath";
	public static String PROP_PATH = "path";


	// constructors
	public BaseContentDoc () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseContentDoc (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseContentDoc (
		java.lang.Integer id,
		java.lang.String path,
		java.lang.Integer grain,
		java.lang.Integer downNeed,
		java.lang.Boolean isOpen,
		java.lang.String fileSuffix,
		java.lang.Float avgScore) {

		this.setId(id);
		this.setDocPath(path);
		this.setGrain(grain);
		this.setDownNeed(downNeed);
		this.setIsOpen(isOpen);
		this.setFileSuffix(fileSuffix);
		this.setAvgScore(avgScore);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String docPath;
	private java.lang.String swfPath;
	private java.lang.String pdfPath;
	private java.lang.Integer grain;
	private java.lang.Integer downNeed;
	private java.lang.Boolean isOpen;
	private java.lang.String fileSuffix;
	private java.lang.Float avgScore;
	private java.lang.Integer swfNum;

	// one to one
	private com.jeecms.cms.entity.main.Content content;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="foreign"
     *  column="content_id"
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




	/**
	 * Return the value associated with the column: path
	 */
	public java.lang.String getDocPath () {
		return docPath;
	}

	/**
	 * Set the value related to the column: path
	 * @param path the path value
	 */
	public void setDocPath (java.lang.String docPath) {
		this.docPath = docPath;
	}


	/**
	 * Return the value associated with the column: swf_path
	 */
	public java.lang.String getSwfPath () {
		return swfPath;
	}

	/**
	 * Set the value related to the column: swf_path
	 * @param swfPath the swf_path value
	 */
	public void setSwfPath (java.lang.String swfPath) {
		this.swfPath = swfPath;
	}

	public java.lang.String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(java.lang.String pdfPath) {
		this.pdfPath = pdfPath;
	}

	/**
	 * Return the value associated with the column: grain
	 */
	public java.lang.Integer getGrain () {
		return grain;
	}

	/**
	 * Set the value related to the column: grain
	 * @param grain the grain value
	 */
	public void setGrain (java.lang.Integer grain) {
		this.grain = grain;
	}


	/**
	 * Return the value associated with the column: down_need
	 */
	public java.lang.Integer getDownNeed () {
		return downNeed;
	}

	/**
	 * Set the value related to the column: down_need
	 * @param downNeed the down_need value
	 */
	public void setDownNeed (java.lang.Integer downNeed) {
		this.downNeed = downNeed;
	}


	/**
	 * Return the value associated with the column: is_open
	 */
	public java.lang.Boolean getIsOpen () {
		return isOpen;
	}

	/**
	 * Set the value related to the column: is_open
	 * @param isOpen the is_open value
	 */
	public void setIsOpen (java.lang.Boolean isOpen) {
		this.isOpen = isOpen;
	}


	/**
	 * Return the value associated with the column: file_suffix
	 */
	public java.lang.String getFileSuffix () {
		return fileSuffix;
	}

	/**
	 * Set the value related to the column: file_suffix
	 * @param fileSuffix the file_suffix value
	 */
	public void setFileSuffix (java.lang.String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}


	/**
	 * Return the value associated with the column: avg_score
	 */
	public java.lang.Float getAvgScore () {
		return avgScore;
	}

	/**
	 * Set the value related to the column: avg_score
	 * @param avgScore the avg_score value
	 */
	public void setAvgScore (java.lang.Float avgScore) {
		this.avgScore = avgScore;
	}
	
	public java.lang.Integer getSwfNum() {
		return swfNum;
	}

	public void setSwfNum(java.lang.Integer swfNum) {
		this.swfNum = swfNum;
	}

	/**
	 * Return the value associated with the column: content
	 */
	public com.jeecms.cms.entity.main.Content getContent () {
		return content;
	}

	/**
	 * Set the value related to the column: content
	 * @param content the content value
	 */
	public void setContent (com.jeecms.cms.entity.main.Content content) {
		this.content = content;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.cms.entity.main.ContentDoc)) return false;
		else {
			com.jeecms.cms.entity.main.ContentDoc contentDoc = (com.jeecms.cms.entity.main.ContentDoc) obj;
			if (null == this.getId() || null == contentDoc.getId()) return false;
			else return (this.getId().equals(contentDoc.getId()));
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