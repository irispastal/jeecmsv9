package com.jeecms.core.entity.base;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.jeecms.core.entity.CmsSmsRecord;

public class BaseCmsSms implements Serializable{
	private static final long serialVersionUID = 1L;

	public static String REF = "CmsSms";
	public static String PROP_ID="id";
	public static String PROP_NAME="name";
	public static String PROP_ACCESS_KEY_ID="accessKeyId";
	public static String PROP_ACCESS_KEY_SECRET="accessKeySecret";
	public static String PROP_TEMPLATE_CODE="templateCode";
	public static String PROP_TEMPLATE_PARAM="templateParam";
	public static String PROP__DAY_COUNT="dayCount";
	public static String PROP_INTERVAL_TIME="intervalTime";
	public static String PROP_EFFECTIVE_TIME="effectiveTime";
	public static String PROP_SIGN_NAME="signName";
	public static String PROP_SMS_UP_EXTEND_CODE="smsUpExtendCode";
	public static String PROP_OUT_ID="outId";
	public static String PROP_NATION_CODE="nationCode";
	public static String PROP_END_POINT="endPoint";
	public static String PROP_INVOKE_ID="invokeId";
	public static String PROP_SOURCE="source";
	public static String PROP_IS_CODE="isCode";
	public static String PROP_INTERVAL_UNIT="intervalUnit";
	public static String PROP_EFFECTIVE_UNIT="effectiveUnit";
	public static String PROP_CREATE_TIME="createTime";
	public static String PROP_RANDOM_NUMBER="randomNum";
	
	public BaseCmsSms(){
		initialize();
	}
	
	public BaseCmsSms(Integer id){
		this.setId(id);
		initialize();
	}


	public BaseCmsSms(Integer id, String name, String accessKeyId, String accessKeySecret, String templateCode,
			String templateParam, Integer intervalTime, Byte intervalUnit, Integer effectiveTime, Byte effectiveUnit,
			String signName, String smsUpExtendCode, String outId, String nationCode, String endPoint, String invokeId,
			Byte source, Boolean isCode, Date createTime, Integer randomNum) {
		super();
		this.id = id;
		this.name = name;
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.templateCode = templateCode;
		this.templateParam = templateParam;
		this.intervalTime = intervalTime;
		this.intervalUnit = intervalUnit;
		this.effectiveTime = effectiveTime;
		this.effectiveUnit = effectiveUnit;
		this.signName = signName;
		this.smsUpExtendCode = smsUpExtendCode;
		this.outId = outId;
		this.nationCode = nationCode;
		this.endPoint = endPoint;
		this.invokeId = invokeId;
		this.source = source;
		this.isCode = isCode;
		this.createTime = createTime;
		this.randomNum = randomNum;
	}

	protected void initialize() {
	}
	
	// primary key
	private Integer id;
	
	private String name;
	private String accessKeyId;//发送账号安全认证的Access Key ID/appId 注：不同接口参数类型可能不同
	private String accessKeySecret;//发送账号安全认证的Secret Access Key/appKey
	private String templateCode;//本次发送使用的模板Code 注：不同接口参数类型可能不同
	private String templateParam;//模板参数 注：不同接口参数类型可能不同
	private Integer intervalTime;//每次发送时间间隔
	private Byte intervalUnit;//间隔时间单位 0秒 1分 2时
	private Integer effectiveTime;//二维码有效时间
	private Byte effectiveUnit;//有效时间单位 0秒 1分 2时
	private String signName;//短信签名(阿里)
	private String smsUpExtendCode;//上行短信扩展码(阿里)
	private String outId;//提供给业务方扩展字段(阿里)
	private String nationCode;//区域码(腾讯)
	private String endPoint;//SMS服务域名，可根据环境选择具体域名(百度)
	private String invokeId;//发送使用签名的调用ID(百度)
	private Byte source;//SMS服务平台1阿里 2腾讯 3百度
	private Boolean isCode;//是否为验证码模板
	private Date createTime;//创建时间
	private Integer randomNum;//验证码位数
	
	private Set<CmsSmsRecord> smsRecords;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccessKeyId() {
		return accessKeyId;
	}
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	public String getAccessKeySecret() {
		return accessKeySecret;
	}
	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getTemplateParam() {
		return templateParam;
	}
	public void setTemplateParam(String templateParam) {
		this.templateParam = templateParam;
	}
	public Integer getIntervalTime() {
		return intervalTime;
	}
	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}
	public Integer getEffectiveTime() {
		return effectiveTime;
	}
	public void setEffectiveTime(Integer effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	public String getSignName() {
		return signName;
	}
	public void setSignName(String signName) {
		this.signName = signName;
	}
	public String getSmsUpExtendCode() {
		return smsUpExtendCode;
	}
	public void setSmsUpExtendCode(String smsUpExtendCode) {
		this.smsUpExtendCode = smsUpExtendCode;
	}
	public String getOutId() {
		return outId;
	}
	public void setOutId(String outId) {
		this.outId = outId;
	}
	public String getNationCode() {
		return nationCode;
	}
	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	public String getInvokeId() {
		return invokeId;
	}
	public void setInvokeId(String invokeId) {
		this.invokeId = invokeId;
	}
	public Byte getSource() {
		return source;
	}
	public void setSource(Byte source) {
		this.source = source;
	}
	public Boolean getIsCode() {
		return isCode;
	}
	public void setIsCode(Boolean isCode) {
		this.isCode = isCode;
	}
	
	public Byte getIntervalUnit() {
		return intervalUnit;
	}

	public void setIntervalUnit(Byte intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	public Byte getEffectiveUnit() {
		return effectiveUnit;
	}

	public void setEffectiveUnit(Byte effectiveUnit) {
		this.effectiveUnit = effectiveUnit;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getRandomNum() {
		return randomNum;
	}

	public void setRandomNum(Integer randomNum) {
		this.randomNum = randomNum;
	}

	public Set<CmsSmsRecord> getSmsRecords() {
		return smsRecords;
	}

	public void setSmsRecords(Set<CmsSmsRecord> smsRecords) {
		this.smsRecords = smsRecords;
	}

	private int hashCode = Integer.MIN_VALUE;

	@Override
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) {
				return super.hashCode();
			}else{
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof com.jeecms.core.entity.CmsSms)) {
			return false;
		}else{
			com.jeecms.core.entity.CmsSms cmsSms = (com.jeecms.core.entity.CmsSms)obj;
			if (null == this.getId() || null == cmsSms.getId()) {
				return false;
			}else{
				return (this.getId().equals(cmsSms.getId()));
			}
		}
	}
	
	
}
