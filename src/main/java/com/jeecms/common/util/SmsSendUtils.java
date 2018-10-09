package com.jeecms.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV2Request;
import com.baidubce.services.sms.model.SendMessageV2Response;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.jeecms.core.entity.CmsSms;

public class SmsSendUtils {

	public static SendSmsResponse sendByALi(CmsSms bean,String mobilePhone,String values) throws ClientException{	
			//初始化ascClient
			IClientProfile profile = DefaultProfile.getProfile(CmsSms.regionId, bean.getAccessKeyId(), bean.getAccessKeySecret());
			DefaultProfile.addEndpoint(CmsSms.endpointName, CmsSms.regionId, CmsSms.product, CmsSms.domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			//组装请求对象	
			SendSmsRequest request = new SendSmsRequest();
			 //使用post提交
			 request.setMethod(MethodType.POST);
			 //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			 request.setPhoneNumbers(mobilePhone);
			 //必填:短信签名-可在短信控制台中找到
			 request.setSignName(bean.getSignName());
			 //必填:短信模板-可在短信控制台中找到
			 request.setTemplateCode(bean.getTemplateCode());
			 //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			 //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			 //格式化模板参数
			 String param = bean.getTemplateParam();
			 String[] split = null;
			 String[] value = null;
			 if (StringUtils.isNotBlank(param)) {
				split = param.split(",");
			 }
			 if (StringUtils.isNotBlank(values)) {
				value = values.split(",");
			 }
			 String templateParam = createParamForAli(split,value);
			 request.setTemplateParam(templateParam);
			 if (StringUtils.isNotBlank(bean.getSmsUpExtendCode())) {
				 //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
				 request.setSmsUpExtendCode("90997");
			 }				
			 if (StringUtils.isNotBlank(bean.getOutId())) {
				 //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
				 request.setOutId(bean.getOutId());
			 }
			 //请求失败这里会抛ClientException异常
		     return	 acsClient.getAcsResponse(request);
	}
	 
	public static SendMessageV2Response sendByBaiDu(CmsSms bean,String mobilePhone,String values){
		// 相关参数定义
        String endPoint = bean.getEndPoint(); // SMS服务域名，可根据环境选择具体域名
        String accessKeyId = bean.getAccessKeyId();  // 发送账号安全认证的Access Key ID
        String secretAccessKy = bean.getAccessKeySecret(); // 发送账号安全认证的Secret Access Key
        // ak、sk等config
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessKeyId, secretAccessKy));
        config.setEndpoint(endPoint);
        // 实例化发送客户端
        SmsClient smsClient = new SmsClient(config);
        
        // 定义请求参数
        String invokeId = bean.getInvokeId(); // 发送使用签名的调用ID
        String phoneNumber = mobilePhone; // 要发送的手机号码(只能填写一个手机号)
        String templateCode = bean.getTemplateCode(); // 本次发送使用的模板Code
        
        String param = bean.getTemplateParam();
        String[] split = null;
		String[] value = null;
		if (StringUtils.isNotBlank(param)) {
			split = param.split(",");
		}
		if (StringUtils.isNotBlank(values)) {
			value = values.split(",");
		}
		Map<String, String> vars =
				new HashMap<String, String>(); // 若模板内容为：您的验证码是${code},在${time}分钟内输入有效
		if (split!=null && value !=null && split.length==value.length) {
			for (int i = 0; i < split.length; i++) {
				vars.put(split[i], value[i]);
			}
		}
        //实例化请求对象
        SendMessageV2Request request = new SendMessageV2Request();
        request.withInvokeId(invokeId)
                .withPhoneNumber(phoneNumber)
                .withTemplateCode(templateCode)
                .withContentVar(vars);     
        return smsClient.sendMessage(request);       
	}
	
	public static SmsSingleSenderResult sendByTX(CmsSms bean,String mobilePhone,String values)throws Exception{
		SmsSingleSender sender = new SmsSingleSender(Integer.parseInt(bean.getAccessKeyId()),bean.getAccessKeySecret());
		ArrayList<String> params = new ArrayList<String>();
		SmsSingleSenderResult result = null;
		String[] value = null;
		if (StringUtils.isNotBlank(values)) {
			value = values.split(",");
		}
		if (value!=null) {
			for (int i = 0; i < value.length; i++) {
				params.add(value[i]);
			}
		}
		if (bean.getTemplateCode()!=null) {
			 result = sender.sendWithParam(bean.getNationCode(),mobilePhone,Integer.parseInt(bean.getTemplateCode()), params, "", "", "");
		}
		return result;		
	}
	
	
	private static String createParamForAli(String[] param, String[] values) {
		JSONObject json = new JSONObject();
		if (param!=null && values!=null && param.length==values.length) {
			for (int i = 0; i < param.length; i++) {
				json.put(param[i], values[i]);
			}
		}
		return json.toString();
	}

	
}
