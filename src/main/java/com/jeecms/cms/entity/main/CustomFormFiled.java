package com.jeecms.cms.entity.main;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jeecms.cms.entity.main.base.BaseCustomFormFiled;

public class CustomFormFiled extends BaseCustomFormFiled {
	
	private static final long serialVersionUID = 1L;
	public JSONObject convertToJsonList(){
		JSONObject json = commonJson();
		if (getDataType()!=null) {
			if (getDataType().equals(6)||getDataType().equals(7)||getDataType().equals(8)) {
				JSONArray jsonArray = new JSONArray();
				String op = getOptValue();
				if (StringUtils.isNotBlank(op)) {
					String[] split = op.split(",");
					for (int i = 0; i < split.length; i++) {
						jsonArray.put(i,split[i]);
					}
				}
				json.put("optValue", jsonArray);
			}else{
				if (StringUtils.isNotBlank(getOptValue())) {
					json.put("optValue", getOptValue());
				}else{
					json.put("optValue", "");
				}
			}
		}
		return json;
	}
	
	public JSONObject convertToJson(){
		JSONObject json = commonJson();
		if (StringUtils.isNotBlank(getOptValue())) {
			json.put("optValue", getOptValue());
		}else{
			json.put("optValue", "");
		}
		return json;
	}
	
	
	//get/list共用json
		private JSONObject commonJson(){
			JSONObject json= new JSONObject();
			if (getId()!=null) {
				json.put("id", getId());
			}else{
				json.put("id", "");
			}
			if (StringUtils.isNotBlank(getField())) {
				json.put("field", getField());
			}else{
				json.put("field", "");
			}
			if (StringUtils.isNotBlank(getLabel())) {
				json.put("label", getLabel());
			}else{
				json.put("label", "");
			}
			if (StringUtils.isNotBlank(getDefValue())) {
				json.put("defValue", getDefValue());
			}else{
				json.put("defValue", "");
			}
			if (StringUtils.isNotBlank(getSize())) {
				json.put("size", getSize());
			}else{
				json.put("size", "");
			}
			if (StringUtils.isNotBlank(getDescription())) {
				json.put("description", getDescription());
			}else{
				json.put("description", "");
			}
			if (getPriority()!=null) {
				json.put("priority", getPriority());
			}else{
				json.put("priority", "");
			}
			if (getDataType()!=null) {
				json.put("dataType", getDataType());
			}else{
				json.put("dataType", "");
			}		
			if (getDisplayInList()!=null) {
				json.put("displayInList", getDisplayInList());
			}else{
				json.put("displayInList",false);
			}
			if (getRequired()!=null) {
				json.put("required", getRequired());
			}else{
				json.put("required", "");
			}			
			return json;
		}
	
	public void init() {
		if (getPriority() == null) {
			setPriority(10);
		}
		if (getDisplayInList() == null) {
			setDisplayInList(false);
		}
		if (getRequired()==null) {
			setRequired(false);
		}
		if (getDataType()==null) {
			setDataType(1);
		}	
	}
	
	// 将字符串字段全部设置为非null，方便判断。
		public void emptyToNull() {
			if (StringUtils.isBlank(getDefValue())) {
				setDefValue(null);
			}
			if (StringUtils.isBlank(getOptValue())) {
				setOptValue(null);
			}
			if (StringUtils.isBlank(getSize())) {
				setSize(null);
			}
			if (StringUtils.isBlank(getDescription())) {
				setDescription(null);
			}
		}

		/* [CONSTRUCTOR MARKER BEGIN] */
		public CustomFormFiled() {
			// TODO Auto-generated constructor stub
			super();
		}

		/**
		 * Constructor for primary key
		 */
		public CustomFormFiled (java.lang.Integer id) {
			super(id);
		}

		/**
		 * Constructor for required fields
		 */
		public CustomFormFiled (
			java.lang.Integer id,
			com.jeecms.cms.entity.main.CustomForm form,
			java.lang.String field,
			java.lang.String label,
			java.lang.Integer dataType,
			java.lang.Boolean display) {

			super (
				id,
				form,
				field,
				label,
				dataType,
				display);
		}
	
}
