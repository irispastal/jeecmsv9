package com.jeecms.core.entity;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.core.entity.base.BaseCmsWorkflowNode;



public class CmsWorkflowNode extends BaseCmsWorkflowNode {
	private static final long serialVersionUID = 1L;
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getRole()!=null&&getRole().getId()!=null) {
			json.put("roleId", getRole().getId());
		}else{
			json.put("roleId", "");
		}
		if (getRole()!=null&&StringUtils.isNotBlank(getRole().getName())) {
			json.put("roleName", getRole().getName());
		}else{
			json.put("roleName", "");
		}
		json.put("countersign", isCountersign());
		return json;
	}
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflowNode () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflowNode (
		com.jeecms.core.entity.CmsRole role,
		boolean countersign) {

		super (
			role,
			countersign);
	}

/*[CONSTRUCTOR MARKER END]*/


}