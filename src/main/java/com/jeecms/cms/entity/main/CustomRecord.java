package com.jeecms.cms.entity.main;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.jeecms.cms.entity.main.base.BaseCustomRecord;

public class CustomRecord extends BaseCustomRecord {
	public static int DATA_CONTENT=1;
	public static int CHECKED=2; //已终审
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (getStatus()!=null) {
			json.put("status", getStatus());
		}else{
			json.put("status",0);
		}
		if (getCreateTime()!=null) {
			json.put("createTime", getCreateTime());
		}else{
			json.put("createTime",0);
		}
		if (getForm()!=null) {
			json.put("formId", getForm().getId());
		}else{
			json.put("formId","");
		}
		getAttrJson(json);
		return json;
	}
	
	public JSONObject getAttrJson(JSONObject json){
		if (getAttr()!=null) {
			for(Entry<String,String> entry:getAttr().entrySet()){
				json.put("attr_"+entry.getKey(),entry.getValue());
			}
		}
		return json;
	}
	
	//获取显示字段
	
	public void init(){
		if(getStatus()==null){
			setStatus(0);
		}
	}
	
	public void setCustomRecordCheck(CustomRecordCheck check) {
		Set<CustomRecordCheck> set = getRecordCheckSet();
		if (set == null) {
			set = new HashSet<CustomRecordCheck>();
			setRecordCheckSet(set);
		}
		if (!set.isEmpty()) {
			set.clear();
		}
		set.add(check);
	}
	
	public CustomRecordCheck getCustomRecordCheck() {
		Set<CustomRecordCheck> set = getRecordCheckSet();
		if (set != null && set.size() > 0) {
			return set.iterator().next();
		} else {
			return null;
		}
	}
	
	public CustomRecord () {
		super();
	}
	
	/**
	 * Constructor for primary key
	 */
	public CustomRecord (java.lang.Integer id) {
		super(id);
	}
	
	/**
	 * Constructor for required fields
	 */
	public CustomRecord (
			java.lang.Integer id,
			com.jeecms.core.entity.CmsSite site,
			java.lang.Integer status,
			java.util.Date createTime){

		super (
			id,
			site,
			status,
			createTime);
		}
}
