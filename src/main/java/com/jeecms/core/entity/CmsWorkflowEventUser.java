package com.jeecms.core.entity;

import com.jeecms.core.entity.base.BaseCmsWorkflowEventUser;



public class CmsWorkflowEventUser extends BaseCmsWorkflowEventUser {
	private static final long serialVersionUID = 1L;

	public CmsWorkflowEventUser(CmsWorkflowEvent event, CmsUser user) {
		super(event, user);
	}
	
	

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflowEventUser () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsWorkflowEventUser (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflowEventUser (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsWorkflowEvent event,
		com.jeecms.core.entity.CmsUser user) {

		super (
			id,
			event,
			user);
	}


/*[CONSTRUCTOR MARKER END]*/


}