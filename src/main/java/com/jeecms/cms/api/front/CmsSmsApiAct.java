package com.jeecms.cms.api.front;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import com.alibaba.fastjson.JSON;
import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.client.SMsgServiceLocator;
import com.jeecms.cms.client.SMsg_PortType;
import com.jeecms.cms.manager.main.ApiAccountMng;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.baidubce.services.sms.model.SendMessageV2Response;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.common.util.SmsSendUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsSms;
import com.jeecms.core.entity.CmsSmsRecord;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;
import com.jeecms.core.entity.UnifiedUser;
import com.jeecms.core.manager.CmsSmsMng;
import com.jeecms.core.manager.CmsSmsRecordMng;
import com.jeecms.core.manager.CmsUserExtMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.UnifiedUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CmsSmsApiAct {
	static final boolean exactlySend = false;

	static final boolean NeedSignValidation = true;

	static final String tpl = "【宁德市公众健康服务平台】验证码：$。您正在使用短信验证码登录功能，该验证码仅用于身份验证，请勿泄露给他人使用。";

	static final String dbIp = "10.129.255.253";

	static final String dbName = "mas";

	static final String dbPort = "43306";

	static final String apiCode = "wjw2";

	static final String username = "wjw2";

	static final String password = "Abc@123456";

	private static final Logger log = LoggerFactory.getLogger(CmsSmsApiAct.class);

	/*@RequestMapping(value = "/send/test", method = RequestMethod.GET)
	public void test(String mobile, String key, HttpServletRequest request, HttpServletResponse response) {
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;

		try {
			String[] to = {mobile};
			if (key.equals("nd_@test_send")) {
				int res_code = sendSMsg(to, "666666");
				body = "\"" + res_code + "\"";
			} else {
				body = "\"key error\"";
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);

	}*/

	private int sendSMsg(String[] to, String code) throws RemoteException, ServiceException {
		String content = tpl.replace("$", code);
		SMsg_PortType client = null;
		try {
			SMsgServiceLocator service = new SMsgServiceLocator();
			client = service.getSMsg();
			//System.out.println("init client...");
			int init_code = client.init(dbIp, dbName, dbPort, username, password);
			if (init_code == 0) {
				System.out.println("init success");
				return client.sendSM(apiCode, username, password, to, content, 0);
			} else {
				System.out.println("init SMsgService fail, result code:" + init_code);
				return 100 + Math.abs(init_code);
			}
		} finally {
			if (client != null) {
				client.release();
			}
		}
	}

	/**
	 *
	 * @param smsType 发送短信类型用途 1： 注册 2：找回密码 3: 重置密码 4: 登录
	 * @param mobile 手机号码
	 * @param username 用户名
	 * @param vCode 图形验证码
	 * @param sign 签名
	 * @param request
	 * @param response
	 * @param session
	 */
	@SignValidate(need = NeedSignValidation, strict = false)
	@RequestMapping(value = "/sms/send", method = RequestMethod.POST)
	public void sendWithVCodeValidate(
			Integer smsType,
			String mobile,
			String vCode,
			String username,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) {
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		//系统默认三分钟有效
		Integer effectiveTime = 3*60*60*1000;
		//系统默认单位为秒
		Byte effectiveUnit = 1;

		// 0-参数存空 1-验证码错误 2-短信验证未激活
		int error_code = 0;

		// 非空检查
		ApiValidate.validateRequiredParams(request, errors, mobile, vCode);

		if (errors.hasErrors()) {
			error_code += 1;
		}

		// 图形验证码校验
		validateCode(vCode,errors,request,response);
		if (errors.hasErrors()) {
			error_code += 2;
		}

		//验证类型：0：无验证 1：邮件验证  2：SMS验证
		Integer type = config.getValidateType();
		if(type != 2) {
			error_code += 4;
			errors.addErrorString(Constants.API_MESSAGE_SMS_IS_DISABLE);
			errors.addErrorString(ResponseCode.API_CODE_SMS_IS_DISABLE);
		}

		if (errors.hasErrors()) {
			body = JSON.toJSONString(errors.getErrors());
			switch (error_code) {
				case 1:
				case 3:
				case 5:
				case 7:
					code = ResponseCode.API_CODE_PARAM_REQUIRED;
					message = Constants.API_MESSAGE_PARAM_REQUIRED;
					break;
				case 2:
				case 6:
					code = ResponseCode.API_CODE_CAPTCHA_CODE_ERROR;
					message = Constants.API_MESSAGE_CAPTCHA_CODE_ERROR;
					break;
				case 4:
					code = ResponseCode.API_CODE_SMS_IS_DISABLE;
					message = Constants.API_MESSAGE_SMS_IS_DISABLE;
					break;
				default:
					code = ResponseCode.API_CODE_PARAM_ERROR;
					message = Constants.API_MESSAGE_PARAM_ERROR;
			}
		} else {

			String values = "123456";
			Byte source = (byte) 0;

			Long id = config.getSmsID();
			String smsId = null;
			CmsSms bean = null;

			if(id != null) {
				smsId = id.toString();
				bean = manager.findById(Integer.parseInt(smsId));
			}
			// 短信下发限制校验
			validateSMS(config, mobile, errors, smsId);

			if (errors.hasErrors()) {
				body = JSON.toJSONString(errors.getErrors());
				message = Constants.API_MESSAGE_SMS_LIMIT;
				code = ResponseCode.API_CODE_SMS_LIMIT;
				ResponseUtils.renderApiJson(response, request, new ApiResponse(request, body, message, code));
				return;
			}

			if (exactlySend) {
				// 确定values 及 source
				// 验证码位数，默认6位
				Integer num = 6;
				if (bean != null) {
					source = bean.getSource();

					if(bean.getRandomNum() != null && bean.getRandomNum() > 0) {
						num = bean.getRandomNum();
					}
				} else {
					source = (byte) 0;
				}
				//创建验证码
				Random r = new Random();
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < num; i++) {
					buffer.append(r.nextInt(10));
				}
				values = buffer.toString();
				// values = "123456";
			}

			if (!errors.hasErrors()) {
				if (source.equals((byte)1)) {
					sendByALi(bean, mobile, values, errors,site,username,smsType);
				}else if (source.equals((byte)2)) {
					sendByTX(bean, mobile, values, errors,site,username,smsType);
				}else if (source.equals((byte)3)) {
					sendByBaiDu(bean, mobile, values, errors, site, username, smsType);
				} else {
					sendByDefault(bean, mobile, values, errors,site, username, smsType);
				}
			}

			if (errors.hasErrors()) {
				message = Constants.API_MESSAGE_SMS_ERROR;
				code = ResponseCode.API_CODE_SMS_ERROR;
			} else {
				//获取验证码有效时间
				if(bean != null && bean.getEffectiveTime() != null && bean.getEffectiveTime() > 0) {
					effectiveTime=bean.getEffectiveTime();
					if(bean.getEffectiveUnit() != null) {
						effectiveUnit = bean.getEffectiveUnit();//获取有效时间单位  有效时间单位 0秒 1分 2时
					}
					switch (effectiveUnit) {
						case 0:
							effectiveTime = effectiveTime * 1000;//秒-毫秒
							break;
						case 1:
							effectiveTime = effectiveTime *60 *1000;//分-毫秒
							break;
						case 2:
							effectiveTime = effectiveTime * 60 * 60 * 1000;//时-毫秒
							break;
						default:
							effectiveTime = effectiveTime * 1000;//秒-毫秒
							break;
					}
				}
				// 写入session
				if(smsType == 1) {//发送短信类型用途  1：注册   2：找回密码
					session.setAttribute("AUTO_CODE",values);//验证码值
					session.setAttribute("AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间
				} else if (smsType == 2){
					session.setAttribute("FORGOTPWD_AUTO_CODE",values);//验证码值
					session.setAttribute("FORGOTPWD_AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间
				}  else if (smsType == 3){
					session.setAttribute("RESETPWD_AUTO_CODE", values);//验证码值
					session.setAttribute("RESETPWD_AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间
				} else if (smsType == 5) {
					session.setAttribute("VALIDATEID_AUTO_CODE", values);//验证码值
					session.setAttribute("VALIDATEID_AUTO_CODE_CREAT_TIME", new Date().getTime()+effectiveTime);//验证码有效时间
				} else  {
					session.setAttribute("LOGIN_AUTO_CODE", values);
					session.setAttribute("LOGIN_AUTO_CODE_CREAT_TIME", new Date().getTime()+effectiveTime);// 验证码有效时间
				}
			}
		}

		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	/**
	 * 发送短信，若配置为手机注册-不需要验证图形验证码，若未配置-需要验证图形验证码
	 * @param mobilePhone 手机号
	 * @param SmsSendType 发送短信类型用途  1：注册   2：找回密码
	 * @param vCode		     验证码
	 * @param username 	     用户名      找回密码
	 * @param request
	 * @param response
	 */
	@RequestMapping("/sms/send_register_msg")
	public void send(Integer smsSendType,String mobilePhone,String vCode,String username,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		errors = ApiValidate.validateRequiredParams(request, errors,mobilePhone);
		if (!errors.hasErrors()) {
			//查询是否开启短信验证
			Integer type = config.getValidateType();
			if(type != 2) {//验证类型：0：无验证 1：邮件验证  2：SMS验证
				errors.addErrorString(Constants.API_MESSAGE_SMS_IS_DISABLE);
				errors.addErrorString(ResponseCode.API_CODE_SMS_IS_DISABLE);
			}
			Long id = config.getSmsID();
			String smsId = "";
			if(id != null && id != 0) {
				smsId = id.toString();
			}
			
			if(smsSendType == 1) {//发送短信类型用途  1：注册   2：找回密码
				errors = validateRegister(config,smsId,mobilePhone, vCode, errors, request, response);
			}else if(smsSendType == 2){
				errors = validateForgotPassword(config,smsId,mobilePhone,vCode,errors, request, response,username);
			}else {
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				errors.addErrorString(ResponseCode.API_CODE_PARAM_ERROR);
			}
			
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = errors.getErrors().get(1);					
			}else{
				//获取模板平台 1阿里 2腾讯 3百度
				CmsSms bean = null;
				if (StringUtils.isNotBlank(smsId)) {
					bean = manager.findById(Integer.parseInt(smsId));
					Byte source = bean.getSource();
					
					//创建验证码
					Random r = new Random();
					StringBuffer str = new StringBuffer();
					//验证码位数
					Integer num = 6;//默认6位
					if(bean.getRandomNum() != null && bean.getRandomNum() > 0) {
						num = bean.getRandomNum();
					}
					int i = 0;
					while(i < num) {
						str.append(r.nextInt(10));
						i++;
					}
					String values = str.toString();
		
					if (source.equals((byte)1)) {
						errors = sendByALi(bean, mobilePhone, values, errors,site,username,smsSendType);
					}else if (source.equals((byte)2)) {
						errors = sendByTX(bean, mobilePhone, values, errors,site,username,smsSendType);
					}else if (source.equals((byte)3)) {
						errors = sendByBaiDu(bean, mobilePhone, values, errors,site,username,smsSendType);
					}
					if (errors.hasErrors()) {
						message = Constants.API_MESSAGE_SMS_ERROR;
						code = ResponseCode.API_CODE_SMS_ERROR;
					}else{
						
						//获取验证码有效时间
						Integer effectiveTime = 3*60*60*1000;//系统默认三分钟有效
						Byte effectiveUnit = 1;
						if(bean.getEffectiveTime() != null && bean.getEffectiveTime() > 0) {
							effectiveTime=bean.getEffectiveTime();
							if(bean.getEffectiveUnit() != null) {
								effectiveUnit = bean.getEffectiveUnit();//获取有效时间单位  有效时间单位 0秒 1分 2时								
							}
							switch (effectiveUnit) {
							case 0:
								effectiveTime = effectiveTime * 1000;//秒-毫秒
								break;
							case 1:
								effectiveTime = effectiveTime *60 *1000;//分-毫秒
								break;
							case 2:
								effectiveTime = effectiveTime * 60 * 60 * 1000;//时-毫秒
								break;
							default:
								effectiveTime = effectiveTime * 1000;//秒-毫秒
								break;
							}	
						}
						if(smsSendType == 1) {//发送短信类型用途  1：注册   2：找回密码
							session.setAttribute("AUTO_CODE",values);//验证码值
							session.setAttribute("AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间						
						}else if(smsSendType == 2){
							session.setAttribute("FORGOTPWD_AUTO_CODE",values);//验证码值
							session.setAttribute("FORGOTPWD_AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间
						}
						
						message = Constants.API_MESSAGE_SUCCESS;
						code = ResponseCode.API_CODE_CALL_SUCCESS;
					}
				}else{
					message = Constants.API_MESSAGE_SMS_NOT_SET;
					code = ResponseCode.API_CODE_SMS_NOT_SET;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private WebErrors validateRegister(CmsConfig config,String smsId,
			String mobilePhone,String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response){

		//判断验证码是否正确
		errors = validateCode(vCode,errors,request,response);			
		if (errors.hasErrors()) {
			return errors;
		}else{
			//判断手机号是否已注册
			int countByPhone = userExtManager.countByPhone(mobilePhone);
			if (countByPhone!=0) {
				errors.addErrorString(Constants.API_MESSAGE_MOBILE_PHONE_EXIST);
				errors.addErrorString(ResponseCode.API_CODE_MOBILE_PHONE_EXIST);
				return errors;
			}
			errors = validateSMS(config,mobilePhone, errors,smsId);
		}
		return errors;
	}

	/**
	 * 
	 * @Title: validateForgotPassword   
	 * @Description: 找回密码SMS验证
	 * @param:  attr
	 * @param:  isSMSRegister
	 * @param:  smsId
	 * @param:  mobilePhone
	 * @param:  vCode
	 * @param:  errors
	 * @param:  request
	 * @param:  response    
	 * @return: WebErrors
	 */
	private WebErrors validateForgotPassword(CmsConfig config,String smsId,
			String mobilePhone,String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response,String username){
		if(StringUtils.isBlank(username)) {
			errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
			errors.addErrorString(ResponseCode.API_CODE_USER_NOT_FOUND);
		}else {
			//判断验证码是否正确
			errors = validateCode(vCode,errors,request,response);
		}
		if (errors.hasErrors()) {
			return errors;
		}else{
			//判断手机号与用户名是否匹配
			UnifiedUser user = unifiedUserMng.getByUsername(username);
			CmsUser user2 = cmsUserMng.findByUsername(username);
			CmsUserExt userExt =null;
			if(user2 != null) {
				 userExt = userExtManager.findById(user2.getId());				
			}
			if (user == null || user2 == null || userExt == null) {
				// 用户名不存在
				errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
				errors.addErrorString(ResponseCode.API_CODE_USER_NOT_FOUND);
			} else if (StringUtils.isBlank(userExt.getMobile())) {
				// 用户没有设置手机号码
				errors.addErrorString(Constants.API_MESSAGE_NOT_MOBILE);
				errors.addErrorString(ResponseCode.API_CODE_MOBILE_NOT_SET);
			}else {
				String mobile = userExt.getMobile();
				if(!mobile.equals(mobilePhone)) {
					//输入的手机号码与绑定的手机号不匹配
					errors.addErrorString(Constants.API_MESSAGE_MOBILE_MISMATCHING);
					errors.addErrorString(ResponseCode.API_CODE_MOBILE_MISMATCHING);
				}
			}
			errors = validateSMS(config,mobilePhone, errors,smsId);
		}
		return errors;
	}
	
	private WebErrors validateSMS(CmsConfig config,String mobilePhone, WebErrors errors, String smsId) {
		//判断手机号每日限制是否已达标
		List<CmsSmsRecord> findByPhone = smsRecordManager.findByPhone(mobilePhone);
		Integer dayCount = 0;
		if(config.getDayCount() != null) {
			dayCount = config.getDayCount();			
		}
		if (dayCount>0) {//每日限制若大于0则需要进行限制校验
			if (findByPhone.size() >= dayCount) {//比较当天发送记录是否达到每日限制次数
				errors.addErrorString(Constants.API_MESSAGE_SMS_LIMIT);
				errors.addErrorString(ResponseCode.API_CODE_SMS_LIMIT);
				return errors;
			} else if (findByPhone.size() > 0) {
				if (StringUtils.isNotBlank(smsId)) {
					CmsSms bean = manager.findById(Integer.parseInt(smsId));
					if (bean==null) {
						errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
						errors.addErrorString(ResponseCode.API_CODE_NOT_FOUND);
						return errors;
					}else{
						CmsSmsRecord record = findByPhone.get(0);
						//判断手机号每条短信相隔时间
						//获取间隔时间
						long intervalTime = 1*60*60*1000;//系统默认间隔1分钟
						Byte intervalUnit =1;
						long sendTime = record.getSendTime().getTime();
						long currentTime = new Date().getTime();
						if(bean.getIntervalTime() != null && bean.getIntervalTime() > 0) {
							intervalTime = bean.getIntervalTime();							
						}
						if(bean.getIntervalUnit() != null) {
							intervalUnit = bean.getIntervalUnit();							
						}
						switch (intervalUnit) {
						case 0:
							intervalTime = intervalTime * 1000;//秒-毫秒
							break;
						case 1:
							intervalTime = intervalTime *60 *1000;//分-毫秒
							break;
						case 2:
							intervalTime = intervalTime * 60 * 60 * 1000;//时-毫秒
							break;
						default:
							intervalTime = intervalTime * 1000;//秒-毫秒
							break;
						}
						if (currentTime-sendTime<intervalTime) {
							//当前时间减去上一次的发送时间，若小于限制间隔时间，则终止发送
							errors.addErrorString(Constants.API_MESSAGE_INTERVAL_NOT_ENOUGH);
							errors.addErrorString(ResponseCode.API_CODE_INTERVAL_NOT_ENOUGH);
							return errors;
						}
					}
				}else{
					//未配置
					errors.addErrorString(Constants.API_MESSAGE_SMS_NOT_SET);
					errors.addErrorString(ResponseCode.API_CODE_SMS_NOT_SET);
					return errors;
				}
			} 
		}
		return errors;
	}
	
	private WebErrors validateCode(String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response) {
		if (StringUtils.isBlank(vCode)) {
			errors.addErrorString(Constants.API_MESSAGE_CAPTCHA_CODE_ERROR);
			errors.addErrorString(ResponseCode.API_CODE_CAPTCHA_CODE_ERROR);
			return errors;
		}else{
			try {
				if (!imageCaptchaService.validateResponseForID(session.getSessionId(request, response), vCode)) {
					errors.addErrorString(Constants.API_MESSAGE_CAPTCHA_CODE_ERROR);
					errors.addErrorString(ResponseCode.API_CODE_CAPTCHA_CODE_ERROR);
					return errors;
				}
			} catch (Exception e) {
				errors.addErrorString(Constants.API_MESSAGE_CREATE_ERROR);
				errors.addErrorString(ResponseCode.API_CODE_CALL_FAIL);
			}
		}
		return errors;
	}

	private WebErrors sendByALi(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
		try {			
			 //请求失败这里会抛ClientException异常
			 SendSmsResponse sendSmsResponse = SmsSendUtils.sendByALi(bean, mobilePhone, values);
			 if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
				 //请求成功
				 CmsUser user =null;
				 if(StringUtils.isNotBlank(username)) {
					 user = cmsUserMng.findByUsername(username);
				 }				
				 smsRecordManager.save(bean, mobilePhone, smsSendType, site, user);
				 return errors;
			 }else{
				 errors.addErrorString(sendSmsResponse.getCode());
				 return errors;
			 }
		} catch (ClientException e) {
			e.printStackTrace();
			errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
		}
		return errors;
	}

	/***
	 * 
	 * @param username 
	 * @param site 
	 * @param smsSendType 
	 * @Title: sendByBaiDu   
	 * @Description: 百度短信服务
	 * @param: @param bean
	 * @param: @param mobilePhone
	 * @param: @param values
	 * @param: @param errors
	 * @param: @return      
	 * @return: WebErrors
	 */
	private WebErrors sendByBaiDu(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
	
        // 发送请求
        SendMessageV2Response response = SmsSendUtils.sendByBaiDu(bean, mobilePhone, values);
        // 解析请求响应 response.isSuccess()为true 表示成功;
        if (response != null && response.isSuccess()) {
        	//请求成功			
			CmsUser user =null;
			if(StringUtils.isNotBlank(username)) {
				user = cmsUserMng.findByUsername(username);
			}			
			smsRecordManager.save(bean, mobilePhone, smsSendType, site, user);
			return errors;
        } else {
        	errors.addErrorString(response.getCode());
			return errors;
        }
	}
	
	/**
	 * 腾讯短信服务
	 * @param bean
	 * @param mobilePhone
	 * @param values
	 * @param errors
	 * @param username 
	 * @param site 
	 * @param smsSendType 
	 * @return
	 */
	private WebErrors sendByTX(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
		try {
			if (bean.getTemplateCode()!=null) {
				SmsSingleSenderResult result = SmsSendUtils.sendByTX(bean, mobilePhone, values);
				if (result.result==0) {
					//请求成功					
					CmsUser user =null;
					if(StringUtils.isNotBlank(username)) {
						user = cmsUserMng.findByUsername(username);
					}
					smsRecordManager.save(bean, mobilePhone, smsSendType, site, user);
					return errors;
				}else{
					errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
					return errors;
				}
			}else{
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				return errors;
			}
		} catch (Exception e) {
			errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
		}
		return errors;
	}
	private WebErrors sendByDefault(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType) {
		String[] mobiles = {mobilePhone};
		int result = 0;
		int retry = 3;

		if (exactlySend) {
			try {
				result = sendSMsg(mobiles, values);
				while (result != 0 && retry > 1) {
					result = sendSMsg(mobiles, values);
					retry--;
					Thread.sleep(200L);
				}
				if (result != 0) {
					// 请求失败
					log.error("Fail to send sms code {} to mobile: {}, result code: {}", values, mobilePhone, result);
					errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
				}
			} catch (Exception e) {
				log.error("Fail to send sms code {} to mobile: {}", values, mobilePhone);
				if (e instanceof  RemoteException) {
					log.error("SMsgService Init Error", e);
				} else if (e instanceof ServiceException) {
					log.error("SMsgService Send Error", e);
				} else {
					log.error("Send SMS Error", e);
				}
				errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
				// 请求异常
				result = -110;
			}
		}

		if (result == 0) {
			//请求成功
			CmsUser user =null;
			if(StringUtils.isNotBlank(username)) {
				user = cmsUserMng.findByUsername(username);
			}
			smsRecordManager.save(bean, mobilePhone, smsSendType, site, user);
		}

		return errors;
	}

	@Autowired
	private CmsSmsMng manager;
	@Autowired
	private CmsSmsRecordMng smsRecordManager;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private CmsUserExtMng userExtManager;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private ApiAccountMng apiAccountMng;
}
