package com.jeecms.cms.api.front;

import java.io.File;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import com.alibaba.fastjson.JSON;
import com.jeecms.cms.api.*;
import com.jeecms.cms.api.vo.Archieve;
import com.jeecms.cms.api.vo.UserInfo;
import com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub;
import com.jeecms.cms.client.JkcxImplServiceLocator;
import com.jeecms.cms.util.XMLUtil;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.core.entity.*;
import com.jeecms.core.manager.*;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.entity.assist.CmsWebservice;
import com.jeecms.cms.entity.main.ApiAccount;
import com.jeecms.cms.entity.main.ApiRecord;
import com.jeecms.cms.entity.main.ApiUserLogin;
import com.jeecms.cms.entity.main.CmsThirdAccount;
import com.jeecms.cms.manager.assist.CmsWebserviceMng;
import com.jeecms.cms.manager.main.ApiAccountMng;
import com.jeecms.cms.manager.main.ApiRecordMng;
import com.jeecms.cms.manager.main.ApiUserLoginMng;
import com.jeecms.cms.manager.main.CmsThirdAccountMng;
import com.jeecms.cms.service.ImageSvc;
import com.jeecms.common.security.encoder.PwdEncoder;
import com.jeecms.common.util.AES128Util;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.util.Num62;
import com.jeecms.common.util.PropertyUtils;
import com.jeecms.common.web.HttpClientUtil;
import com.jeecms.common.web.LoginUtils;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserApiAct {
	private static final Logger log = LoggerFactory.getLogger(UserApiAct.class);
	
	private final String WEIXIN_JSCODE_2_SESSION_URL="weixin.jscode2sessionUrl";

	static final boolean NoSignValidation = false;

	/**
	 *
	 * @param appId
	 * @param encrypt 加密内容
	 * @return
	 */
	private String descrypt(String appId, String encrypt){
		Assert.notNull(appId, "appId不能为空");
		ApiAccount apiAccount=apiAccountMng.findByAppId(appId);
		String result = null;
		if(apiAccount!=null){
			String aesKey=apiAccount.getAesKey();
			try {
				result = AES128Util.decrypt(encrypt, aesKey,apiAccount.getIvKey());
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 *
	 * @param idCard
	 * @param mobile
	 * @param request
	 * @param response
	 */
	@SignValidate(need = !NoSignValidation)
	@RequestMapping(value = "/user/checkid", method = RequestMethod.POST)
	public void checkID(String idCard,
						HttpServletRequest request,
						HttpServletResponse response) {
		String body = "\"\"";
		String message = Constants.API_STATUS_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;

		WebErrors errors = WebErrors.create(request);

		// validate parameters
		ApiValidate.validateRequiredParams(request, errors, idCard);
		if (errors.hasErrors()) {
			message = Constants.API_MESSAGE_PARAM_REQUIRED;
			code = ResponseCode.API_CODE_PARAM_REQUIRED;
		} else {
			Archieve archieve = getArchieveByIDCard(idCard);
			if (archieve == null) {
				message = Constants.API_MESSAGE_USER_NOT_FOUND;
				code = ResponseCode.API_CODE_USER_NOT_FOUND;
			} else {
				body = JSON.toJSONString(archieve);
			}
		}

		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	/**
	 *
	 * @param idCard
	 * @param key
	 * @param request
	 * @param response
	 */
//	@RequestMapping(value = "/user/jkcx", method = RequestMethod.POST)
	public void jkcx(String idCard,
						String key,
						HttpServletRequest request,
						HttpServletResponse response) {
		String body = "\"\"";
		String message = Constants.API_STATUS_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;

		try {
			if (key.equals("lushian")) {
				String ywgndm = "CXJKDAXX";
				String ywxml =  "<YWXML>\n" +
						"    <DLSJ>\n" +
						"        <DLDM>mhwz</DLDM>\n" +
						"        <DLMM>mhwz</DLMM>\n" +
						"    </DLSJ>\n" +
						"    <YWSJ>\n" +
						"        <ZJHM>$</ZJHM>\n" +
						"        <ZJLX>01</ZJLX>\n" +
						"    </YWSJ>\n" +
						"</YWXML>";
				ywxml = ywxml.replace("$", idCard);
				Gzfw_jkcxSoapBindingStub binding = null;

				binding = (Gzfw_jkcxSoapBindingStub) new JkcxImplServiceLocator().getgzfw_jkcx();

				if (binding != null) {
					binding.setTimeout(60000);
					body = "\"" + binding.jkcx_Jkhs(ywgndm, ywxml) + "\"";
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	private Archieve getArchieveByIDCard(String idCard) {
		Archieve archieve = new Archieve();

		String ywgndm = "CXJKDAXX";
		String ywxml =  "<YWXML>\n" +
						"    <DLSJ>\n" +
						"        <DLDM>mhwz</DLDM>\n" +
						"        <DLMM>mhwz</DLMM>\n" +
						"    </DLSJ>\n" +
						"    <YWSJ>\n" +
						"        <ZJHM>$</ZJHM>\n" +
						"        <ZJLX>01</ZJLX>\n" +
						"    </YWSJ>\n" +
						"</YWXML>";
		ywxml = ywxml.replace("$", idCard);
		Gzfw_jkcxSoapBindingStub binding = null;

		try {
			binding = (Gzfw_jkcxSoapBindingStub) new JkcxImplServiceLocator().getgzfw_jkcx();
		} catch (ServiceException e) {
			e.printStackTrace();
		}


		try {
			if (binding != null) {
				// Time out after a minute
				binding.setTimeout(60000);
				String xml = binding.jkcx_Jkhs(ywgndm, ywxml);
				Document doc = DocumentHelper.parseText(xml);

				Map<String, Object> map = XMLUtil.Dom2Map(doc);
				if (map.get("STATUS").equals("T")) {
					if (map.get("YWSJ") != null && StringUtils.isNotBlank(map.get("YWSJ").toString())) {
						Map<String, Object> ywsj = (Map<String, Object>) map.get("YWSJ");
						if (ywsj != null) {
							Map<String, Object> jbxx = (Map<String, Object>) ywsj.get("DA_GR_JBXX");

							if (StringUtils.isNotBlank(jbxx.get("BRDHHM").toString())){
								archieve.setMobile(jbxx.get("BRDHHM").toString());
							}
							if (StringUtils.isNotBlank(jbxx.get("LXRDHHM").toString())) {
								archieve.setAssociatedMobile(jbxx.get("LXRDHHM").toString());
							}
							if (StringUtils.isNotBlank(jbxx.get("XM").toString())) {
								archieve.setName(jbxx.get("XM").toString());
							}
							return archieve;
						}
					} else {
						System.out.println(map.get("MSG"));
					}
				} else {
					map.get("MSG");
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 *
	 * @param username 用戶名
	 * @param loginPassword 登錄密碼
	 * @param idCard 身份證號碼
	 * @param userExt
	 * @param smsCode 短信驗證碼
	 * @param request
	 * @param response
	 */
	@SignValidate(need = !NoSignValidation)
	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	public void userRegester(String username,
							 String loginPassword,
							 String idCard,
							 CmsUserExt userExt,
							 String smsCode,
							 HttpServletRequest request,
							 HttpServletResponse response){
		// TODO
		// 1)添加簽名校驗
		// 2)密碼使用AES加密傳輸

		String body = "\"\"";
		String message = Constants.API_STATUS_FAIL;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;

		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		WebErrors errors = WebErrors.create(request);
		// 参数校验
		validateSmsSubmit(1, userExt.getMobile(), username, loginPassword, smsCode, errors, site, request, response);

		boolean disabled = false;
		if (config.getMemberConfig().isCheckOn()) {
			disabled = true;
		}

		if (!errors.hasErrors()) {
			String ip = RequestUtils.getIpAddr(request);
			Map<String, String> attrs = RequestUtils.getRequestMap(request, "attr_");
			attrs.put("idCard", idCard);
			CmsUser user = cmsUserMng.registerMember(username, null, loginPassword, ip, null, null, disabled, userExt, attrs);
			cmsWebserviceMng.callWebService("false", username, loginPassword, null, userExt,
					CmsWebservice.SERVICE_TYPE_ADD_USER);
			// 添加本人身份信息到身份信息管理
			CmsUserIdcard idcard = userIdcardMng.register(user, idCard);
			// 绑定本人身份证
			user.getAttr().put("bindid", idcard.getId().toString());
			cmsUserMng.updateUser(user);

			log.info("member register success. username={}", username);
			message = Constants.API_MESSAGE_SUCCESS;
		} else {
			message = Constants.API_MESSAGE_PARAM_ERROR;
			code = ResponseCode.API_CODE_PARAM_ERROR;
		}
		if (errors.hasErrors()) {
			body = JSON.toJSONString(errors.getErrors());
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	@RequestMapping(value = "/user/smsLogin")
	public void userSmsLogin(String mobile,
							 String smsCode,
							 String appId,
							 String nonce_str,
							 String sign,
							 HttpServletRequest request,
							 HttpServletResponse response) {
		ApiAccount apiAccount;
		CmsUser user;
		String message;
		String code;

		WebErrors errors=WebErrors.create(request);
		String body = "\"\"";

		// 短信验证码用途 1：注册 2： 找回密码 3：重置密码 4：登录
		Integer type = 4;

		// 非空验证
		ApiValidate.validateRequiredParams(request, errors, mobile, smsCode, appId, nonce_str, sign);
		// 签名校验

		if (!NoSignValidation) {
			int flag = ValidationUtil.validateSign(apiRecordMng, apiAccountMng, request, errors, appId, sign);
			switch (flag) {
				case 1:
					code = ResponseCode.API_CODE_PARAM_ERROR;
					message = Constants.API_MESSAGE_APP_PARAM_ERROR;
					break;
				case 2:
					code = ResponseCode.API_CODE_CALL_FAIL;
					message = Constants.API_MESSAGE_APP_PARAM_ERROR;
					break;
				case 3:
					code = ResponseCode.API_CODE_SIGN_ERROR;
					message = Constants.API_STATUS_FAIL;
					break;
				case 4:
					message=Constants.API_MESSAGE_REQUEST_REPEAT;
					code=ResponseCode.API_CODE_REQUEST_REPEAT;
					break;
			}
		}

		if (errors.hasErrors()) {
			code = ResponseCode.API_CODE_PARAM_ERROR;
			message = Constants.API_MESSAGE_APP_PARAM_ERROR;
		} else {
			CmsUserExt userExt = userExtManager.findByPhone(mobile);
			if (userExt != null) {
				user = userExt.getUser();
				apiAccount = apiAccountMng.findByAppId(appId);
				String username = user.getUsername();
				String aesKey = apiAccount.getAesKey();

				// 短信验证
				ValidationUtil.validateSmsCode(type, smsCode, errors, request, response, session);

				if (!errors.hasErrors()) {
					//解决会话固定漏洞
					LoginUtils.logout();
					//sessionID加密后返回 ,该值作为用户数据交互识别的关键值
					//调用接口端将该值保存，调用用户数据相关接口传递加密sessionID后的值，服务器端解密后查找用户
					String sessionKey=session.getSessionId(request, response);
					apiUserLoginMng.userLogin(username, appId, sessionKey,request,response);
					//前后台统一登录 api和web
					LoginUtils.loginShiro(request, response, username);
					CmsUtils.setUser(request, user);

					UserInfo info = new UserInfo();
					try {
						String sec = AES128Util.encrypt(sessionKey, aesKey, apiAccount.getIvKey());
						info = UserConvertToInfo(user, sec);
					} catch (Exception e) {
						e.printStackTrace();
					}
					apiRecordMng.callApiRecord(RequestUtils.getIpAddr(request),
							appId, "/user/smsLogin",sign);
					body = JSON.toJSONString(info);
					message=Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				} else {
					errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
					message = Constants.API_MESSAGE_SMS_ERROR;
					code = ResponseCode.API_CODE_SMS_ERROR;
				}
			} else {
				//用户不存在
				errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
				message=Constants.API_MESSAGE_USER_NOT_FOUND;
				code=ResponseCode.API_CODE_USER_NOT_FOUND;
			}

		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	/**
	 * 用户登录API
	 * @param username 用户名 必选
	 * @param aesPassword 加密密码 必选
	 * @param appId appID 必选
	 * @param sign 签名必选
	 */
	@SignValidate(need = !NoSignValidation)
	@RequestMapping(value = "/user/myLogin", method = RequestMethod.POST)
	public void myLogin(
			String username,
			String aesPassword,
			String vCode,
			String appId,
			String sign,
			Boolean rememberMe,
			HttpServletRequest request,
			HttpServletResponse response) {

		WebErrors errors=WebErrors.create(request);
		String body="\"\"";

		String message;
		String code;

		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors, appId,sign,username,aesPassword, vCode);

		if(errors.hasErrors()){
			message = Constants.API_MESSAGE_PARAM_REQUIRED;
			code = ResponseCode.API_CODE_PARAM_REQUIRED;
		}else{
			//校验图片验证码
			boolean isValid = false;
			try {
				isValid = imageCaptchaService.validateResponseForID(session.getSessionId(request, response), vCode);
			} catch (CaptchaServiceException e) {}

			if (!isValid) {
				code = ResponseCode.API_CODE_CAPTCHA_CODE_ERROR;
				message = Constants.API_MESSAGE_CAPTCHA_CODE_ERROR;
			} else {
				ApiAccount apiAccount = apiAccountMng.findByAppId(appId);
				CmsUser user=cmsUserMng.findByUsername(username);
				if(user!=null){
					String aesKey=apiAccount.getAesKey();
					//解密用户输入的密码
					String encryptPass="";
					try {
						encryptPass = AES128Util.decrypt(aesPassword, aesKey,apiAccount.getIvKey());
					} catch (Exception e) {
						//e.printStackTrace();
					}
					//验证用户密码
					if(cmsUserMng.isPasswordValid(user.getId(), encryptPass)){
						//解决会话固定漏洞
						LoginUtils.logout();
						//sessionID加密后返回 ,该值作为用户数据交互识别的关键值
						//调用接口端将该值保存，调用用户数据相关接口传递加密sessionID后的值，服务器端解密后查找用户
						String sessionKey=session.getSessionId(request, response);
						apiUserLoginMng.userLogin(username, appId, sessionKey,request,response);
						//前后台统一登录 api和web
						// LoginUtils.loginShiro(request, response, username);
						Subject subject = SecurityUtils.getSubject();

						// 不论用户输入的是用户名还是手机号, 前台标签统一用username接收
						UsernamePasswordToken token = new UsernamePasswordToken(username, encryptPass);
						// 设置是否'记住我'
						rememberMe = rememberMe == null ? false : rememberMe;
						token.setRememberMe(rememberMe);
						subject.login(token);

						CmsUtils.setUser(request, user);

						UserInfo info = new UserInfo();
						try {
							String sec = AES128Util.encrypt(sessionKey, aesKey, apiAccount.getIvKey());
							info = UserConvertToInfo(user, sec);
						} catch (Exception e) {
							e.printStackTrace();
						}
						apiRecordMng.callApiRecord(RequestUtils.getIpAddr(request),
								appId, "/user/login",sign);

						body = JSON.toJSONString(info);
						message=Constants.API_MESSAGE_SUCCESS;
						code = ResponseCode.API_CODE_CALL_SUCCESS;
					}else{
						//密码错误
						message=Constants.API_MESSAGE_PASSWORD_ERROR;
						code=ResponseCode.API_CODE_PASSWORD_ERROR;
					}
				}else{
					//用户不存在
					message=Constants.API_MESSAGE_USER_NOT_FOUND;
					code=ResponseCode.API_CODE_USER_NOT_FOUND;
				}
			}

		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}

	/**
	 *
	 * @param user
	 * @param sec 加密信息
	 * @return
	 */
	private UserInfo UserConvertToInfo(CmsUser user, String sec) {
		UserInfo info = new UserInfo();
		info.setId(user.getId());
		info.setUsername(user.getUsername());
		info.setRealname(user.getRealname());
		info.setAvatar(user.getUserImg());
		info.setMoblie(user.getMobile());
		Map<String, String> attr = user.getAttr();
		if (attr != null) {
			if (StringUtils.isNotBlank(attr.get("bindid"))) {
				Integer bindid = Integer.parseInt(attr.get("bindid"));
				Set<CmsUserIdcard> idcards = user.getUserIdcardSet();
				if (idcards != null && idcards.size() > 0) {
					for (CmsUserIdcard idcard : idcards) {
						if (idcard.getId().equals(bindid)) {
							info.setBindIdcard(idcard.convertoJSON());
						}
					}
				}
			}
			if (StringUtils.isNotBlank(attr.get("idCard"))) {
				info.setIdCard(attr.get("idCard"));
			}
		}
		info.setQq(user.getQq());
		info.setComefrom(user.getComefrom());
		info.setSec(sec);
		return info;
	}



	/**
	 * 用户登录API
	 * @param username 用户名 必选
	 * @param aesPassword 加密密码 必选
	 * @param appId appID 必选
	 * @param nonce_str 随机字符串 必选
	 * @param sign 签名必选
	 */
	@RequestMapping(value = "/user/login")
	public void userLogin(
			String username,String aesPassword,
			String appId,String nonce_str,String sign,
			HttpServletRequest request,HttpServletResponse response)
			throws JSONException {
		String body="\"\"";
		String message=Constants.API_STATUS_FAIL;
		String code=ResponseCode.API_CODE_CALL_SUCCESS;
		WebErrors errors=WebErrors.create(request);
		ApiAccount apiAccount = null;
		CmsUser user = null;
		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors, appId,
				nonce_str,sign,username,aesPassword);
		if(!errors.hasErrors()){
			apiAccount=apiAccountMng.findByAppId(appId);
			errors=ApiValidate.validateApiAccount(request, errors, apiAccount);
			if(errors.hasErrors()){
				code=ResponseCode.API_CODE_APP_PARAM_ERROR;
			}else{
				//验证签名
				errors=ApiValidate.validateSign(request, errors,apiAccount, sign);
				if(errors.hasErrors()){
					code=ResponseCode.API_CODE_SIGN_ERROR;
				}
			}
		}else{
			code=ResponseCode.API_CODE_PARAM_REQUIRED;
		}
		if(errors.hasErrors()){
			message=errors.getErrors().get(0);
		}else{
			//签名数据不可重复利用
			ApiRecord record=apiRecordMng.findBySign(sign, appId);
			if(record!=null){
				message=Constants.API_MESSAGE_REQUEST_REPEAT;
				code=ResponseCode.API_CODE_REQUEST_REPEAT;
			}else{
				user=cmsUserMng.findByUsername(username);
				if(user!=null){
					String aesKey=apiAccount.getAesKey();
					//解密用户输入的密码
					String encryptPass="";
					try {
						encryptPass = AES128Util.decrypt(aesPassword, aesKey,apiAccount.getIvKey());
					} catch (Exception e) {
						//e.printStackTrace();
					}
					//验证用户密码
					if(cmsUserMng.isPasswordValid(user.getId(), encryptPass)){
						if(user.getSites() == null || user.getSites().size() == 0){
							message = "用户没有站群权限";
							code = ResponseCode.API_CODE_USER_NOT_HAS_PERM;
							ApiResponse apiResponse=new ApiResponse(request, body, message,code);
							ResponseUtils.renderApiJson(response, request, apiResponse);
							return;
						}
						//解决会话固定漏洞
						LoginUtils.logout();
						//sessionID加密后返回 ,该值作为用户数据交互识别的关键值
						//调用接口端将该值保存，调用用户数据相关接口传递加密sessionID后的值，服务器端解密后查找用户
						String sessionKey=session.getSessionId(request, response);
						apiUserLoginMng.userLogin(username, appId, sessionKey,request,response);
						//前后台统一登录 api和web
						LoginUtils.loginShiro(request, response, username);
						CmsUtils.setUser(request, user);
						try {
							//加密返回
							body="\""+AES128Util.encrypt(sessionKey, aesKey,apiAccount.getIvKey())+"\"";
						} catch (Exception e) {
							e.printStackTrace();
						}
						apiRecordMng.callApiRecord(RequestUtils.getIpAddr(request),
								appId, "/user/login",sign);
						message=Constants.API_MESSAGE_SUCCESS;
					}else{
						//密码错误
						message=Constants.API_MESSAGE_PASSWORD_ERROR;
						code=ResponseCode.API_CODE_PASSWORD_ERROR;
					}
				}else{
					//用户不存在
					message=Constants.API_MESSAGE_USER_NOT_FOUND;
					code=ResponseCode.API_CODE_USER_NOT_FOUND;
				}
			}
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	/**
	 * 获取用户状态API
	 * @param username 用户名 必选
	 * @param sessionKey 会话标识 必选
	 * @param appId appID 必选
	 * @param nonce_str 随机字符串 必选
	 * @param sign 签名必选
	 */
	@RequestMapping(value = "/user/getStatus")
	public void getUserStatus(
			String username,String sessionKey,
			String appId,String nonce_str,String sign,
			HttpServletRequest request,HttpServletResponse response) {
		String body="\"\"";
		String message=Constants.API_STATUS_FAIL;
		String code=ResponseCode.API_CODE_CALL_SUCCESS;
		WebErrors errors=WebErrors.create(request);
		ApiAccount apiAccount = null;
		CmsUser user = null;
		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors, appId,
				nonce_str,sign,username,sessionKey);
		if(!errors.hasErrors()){
			apiAccount=apiAccountMng.findByAppId(appId);
			errors=ApiValidate.validateApiAccount(request, errors, apiAccount);
			if(errors.hasErrors()){
				code=ResponseCode.API_CODE_APP_PARAM_ERROR;
			}else{
				//验证签名
				errors=ApiValidate.validateSign(request, errors,apiAccount, sign);
				if(errors.hasErrors()){
					code=ResponseCode.API_CODE_SIGN_ERROR;
				}
			}
		}else{
			code=ResponseCode.API_CODE_PARAM_REQUIRED;
		}
		if(errors.hasErrors()){
			message="\""+errors.getErrors().get(0)+"\"";
		}else{
			user=cmsUserMng.findByUsername(username);
			if(user!=null){
				String aesKey=apiAccount.getAesKey();
				String decryptSessionKey = null;
				try {
					decryptSessionKey = AES128Util.decrypt(sessionKey, aesKey,apiAccount.getIvKey());
				} catch (Exception e) {
					//e.printStackTrace();
				}
				if(StringUtils.isNotBlank(decryptSessionKey)){
					ApiUserLogin userLogin=apiUserLoginMng.findUserLogin(username, decryptSessionKey);
					if(userLogin!=null){
						message=Constants.API_MESSAGE_USER_STATUS_OVER_TIME;
						code=ResponseCode.API_CODE_USER_STATUS_OVER_TIME;
						if(userLogin.getActiveTime()!=null){
							Date now=Calendar.getInstance().getTime();
							Double timeOver=DateUtils.getDiffMinuteTwoDate(userLogin.getActiveTime(), now);
							if(timeOver<=Constants.USER_OVER_TIME){
								message=Constants.API_MESSAGE_USER_STATUS_LOGIN;
								code=ResponseCode.API_CODE_USER_STATUS_LOGIN;
								LoginUtils.loginShiro(request, response, username);
							}else{
								CmsUser currUser=CmsUtils.getUser(request);
								if(currUser!=null){
									apiUserLoginMng.userActive(request,response);
								}else{
									//如果记住登录的
									Subject subject = SecurityUtils.getSubject();
									if(subject.isRemembered()){
										String rememberUser =  (String) subject.getPrincipal();
										LoginUtils.loginShiro(request, response, rememberUser);
									}else{
										LoginUtils.logout();
									}
								}
							}
						}
					}else{
						message=Constants.API_MESSAGE_USER_STATUS_NOT_LOGIN;
						code=ResponseCode.API_CODE_USER_STATUS_LOGOUT;
						LoginUtils.logout();
					}
				}else{
					message=Constants.API_MESSAGE_PARAM_ERROR;
					code=ResponseCode.API_CODE_PARAM_ERROR;
				}
			}else{
				//用户不存在
				message=Constants.API_MESSAGE_USER_NOT_FOUND;
				code=ResponseCode.API_CODE_USER_NOT_FOUND;
			}
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 微信小程序-微信用户登录获取sessionKey和openid API
	 * @param js_code 微信小程序登录code 必选
	 * @param grant_type 非必选
	 * @param appId appID 必选
	 * @param nonce_str 随机字符串 必选
	 * @param sign 签名必选
	 */
	@RequestMapping(value = "/user/weixinLogin")
	public void weixinAppLogin(
			String js_code,String grant_type,
			String appId,String nonce_str,String sign,
			HttpServletRequest request,HttpServletResponse response) 
					{
		String body="\"\"";
		String message=Constants.API_STATUS_FAIL;
		String code=ResponseCode.API_CODE_CALL_SUCCESS;
		WebErrors errors=WebErrors.create(request);
		if(StringUtils.isNotBlank(grant_type)){
			grant_type="authorization_code";
		}
		ApiAccount apiAccount = null;
		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors, appId,
				nonce_str,sign,js_code);
		if(!errors.hasErrors()){
			apiAccount=apiAccountMng.findByAppId(appId);
			errors=ApiValidate.validateApiAccount(request, errors, apiAccount);
			if(errors.hasErrors()){
				code=ResponseCode.API_CODE_APP_PARAM_ERROR;
			}else{
				//验证签名
				errors=ApiValidate.validateSign(request, errors,apiAccount, sign);
				if(errors.hasErrors()){
					code=ResponseCode.API_CODE_SIGN_ERROR;
				}
			}
		}else{
			code=ResponseCode.API_CODE_PARAM_REQUIRED;
		}
		if(errors.hasErrors()){
			message=errors.getErrors().get(0);
		}else{
			//签名数据不可重复利用
			ApiRecord record=apiRecordMng.findBySign(sign, appId);
			if(record!=null){
				message=Constants.API_MESSAGE_REQUEST_REPEAT;
				code=ResponseCode.API_CODE_REQUEST_REPEAT;
			}else{
				initWeiXinJsCode2SessionUrl();
				Map<String,String>params=new HashMap<String, String>();
				CmsConfig config=configMng.get();
				params.put("appid", config.getWeixinAppId());
				params.put("secret", config.getWeixinAppSecret());
				params.put("js_code",js_code);
				params.put("grant_type",grant_type);
				String result=HttpClientUtil.postParams(getWeiXinJsCode2SessionUrl(),
						params);
				JSONObject json;
				Object openId = null;
				try {
					json = new JSONObject(result);
					openId=json.get("openid");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String openid=null;
				if(openId!=null){
					openid=(String)openId;
				}
				if(StringUtils.isNotBlank(openid)){
					body=thirdLoginGetSessionKey(apiAccount, openid,null, 
							Constants.THIRD_SOURCE_WEIXIN_APP, request, response);
				}
				message=Constants.API_MESSAGE_SUCCESS;
			}
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 第三方登录API
	 * @param thirdKey 第三方key 必选
	 * @param source 第三方来源 非必选 默认微信小程序
	 * @param username 为第三方用户指定创建的用户名
	 * @param appId appID 必选
	 * @param nonce_str 随机字符串 必选
	 * @param sign 签名必选
	 */
	@RequestMapping(value = "/user/thirdLogin")
	public void thirdLoginApi(
			String thirdKey,String source,String username,
			String appId,String nonce_str,String sign,
			HttpServletRequest request,HttpServletResponse response) 
					throws JSONException {
		String body="\"\"";
		String message=Constants.API_STATUS_FAIL;
		String code=ResponseCode.API_CODE_CALL_SUCCESS;
		if(StringUtils.isNotBlank(source)){
			source=Constants.THIRD_SOURCE_WEIXIN_APP;
		}
		WebErrors errors=WebErrors.create(request);
		ApiAccount apiAccount = null;
		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors, appId,
				nonce_str,sign,thirdKey);
		if(!errors.hasErrors()){
			apiAccount=apiAccountMng.findByAppId(appId);
			errors=ApiValidate.validateApiAccount(request, errors, apiAccount);
			if(errors.hasErrors()){
				code=ResponseCode.API_CODE_APP_PARAM_ERROR;
			}else{
				//验证签名
				errors=ApiValidate.validateSign(request, errors,apiAccount, sign);
				if(errors.hasErrors()){
					code=ResponseCode.API_CODE_SIGN_ERROR;
				}
			}
		}else{
			code=ResponseCode.API_CODE_PARAM_REQUIRED;
		}
		if(errors.hasErrors()){
			message=errors.getErrors().get(0);
		}else{
			//签名数据不可重复利用
			ApiRecord record=apiRecordMng.findBySign(sign, appId);
			if(record!=null){
				message=Constants.API_MESSAGE_REQUEST_REPEAT;
				code=ResponseCode.API_CODE_REQUEST_REPEAT;
			}else{
				body=thirdLoginGetSessionKey(apiAccount, thirdKey,
						username, source, request, response);
				apiRecordMng.callApiRecord(RequestUtils.getIpAddr(request),
						appId, "/user/thirdLogin",sign);
				message=Constants.API_MESSAGE_SUCCESS;
			}
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 添加会员用户
	 * @param username 用户名   必选
	 * @param email 邮箱 非必选
	 * @param loginPassword 密码  必选
	 * @param realname 真实姓名 非必选
	 * @param gender 性别 非必选
	 * @param birthdayStr 生日 格式"yyyy-MM-dd" 例如"1980-01-01" 非必选
	 * @param phone  电话 非必选
	 * @param mobile 手机 非必选
	 * @param qq qq号  非必选
	 * @param userImg 用户头像  非必选
	 */
	@SignValidate(need = !NoSignValidation)
	@RequestMapping(value = "/user/add")
	public void userAdd(
			String username, String email, String loginPassword,
			String realname,Boolean gender,String birthdayStr,
			String phone,String mobile,String qq,String userImg,
			HttpServletRequest request,HttpServletResponse response) throws JSONException {
		String body="\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors=WebErrors.create(request);
		CmsUser user = null;
		//验证公共非空参数
		errors=ApiValidate.validateRequiredParams(request,errors,username,loginPassword);
		if(!errors.hasErrors()){
			user=cmsUserMng.findByUsername(username);
			if(user==null){
				String ip = RequestUtils.getIpAddr(request);
				Map<String,String>attrs=RequestUtils.getRequestMap(request, "attr_");
				boolean disabled=false;
				CmsSite site = CmsUtils.getSite(request);
				CmsConfig config = site.getConfig();
				if(config.getMemberConfig().isCheckOn()){
					disabled=true;
				}
				CmsUserExt userExt=new CmsUserExt();
				if(StringUtils.isNotBlank(birthdayStr)){
					userExt.setBirthday(DateUtils.parseDayStrToDate(birthdayStr));
				}
				userExt.setGender(gender);
				userExt.setMobile(mobile);
				userExt.setPhone(phone);
				userExt.setQq(qq);
				userExt.setRealname(realname);
				userExt.setUserImg(userImg);
				user=cmsUserMng.registerMember(username, email, loginPassword, ip, null,null,disabled,userExt,attrs);
				cmsWebserviceMng.callWebService("false",username, loginPassword, email, userExt,CmsWebservice.SERVICE_TYPE_ADD_USER);
				body="{\"id\":"+"\""+user.getId()+"\"}";
				message=Constants.API_MESSAGE_SUCCESS;
				code =ResponseCode.API_CODE_CALL_SUCCESS;
			}else{
				//用户名已存在
				message=Constants.API_MESSAGE_USERNAME_EXIST;
				code=ResponseCode.API_CODE_USERNAME_EXIST;
			}
		}
		ApiResponse apiResponse=new ApiResponse(request, body, message,code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}


	/**
	 *
	 * @Title: validateSmsSubmit
	 * @Description: 校验SMS
	 * @param: @param username
	 * @param: @param email
	 * @param: @param loginPassword
	 * @param: @param captcha
	 * @param: @param site
	 * @param: @param request
	 * @param: @param response
	 * @param: @return
	 * @return: WebErrors
	 */
	private WebErrors validateSmsSubmit(Integer type, String mobile,String username, String loginPassword, String smsCode, WebErrors errors,
										CmsSite site, HttpServletRequest request, HttpServletResponse response) {
		MemberConfig mcfg = site.getConfig().getMemberConfig();

		ValidationUtil.validateSmsCode(type, smsCode, errors, request, response, session);

		if (errors.ifOutOfLength(username, MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100, true)) {
			return errors;
		}
		if (errors.ifNotUsername(username, MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100, true)) {
			return errors;
		}
		if (errors.ifOutOfLength(loginPassword, MessageResolver.getMessage(request, "field.password"),
				mcfg.getPasswordMinLen(), 100, true)) {
			return errors;
		}
		// 保留字检查不通过，返回false。
		if (!mcfg.checkUsernameReserved(username)) {
			errors.addErrorCode("error.usernameReserved");
			return errors;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			errors.addErrorCode("error.usernameExist");
			return errors;
		}
		// 手机号存在，返回false。
		if (userExtManager.countByPhone(mobile) != 0) {
			errors.addErrorCode("error.mobilePhoneExist");
			return errors;
		}
		return errors;
	}

	private WebErrors validateSubmit(String username, String loginPassword, String captcha, CmsSite site,
									 HttpServletRequest request, HttpServletResponse response) {
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		WebErrors errors = WebErrors.create(request);
		try {
			if (!imageCaptchaService.validateResponseForID(session.getSessionId(request, response), captcha)) {
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			return errors;
		}
		if (errors.ifOutOfLength(username, MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100, true)) {
			return errors;
		}
		if (errors.ifNotUsername(username, MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100, true)) {
			return errors;
		}
		if (errors.ifOutOfLength(loginPassword, MessageResolver.getMessage(request, "field.password"),
				mcfg.getPasswordMinLen(), 100, true)) {
			return errors;
		}

		// 保留字检查不通过，返回false。
		if (!mcfg.checkUsernameReserved(username)) {
			errors.addErrorCode("error.usernameReserved");
			return errors;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			errors.addErrorCode("error.usernameExist");
			return errors;
		}
		return errors;
	}

	private String thirdLoginGetSessionKey(ApiAccount apiAccount,
			String thirdKey,String username,String source,
			HttpServletRequest request,HttpServletResponse response){
		String aesKey=apiAccount.getAesKey();
		thirdKey=pwdEncoder.encodePassword(thirdKey);
		CmsThirdAccount thirdAccount=thirdAccountMng.findByKey(thirdKey);
		if(thirdAccount!=null){
			username=thirdAccount.getUsername();
		}else{
			//用户不存在,则新建用户
			//若是没有传递用户名则随机用户
			if(StringUtils.isBlank(username)){
				username=getRandomUsername();
			}else{
				//若是传递的用户名存在则随机
				if(userExist(username)){
					username=getRandomUsername();
				}
			}
			CmsUserExt userExt=new CmsUserExt();
			//第三方授权来自微信小程序
			if(source.equals(Constants.THIRD_SOURCE_WEIXIN_APP)){
				String nickName =request.getParameter("nickName");
				String avatarUrl =request.getParameter("avatarUrl");
				String gender =request.getParameter("gender");
				String province =request.getParameter("province");
				String city =request.getParameter("city");
				String country =request.getParameter("country");
				if(StringUtils.isNotBlank(gender)){
					if(gender.equals(2)){
						userExt.setGender(false);
					}else if(gender.equals(1)){
						userExt.setGender(true);
					}else{
						userExt.setGender(null);
					}
				}
				if(StringUtils.isNotBlank(nickName)){
					userExt.setRealname(nickName);
				}
				String comefrom="";
				if(StringUtils.isNotBlank(country)){
					comefrom+=country;
				}
				if(StringUtils.isNotBlank(province)){
					comefrom+=province;
				}
				if(StringUtils.isNotBlank(city)){
					comefrom+=city;
				}
				userExt.setComefrom(comefrom);
				String imageUrl="";
				if(StringUtils.isNotBlank(avatarUrl)){
					CmsConfig config=configMng.get();
					CmsSite site=CmsUtils.getSite(request);
					Ftp ftp=site.getUploadFtp();
					imageUrl=imgSvc.crawlImg(avatarUrl, config.getContextPath(), 
							config.getUploadToDb(), config.getDbFileUri(), 
							ftp,site.getUploadOss(), site.getUploadPath());
				}
				userExt.setUserImg(imageUrl);
			}
			String ip = RequestUtils.getIpAddr(request);
			boolean disabled=false;
			CmsSite site = CmsUtils.getSite(request);
			CmsConfig config = site.getConfig();
			if(config.getMemberConfig().isCheckOn()){
				disabled=true;
			}
			CmsUser user=null;
			user=cmsUserMng.registerMember(username, null, thirdKey, ip, null,null,disabled,userExt,null);
			if(user!=null){
				//解决会话固定漏洞
				LoginUtils.logout();
				cmsWebserviceMng.callWebService("false",username, thirdKey, null, userExt,CmsWebservice.SERVICE_TYPE_ADD_USER);
				//绑定新建的用户
				thirdAccount=new CmsThirdAccount();
				thirdAccount.setUsername(username);
				thirdAccount.setAccountKey(thirdKey);
				thirdAccount.setSource(source);
				thirdAccount.setUser(user);
				thirdAccountMng.save(thirdAccount);
				LoginUtils.loginShiro(request, response, username);
				CmsUtils.setUser(request, user);
			}
			
		}
		String sessionKey=session.getSessionId(request, response);
		apiUserLoginMng.userLogin(username, apiAccount.getAppId(), sessionKey,request,response);
		JSONObject json=new JSONObject();
		try {
			//加密返回
			json.put("sessionKey", AES128Util.encrypt(sessionKey, aesKey,apiAccount.getIvKey()));
			json.put("username",username);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return json.toString();
	}
	
	private  String getRandomUsername(){
		SimpleDateFormat fomat=new SimpleDateFormat("yyyyMMddHHmmss");
		String username=fomat.format(Calendar.getInstance().getTime())+RandomStringUtils.random(5,Num62.N10_CHARS);;
		if (userExist(username)) {
			return getRandomUsername();
		}else{
			return username;
		}
	}
	
	private  boolean userExist(String username){
		if (unifiedUserMng.usernameExist(username)) {
			return true;
		}else{
			return false;
		}
	}
	
	private void initWeiXinJsCode2SessionUrl(){
		if(getWeiXinJsCode2SessionUrl()==null){
			setWeiXinJsCode2SessionUrl(PropertyUtils.getPropertyValue(
					new File(realPathResolver.get(com.jeecms.cms.Constants.JEECMS_CONFIG)),WEIXIN_JSCODE_2_SESSION_URL));
		}
	}
	
	private String weiXinJsCode2SessionUrl;
	
	public String getWeiXinJsCode2SessionUrl() {
		return weiXinJsCode2SessionUrl;
	}

	public void setWeiXinJsCode2SessionUrl(String weiXinJsCode2SessionUrl) {
		this.weiXinJsCode2SessionUrl = weiXinJsCode2SessionUrl;
	}

	@Autowired
	private ApiRecordMng apiRecordMng;
	@Autowired
	private ApiAccountMng apiAccountMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ApiUserLoginMng apiUserLoginMng;
	@Autowired
	private CmsWebserviceMng cmsWebserviceMng;
	@Autowired
	private CmsThirdAccountMng thirdAccountMng;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserExtMng userExtManager;
	@Autowired
	private CmsUserIdcardMng userIdcardMng;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private ImageSvc imgSvc;
	@Autowired
	private CmsConfigMng configMng;
	@Autowired
	private PwdEncoder pwdEncoder;
	@Autowired
	private RealPathResolver realPathResolver;
}

