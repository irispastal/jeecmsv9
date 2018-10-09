package com.jeecms.core.entity;

import com.jeecms.core.entity.base.BaseCmsWorkflowRecord;



public class CmsWorkflowRecord extends BaseCmsWorkflowRecord {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflowRecord () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsWorkflowRecord (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflowRecord (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsSite site,
		com.jeecms.core.entity.CmsWorkflowEvent event,
		com.jeecms.core.entity.CmsUser user,
		java.util.Date recordTime,
		java.lang.Integer type) {

		super (
			id,
			site,
			event,
			user,
			recordTime,
			type);
	}

/*[CONSTRUCTOR MARKER END]*/


}