package com.jeecms.cms.entity.assist;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.cms.entity.assist.base.BaseCmsAcquisitionShield;

public class CmsAcquisitionShield extends BaseCmsAcquisitionShield {
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(getShieldStart())) {
			json.put("shieldStart", getShieldStart());
		}else{
			json.put("shieldStart", "");
		}
		if (StringUtils.isNotBlank(getShieldEnd())) {
			json.put("shieldEnd", getShieldEnd());
		}else{
			json.put("shieldEnd", "");
		}
		return json;
	}
	
	public CmsAcquisitionShield() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsAcquisitionShield (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsAcquisitionShield (
		java.lang.Integer id,
		com.jeecms.cms.entity.assist.CmsAcquisition acquisition,
		java.lang.String shieldStart,
		java.lang.String shieldEnd) {

		super (
			id,
			acquisition,
			shieldStart,
			shieldEnd);
	}
}
