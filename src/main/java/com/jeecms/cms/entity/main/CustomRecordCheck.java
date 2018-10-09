package com.jeecms.cms.entity.main;

import org.apache.commons.lang.StringUtils;

import com.jeecms.cms.entity.main.base.BaseCustomRecordCheck;

public class CustomRecordCheck extends BaseCustomRecordCheck {
	private static final long serialVersionUID = 1L;
	/**
	 * 未审核
	 */
	public static final Integer DRAFT = 0;
	/**
	 * 审核中
	 */
	public static final Integer CHECKING = 1;
	/**
	 * 退回
	 */
	public static final Integer REJECT = -1;
	/**
	 * 已审核
	 */
	public static final Integer CHECKED = 2;
	

	public void init() {
		byte zero = 0;
		if (getCheckStep() == null) {
			setCheckStep(zero);
		}
		if (getRejected() == null) {
			setRejected(false);
		}
	}

	public void blankToNull() {
		if (StringUtils.isBlank(getCheckOpinion())) {
			setCheckOpinion(null);
		}
	}

	/* [CONSTRUCTOR MARKER BEGIN] */
	public CustomRecordCheck () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CustomRecordCheck (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CustomRecordCheck (
		java.lang.Integer id,
		java.lang.Byte checkStep,
		java.lang.Boolean rejected) {

		super (
			id,
			checkStep,
			rejected);
	}

	/* [CONSTRUCTOR MARKER END] */
}
