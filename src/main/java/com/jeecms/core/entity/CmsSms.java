package com.jeecms.core.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jeecms.common.util.DateUtils;
import com.jeecms.core.entity.base.BaseCmsSms;

public class CmsSms extends BaseCmsSms{
	private static final long serialVersionUID = 1L;
	
	public static final Byte source_ali= 1;
	public static final Byte source_tx= 2;
	public static final Byte source_baidu= 3;
	
	//阿里固定参数
	//短信API产品名称
	public static final String product = "Dysmsapi";
	//短信API产品域名
	public static final String domain ="dysmsapi.aliyuncs.com";
	//初始化ascClient,暂时不支持多region
	public static final String regionId = "cn-hangzhou";
	public static final String endpointName = "cn-hangzhou";
	
	public JSONObject convertToJson(boolean isList){
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
		if (StringUtils.isNotBlank(getAccessKeyId())) {
			json.put("accessKeyId", "");
		}else{
			json.put("accessKeyId", "");
		}
		if (StringUtils.isNotBlank(getAccessKeySecret())) {
			json.put("accessKeySecret", "");
		}else{
			json.put("accessKeySecret", "");
		}
		if (getSource()!=null) {
			json.put("source", getSource());
		}else{
			json.put("source", "");
		}
		if (getSource()!=null) {
			Byte sourceType = getSource();
			String sourceName="未知";
			if(sourceType == 1) {
				sourceName="阿里云";
			}else if(sourceType == 3) {
				sourceName="百度云";
			}else if(sourceType == 2) {
				sourceName="腾讯云";
			}
			
			json.put("sourceName", sourceName);
		}else{
			json.put("sourceName", "");
		}
		if (getIsCode()!=null) {
			json.put("isCode", getIsCode());
		}else{
			json.put("isCode", "");
		}
		if (getCreateTime()!=null) {
			json.put("createTime", DateUtils.parseDateToTimeStr(getCreateTime()));
		}else{
			json.put("createTime", "");
		}
		if (!isList) {
			if (StringUtils.isNotBlank(getTemplateCode())) {
				json.put("templateCode", getTemplateCode());
			}else{
				json.put("templateCode", "");
			}
			if (getIntervalTime()!=null) {
				json.put("intervalTime", getIntervalTime());
			}else{
				json.put("intervalTime", "");
			}
			if (getIntervalUnit()!=null) {
				json.put("intervalUnit", getIntervalUnit());
			}else{
				json.put("intervalUnit", "");
			}
			if (getEffectiveTime()!=null) {
				json.put("effectiveTime", getEffectiveTime());
			}else{
				json.put("effectiveTime", "");
			}
			if (getEffectiveUnit()!=null) {
				json.put("effectiveUnit", getEffectiveUnit());
			}else{
				json.put("effectiveUnit", "");
			}
			if (StringUtils.isNotBlank(getSignName())) {
				json.put("signName", getSignName());
			}else{
				json.put("signName", "");
			}
			if (StringUtils.isNotBlank(getSmsUpExtendCode())) {
				json.put("smsUpExtendCode", getSmsUpExtendCode());
			}else{
				json.put("smsUpExtendCode", "");
			}
			if (StringUtils.isNotBlank(getOutId())) {
				json.put("outId", getOutId());
			}else{
				json.put("outId", "");
			}
			if (StringUtils.isNotBlank(getNationCode())) {
				json.put("nationCode", getNationCode());
			}else{
				json.put("nationCode", "");
			}
			if (StringUtils.isNotBlank(getEndPoint())) {
				json.put("endPoint", getEndPoint());
			}else{
				json.put("endPoint", "");
			}
			if (StringUtils.isNotBlank(getInvokeId())) {
				json.put("invokeId", getInvokeId());
			}else{
				json.put("invokeId", "");
			}
			if (getRandomNum()!=null) {
				json.put("randomNum", getRandomNum());
			}else{
				json.put("randomNum", "");
			}
			JSONArray paramArr = new JSONArray();
			if (StringUtils.isNotBlank(getTemplateParam())) {
				String param = getTemplateParam();
				String[] params = param.split(",");
				for (int i = 0; i < params.length; i++) {
					paramArr.put(i,params[i]);
				}
			}
			json.put("templateParam", paramArr);
		}
		return json;
	}
	
	public void init(){
		if (getIntervalTime()==null) {
			setIntervalTime(0);
		}
		if (getIntervalUnit()==null) {
			setIntervalUnit((byte)0);
		}
		if (getEffectiveTime()==null) {
			setEffectiveTime(0);
		}
		if (getEffectiveUnit()==null) {
			setEffectiveUnit((byte)0);
		}
		if (getIsCode()==null) {
			setIsCode(false);
		}
	}
	
	public CmsSms() {
		super();
	}

	public CmsSms(Integer id) {
		super(id);
	}

	public CmsSms(Integer id, String name, String accessKeyId, String accessKeySecret, String templateCode,
			String templateParam, Integer intervalTime, Byte intervalUnit, Integer effectiveTime, Byte effectiveUnit,
			String signName, String smsUpExtendCode, String outId, String nationCode, String endPoint, String invokeId,
			Byte source, Boolean isCode, Date createTime, Integer randomNum) {
		super(id, name, accessKeyId, accessKeySecret, templateCode, templateParam, intervalTime, intervalUnit, effectiveTime,
				effectiveUnit, signName, smsUpExtendCode, outId, nationCode, endPoint, invokeId, source, isCode, createTime,
				randomNum);
		// TODO Auto-generated constructor stub
	}

}
