package com.jeecms.core.entity.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the jc_workflow_event table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_workflow_event"
 */

public abstract class BaseCmsWorkflowEvent  implements Serializable {

	public static String REF = "CmsWorkflowEvent";
	public static String PROP_INITIATOR = "initiator";
	public static String PROP_HAS_FINISH = "hasFinish";
	public static String PROP_DATE_ID = "dateId";
	public static String PROP_NEXT_STEP = "nextStep";
	public static String PROP_WORK_FLOW = "workFlow";
	public static String PROP_PASS_NUM = "passNum";
	public static String PROP_ID = "id";
	public static String PROP_END_TIME = "endTime";
	public static String PROP_START_TIME = "startTime";
	public static String PROP_DATE_TYPE = "dateType";


	// constructors
	public BaseCmsWorkflowEvent () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCmsWorkflowEvent (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCmsWorkflowEvent (
		java.lang.Integer id,
		com.jeecms.core.entity.CmsWorkflow workFlow,
		com.jeecms.core.entity.CmsUser initiator,
		java.lang.Integer dateId,
		java.util.Date startTime,
		java.lang.Integer nextStep,
		java.lang.Integer dateType,
		java.lang.Boolean hasFinish) {

		this.setId(id);
		this.setWorkFlow(workFlow);
		this.setInitiator(initiator);
		this.setDateId(dateId);
		this.setStartTime(startTime);
		this.setNextStep(nextStep);
		this.setDateType(dateType);
		this.setHasFinish(hasFinish);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.Integer dateId;
	private java.util.Date startTime;
	private java.util.Date endTime;
	private java.lang.Integer nextStep;
	private java.lang.Integer dateType;
	private java.lang.Boolean hasFinish;
	private java.lang.Integer passNum;

	// many to one
	private com.jeecms.core.entity.CmsWorkflow workFlow;
	private com.jeecms.core.entity.CmsUser initiator;

	// collections
	private java.util.Set<com.jeecms.core.entity.CmsWorkflowRecord> records;
	private java.util.Set<com.jeecms.core.entity.CmsWorkflowEventUser> users;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="event_id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: date_id
	 */
	public java.lang.Integer getDateId () {
		return dateId;
	}

	/**
	 * Set the value related to the column: date_id
	 * @param dateId the date_id value
	 */
	public void setDateId (java.lang.Integer dateId) {
		this.dateId = dateId;
	}


	/**
	 * Return the value associated with the column: start_time
	 */
	public java.util.Date getStartTime () {
		return startTime;
	}

	/**
	 * Set the value related to the column: start_time
	 * @param startTime the start_time value
	 */
	public void setStartTime (java.util.Date startTime) {
		this.startTime = startTime;
	}


	/**
	 * Return the value associated with the column: end_time
	 */
	public java.util.Date getEndTime () {
		return endTime;
	}

	/**
	 * Set the value related to the column: end_time
	 * @param endTime the end_time value
	 */
	public void setEndTime (java.util.Date endTime) {
		this.endTime = endTime;
	}


	/**
	 * Return the value associated with the column: next_step
	 */
	public java.lang.Integer getNextStep () {
		return nextStep;
	}

	/**
	 * Set the value related to the column: next_step
	 * @param nextStep the next_step value
	 */
	public void setNextStep (java.lang.Integer nextStep) {
		this.nextStep = nextStep;
	}


	/**
	 * Return the value associated with the column: date_type
	 */
	public java.lang.Integer getDateType () {
		return dateType;
	}

	/**
	 * Set the value related to the column: date_type
	 * @param dateType the date_type value
	 */
	public void setDateType (java.lang.Integer dateType) {
		this.dateType = dateType;
	}


	/**
	 * Return the value associated with the column: is_finish
	 */
	public java.lang.Boolean getHasFinish () {
		return hasFinish;
	}

	/**
	 * Set the value related to the column: is_finish
	 * @param hasFinish the is_finish value
	 */
	public void setHasFinish (java.lang.Boolean hasFinish) {
		this.hasFinish = hasFinish;
	}


	/**
	 * Return the value associated with the column: pass_num
	 */
	public java.lang.Integer getPassNum () {
		return passNum;
	}

	/**
	 * Set the value related to the column: pass_num
	 * @param passNum the pass_num value
	 */
	public void setPassNum (java.lang.Integer passNum) {
		this.passNum = passNum;
	}


	/**
	 * Return the value associated with the column: workflow_id
	 */
	public com.jeecms.core.entity.CmsWorkflow getWorkFlow () {
		return workFlow;
	}

	/**
	 * Set the value related to the column: workflow_id
	 * @param workFlow the workflow_id value
	 */
	public void setWorkFlow (com.jeecms.core.entity.CmsWorkflow workFlow) {
		this.workFlow = workFlow;
	}


	/**
	 * Return the value associated with the column: user_id
	 */
	public com.jeecms.core.entity.CmsUser getInitiator () {
		return initiator;
	}

	/**
	 * Set the value related to the column: user_id
	 * @param initiator the user_id value
	 */
	public void setInitiator (com.jeecms.core.entity.CmsUser initiator) {
		this.initiator = initiator;
	}


	/**
	 * Return the value associated with the column: records
	 */
	public java.util.Set<com.jeecms.core.entity.CmsWorkflowRecord> getRecords () {
		return records;
	}

	/**
	 * Set the value related to the column: records
	 * @param records the records value
	 */
	public void setRecords (java.util.Set<com.jeecms.core.entity.CmsWorkflowRecord> records) {
		this.records = records;
	}


	/**
	 * Return the value associated with the column: users
	 */
	public java.util.Set<com.jeecms.core.entity.CmsWorkflowEventUser> getUsers () {
		return users;
	}

	/**
	 * Set the value related to the column: users
	 * @param users the users value
	 */
	public void setUsers (java.util.Set<com.jeecms.core.entity.CmsWorkflowEventUser> users) {
		this.users = users;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.core.entity.CmsWorkflowEvent)) return false;
		else {
			com.jeecms.core.entity.CmsWorkflowEvent cmsWorkflowEvent = (com.jeecms.core.entity.CmsWorkflowEvent) obj;
			if (null == this.getId() || null == cmsWorkflowEvent.getId()) return false;
			else return (this.getId().equals(cmsWorkflowEvent.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}