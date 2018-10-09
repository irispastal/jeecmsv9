package com.jeecms.core.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;

import com.jeecms.core.entity.base.BaseCmsWorkflow;

import net.sf.json.JSONObject;



public class CmsWorkflow extends BaseCmsWorkflow {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 通过
	 */
	public static int PASS=1;
	/**
	 * 退回
	 */
	public static int BACK=2;
	/**
	 * 保持
	 */
	public static int KEEP=3;
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(getName())) {
			json.put("name", getName());
		}else{
			json.put("name", "");
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
		json.put("disabled", getDisabled());
		json.put("cross", getCross());
		JSONArray nodeArray = new JSONArray();
		if (getNodes()!=null&&getNodes().size()>0) {
			List<CmsWorkflowNode> list = getNodes();
			for(int i = 0 ; i < list.size(); i++){
				if (list.get(i)!=null) {
					nodeArray.put(i,list.get(i).convertToJson());
				}
			}
		}
		json.put("nodes", nodeArray.toString());
		if (getEvents()!=null&&getEvents().size()>0) {
			Set<CmsWorkflowEvent> set = getEvents();
			JSONArray jsonArray = new JSONArray();
			int index =0 ;
			for (CmsWorkflowEvent event : set) {
				jsonArray.put(index,event.convertToJson());
			}
			json.put("events", jsonArray.toString());
		}else{
			json.put("events", new JSONArray());
		}
		return json;
	}
	
	public boolean getDisabled () {
		return super.isDisabled();
	}
	
	public boolean getCross () {
		return super.isCross();
	}
	
	
	public void addToNodes(CmsRole role, boolean countersign) {
		List<CmsWorkflowNode> list =getNodes();
		if (list == null) {
			list = new ArrayList<CmsWorkflowNode>();
			setNodes(list);
		}
		CmsWorkflowNode node = new CmsWorkflowNode();
		node.setRole(role);
		node.setCountersign(countersign);
		list.add(node);
	}

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflow () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsWorkflow (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflow (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsSite site,
		java.lang.String name,
		java.lang.Integer priority,
		boolean disabled) {

		super (
			id,
			site,
			name,
			priority,
			disabled);
	}

/*[CONSTRUCTOR MARKER END]*/


}