package com.jeecms.core.entity;

import java.util.Calendar;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.util.StrUtils;
import com.jeecms.core.entity.base.BaseCmsWorkflowEvent;

import net.sf.json.JSONObject;



public class CmsWorkflowEvent extends BaseCmsWorkflowEvent {
	private static final long serialVersionUID = 1L;
	
	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (getDateId()!=null) {
			json.put("dateId", getDateId());
		}else{
			json.put("dateId", "");
		}
		if (getStartTime()!=null) {
			json.put("startTime", DateUtils.parseDateToDateStr(getStartTime()));
		}else{
			json.put("startTime", "");
		}
		if (getEndTime()!=null) {
			json.put("endTime", DateUtils.parseDateToDateStr(getEndTime()));
		}else{
			json.put("endTime", "");
		}
		if (getNextStep()!=null) {
			json.put("nextStep", getNextStep());
		}else{
			json.put("nextStep", "");
		}
		if (getDateType()!=null) {
			json.put("dateType", getDateType());
		}else{
			json.put("dateType", "");
		}
		if (getHasFinish()!=null) {
			json.put("hasFinish", getHasFinish());
		}else{
			json.put("hasFinish", "");
		}
		if (getPassNum()!=null) {
			json.put("passNum", getPassNum());
		}else{
			json.put("passNum", "");
		}
		return json;
	}
	
	public void init(){
		if(getStartTime()==null){
			setStartTime(Calendar.getInstance().getTime());
		}
		if(getDateType()==null){
			setDateType(Content.DATA_CONTENT);
		}
		if(getHasFinish()==null){
			setHasFinish(false);
		}
		//会签第一人建立流程轨迹 通过数为1
		if(getPassNum()==null){
			setPassNum(0);
		}
	}

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflowEvent () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsWorkflowEvent (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflowEvent (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsWorkflow workFlow,
		com.jeecms.core.entity.CmsUser initiator,
		java.lang.Integer dateId,
		java.util.Date startTime,
		java.lang.Integer nextStep,
		java.lang.Integer dateType,
		java.lang.Boolean hasFinish) {

		super (
			id,
			workFlow,
			initiator,
			dateId,
			startTime,
			nextStep,
			dateType,
			hasFinish);
	}

/*[CONSTRUCTOR MARKER END]*/


}