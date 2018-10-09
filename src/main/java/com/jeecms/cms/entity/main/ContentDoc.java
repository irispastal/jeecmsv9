package com.jeecms.cms.entity.main;


import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.cms.entity.main.base.BaseContentDoc;



public class ContentDoc extends BaseContentDoc {
	private static final long serialVersionUID = 1L;
	/**
	 * 私有
	 */
	public static final  byte PERSONAL=0;
	/**
	 * 公开
	 */
	public static final  byte OPEN=1;
	/**
	 * 所有
	 */
	public static final  byte ALL=2;
	
	public void init(){
		if(getAvgScore()==null){
			setAvgScore(0f);
		}
		if(getDownNeed()==null){
			setDownNeed(0);
		}
		if(getGrain()==null){
			setGrain(0);
		}
		if(getIsOpen()==null){
			setIsOpen(true);
		}
		if(getSwfNum()==null){
			setSwfNum(0);
		}
	}

	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(getDocPath())) {
			json.put("docPath", getDocPath());
		}else{
			json.put("docPath", "");
		}
		if (StringUtils.isNotBlank(getSwfPath())) {
			json.put("swfPath", getSwfPath());
		}else{
			json.put("swfPath", "");
		}
		if (StringUtils.isNotBlank(getPdfPath())) {
			json.put("pdfPath", getPdfPath());
		}else{
			json.put("pdfPath", "");
		}
		if (StringUtils.isNotBlank(getDocPath())) {
			json.put("docPath", getDocPath());
		}else{
			json.put("docPath", "");
		}
		if (getGrain()!=null) {
			json.put("grain", getGrain());
		}else{
			json.put("grain", "");
		}
		if (getDownNeed()!=null) {
			json.put("downNeed", getDownNeed());
		}else{
			json.put("downNeed", "");
		}
		if (getIsOpen()!=null) {
			json.put("isOpen", getIsOpen());
		}else{
			json.put("isOpen", "");
		}
		if (getAvgScore()!=null) {
			json.put("avgScore", getAvgScore());
		}else{
			json.put("avgScore", "");
		}
		if (getSwfNum()!=null) {
			json.put("swfNum", getSwfNum());
		}else{
			json.put("swfNum", "");
		}
		return json;
	}
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public ContentDoc () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public ContentDoc (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public ContentDoc (
		java.lang.Integer id,
		java.lang.String path,
		java.lang.Integer grain,
		java.lang.Integer downNeed,
		java.lang.Boolean isOpen,
		java.lang.String fileSuffix,
		java.lang.Float avgScore) {

		super (
			id,
			path,
			grain,
			downNeed,
			isOpen,
			fileSuffix,
			avgScore);
	}

/*[CONSTRUCTOR MARKER END]*/


}