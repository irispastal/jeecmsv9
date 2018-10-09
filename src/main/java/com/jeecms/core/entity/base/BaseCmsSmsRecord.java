package com.jeecms.core.entity.base;

import java.io.Serializable;
import java.util.Date;

import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsSms;
import com.jeecms.core.entity.CmsUser;

/**   
 * @Description:TODO
 * @author: SirFan
 * @date:   Mar 3, 2018 5:21:53 PM     
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */ 
public class BaseCmsSmsRecord implements Serializable{
	private static final long serialVersionUID = 1L;

	public static String PROP_ID="id";
	public static String PROP_PHONE="phone";
	public static String PROP_SEND_TIME="sendTime";
	public static String PROP_SEND_CONTENT="sendContent";
	
	public BaseCmsSmsRecord() {
		super();
	}
	
	public BaseCmsSmsRecord(Integer id) {
		super();
		this.id = id;
	}

	public BaseCmsSmsRecord(Integer id, String phone, Date sendTime, String sendContent) {
		super();
		this.id = id;
		this.phone = phone;
		this.sendTime = sendTime;
		this.sendContent = sendContent;
	}

	private Integer id;
	private String phone;
	private Date sendTime;
	private String sendContent;
	private Integer validateType;
	
	private CmsSms sms;
	private CmsSite site;
	private CmsUser user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getValidateType() {
		return validateType;
	}

	public void setValidateType(Integer validateType) {
		this.validateType = validateType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public CmsSms getSms() {
		return sms;
	}

	public void setSms(CmsSms sms) {
		this.sms = sms;
	}

	public CmsSite getSite() {
		return site;
	}

	public void setSite(CmsSite site) {
		this.site = site;
	}

	public CmsUser getUser() {
		return user;
	}

	public void setUser(CmsUser user) {
		this.user = user;
	}
	
	
	
}
