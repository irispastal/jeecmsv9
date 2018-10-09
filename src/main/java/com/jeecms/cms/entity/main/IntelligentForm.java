package com.jeecms.cms.entity.main;

import org.apache.commons.lang.StringUtils;
import com.jeecms.cms.entity.main.base.BaseIntelligentForm;
import com.jeecms.common.util.DateUtils;
import net.sf.json.JSONObject;

public class IntelligentForm extends BaseIntelligentForm{
	
	private static final long serialVersionUID = 1L;

	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (getCollectionTime()!=null) {
			json.put("collectionTime", DateUtils.parseDateToDateStr(getCollectionTime()));
		}else{
			json.put("collectionTime", "");
		}
		if (getCreateTime()!=null) {
			json.put("createTime", DateUtils.parseDateToDateStr(getCreateTime()));
		}else{
			json.put("createTime", "");
		}
		if (StringUtils.isNotBlank(getName())) {
			json.put("name", getName());
		}else{
			json.put("name", "");
		}
		if (StringUtils.isNotBlank(getTag())) {
			json.put("tag", getTag());
		}else{
			json.put("tag", "");
		}
		if (getFeedbackNum()!=null) {
			json.put("feedbackNum", getFeedbackNum());
		}else{
			json.put("feedbackNum", "");
		}
		if (getStatus()!=null) {
			json.put("status", getStatus());
		}else{
			json.put("status", "");
		}
		return json;
	}
	
	/*[CONSTRUCTOR MARKER BEGIN]*/
	public IntelligentForm () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public IntelligentForm (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public IntelligentForm (
			java.lang.Integer id,
			java.lang.String name,
			java.lang.String tag,
			java.lang.Short status,
			java.lang.Integer feedbackNum,
			java.util.Date collectionTime,
			java.util.Date createTime) {

		super (
			id,
			name,
			tag,
			status,
			feedbackNum,
			collectionTime,
			createTime);
	}

/*[CONSTRUCTOR MARKER END]*/

}
