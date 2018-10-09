package com.jeecms.core.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.common.util.DateUtils;
import com.jeecms.core.entity.base.BaseCmsSmsRecord;

public class CmsSmsRecord extends BaseCmsSmsRecord{
	private static final long serialVersionUID = 1L;

	public CmsSmsRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CmsSmsRecord(Integer id, String phone, Date sendTime, String sendContent) {
		super(id, phone, sendTime, sendContent);
		// TODO Auto-generated constructor stub
	}

	public CmsSmsRecord(Integer id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	public JSONObject convertToJson() {
		
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (getSendTime()!=null) {
			json.put("sendTime", DateUtils.parseDateToTimeStr(getSendTime()));
		}else{
			json.put("sendTime", "");
		}

		if (getPhone() != null) {
			json.put("phone", getPhone());
		}else{
			json.put("phone", "");
		}
		if (getSms() != null) {
			CmsSms cmsSms = getSms();
			Byte source = cmsSms.getSource();
			if(source == 1) {//SMS服务平台1阿里 2腾讯 3百度',
				json.put("smsName", "阿里云");
			}else if(source == 2) {
				json.put("smsName", "腾讯云");
			}else if(source == 3) {
				json.put("smsName", "百度云");
			}else {
				json.put("smsName", "未知");
			}
		}else{
			json.put("smsName", "未知");
		}
		if (getUser() != null) {
			CmsUser cmsUser = getUser();
			json.put("username", cmsUser.getUsername());
		}else{
			json.put("username", "");
		}
		if (getSite() != null) {
			CmsSite cmsSite = getSite();
			json.put("siteName", cmsSite.getName());
		}else{
			json.put("siteName", "");
		}
		if (getValidateType() != null) {
			Integer integer = getValidateType();
			if(integer == 1) {
				json.put("validateType", "注册验证");
			}else if(integer == 2) {
				json.put("validateType", "找回密码");
			}else {
				json.put("validateType", "未知验证");				
			}
		}else{
			json.put("validateType", "未知验证");
		}
		
		return json;
	}
	
	
	
}
