package com.jeecms.cms.action.member;

import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.entity.assist.CmsWebservice;
import com.jeecms.cms.manager.assist.CmsWebserviceMng;
import com.jeecms.cms.web.Token;
import com.jeecms.common.email.EmailSender;
import com.jeecms.common.email.MessageTemplate;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsConfigItem;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUserExt;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.entity.UnifiedUser;
import com.jeecms.core.manager.CmsConfigItemMng;
import com.jeecms.core.manager.CmsUserExtMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.ConfigMng;
import com.jeecms.core.manager.UnifiedUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * 前台会员注册Action
 */
@Controller
public class RegisterAct {
	private static final Logger log = LoggerFactory.getLogger(RegisterAct.class);

	public static final String REGISTER = "tpl.register";
	public static final String REGISTER_RESULT = "tpl.registerResult";
	public static final String REGISTER_ACTIVE_SUCCESS = "tpl.registerActiveSuccess";
	public static final String LOGIN_INPUT = "tpl.loginInput";

	@Token(save = true)
	@RequestMapping(value = "/register.jspx", method = RequestMethod.GET)
	public String input(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		// 没有开启会员注册
		if (!mcfg.isRegisterOn()) {
			return FrontUtils.showMessage(request, model, "member.registerClose");
		}
		List<CmsConfigItem> items = cmsConfigItemMng.getList(site.getConfig().getId(), CmsConfigItem.CATEGORY_REGISTER);
		FrontUtils.frontData(request, model, site);
		CmsConfig config = site.getConfig();
		Integer validateType = 0;
		if(config.getValidateType() != null) {
			validateType = config.getValidateType();			
		}
		model.addAttribute("mcfg", mcfg);
		model.addAttribute("items", items);
		model.addAttribute("validateType", validateType);//验证类型  0:无 1：邮件验证    2： SMS短信验证
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_MEMBER, REGISTER);
	}

	@Token(remove = true)
	@RequestMapping(value = "/register.jspx", method = RequestMethod.POST)
	public String submit(String username, String email, String loginPassword, CmsUserExt userExt, String captcha,
			String nextUrl, String smsCode,HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		WebErrors errors = WebErrors.create(request);
		if (config.getValidateType() == 2) {// 判断是否开启的是SMS验证
			errors = validateSmsSubmit(userExt.getMobile(),username, loginPassword, captcha, site, request, response);
		} else {
			errors = validateSubmit(username, email, loginPassword, captcha, site, request, response);
		}
		boolean disabled = false;
		if (config.getMemberConfig().isCheckOn()) {
			disabled = true;
		}
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		String ip = RequestUtils.getIpAddr(request);
		Map<String, String> attrs = RequestUtils.getRequestMap(request, "attr_");
		if (config.getValidateType() == 1) {// 邮件验证
			EmailSender sender = configMng.getEmailSender();
			MessageTemplate msgTpl = configMng.getRegisterMessageTemplate();
			if (sender == null) {
				// 邮件服务器没有设置好
				model.addAttribute("status", 4);
			} else if (msgTpl == null) {
				// 邮件模板没有设置好
				model.addAttribute("status", 5);
			} else {
				try {
					cmsUserMng.registerMember(username, email, loginPassword, ip, null, disabled, userExt, attrs, false,
							sender, msgTpl);
					cmsWebserviceMng.callWebService("false", username, loginPassword, email, userExt,
							CmsWebservice.SERVICE_TYPE_ADD_USER);
					model.addAttribute("status", 0);
				} catch (UnsupportedEncodingException e) {
					// 发送邮件异常
					model.addAttribute("status", 100);
					model.addAttribute("message", e.getMessage());
					log.error("send email exception.", e);
				} catch (MessagingException e) {
					// 发送邮件异常
					model.addAttribute("status", 101);
					model.addAttribute("message", e.getMessage());
					log.error("send email exception.", e);
				}
			}
			log.info("member register success. username={}", username);
			FrontUtils.frontData(request, model, site);
			if (!StringUtils.isBlank(nextUrl)) {
				response.sendRedirect(nextUrl);
				return null;
			} else {
				return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_MEMBER, REGISTER_RESULT);
			}
		} else {
//			mobilePhone
//			userExtManager
			cmsUserMng.registerMember(username, email, loginPassword, ip, null, null, disabled, userExt, attrs);
			cmsWebserviceMng.callWebService("false", username, loginPassword, email, userExt,
					CmsWebservice.SERVICE_TYPE_ADD_USER);
			log.info("member register success. username={}", username);
			FrontUtils.frontData(request, model, site);
			FrontUtils.frontPageData(request, model);
			model.addAttribute("success", true);
			return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_MEMBER, LOGIN_INPUT);
		}
	}

	@RequestMapping(value = "/active.jspx", method = RequestMethod.GET)
	public String active(String username, String key, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		username = new String(request.getParameter("username").getBytes("iso8859-1"), "GBK");
		WebErrors errors = validateActive(username, key, request, response);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		unifiedUserMng.active(username, key);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_MEMBER, REGISTER_ACTIVE_SUCCESS);
	}

	@RequestMapping(value = "/username_unique.jspx")
	public void usernameUnique(HttpServletRequest request, HttpServletResponse response) {
		String username = RequestUtils.getQueryParam(request, "username");
		// 用户名为空，返回false。
		if (StringUtils.isBlank(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		// 保留字检查不通过，返回false。
		if (!config.getMemberConfig().checkUsernameReserved(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		ResponseUtils.renderJson(response, "true");
	}
	
	/**
	 * 
	 * @Title: emailUnique   
	 * @Description: 判断手机号是否被注册
	 * @param:  request
	 * @param:  response      
	 * @return: void
	 */
	@RequestMapping(value = "/mobilePhone_unique.jspx")
	public void mobilePhoneUnique(HttpServletRequest request, HttpServletResponse response) {
		String mobilePhone = RequestUtils.getQueryParam(request, "mobile");
		// mobilePhone为空，返回false。
		if (StringUtils.isBlank(mobilePhone)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// mobilePhone存在，返回false。
		int countByPhone = userExtManager.countByPhone(mobilePhone);
		if (countByPhone != 0) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		ResponseUtils.renderJson(response, "true");
	}

	@RequestMapping(value = "/email_unique.jspx")
	public void emailUnique(HttpServletRequest request, HttpServletResponse response) {
		String email = RequestUtils.getQueryParam(request, "email");
		// email为空，返回false。
		if (StringUtils.isBlank(email)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// email存在，返回false。
		if (unifiedUserMng.emailExist(email)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		ResponseUtils.renderJson(response, "true");
	}

	private WebErrors validateSubmit(String username, String email, String loginPassword, String captcha, CmsSite site,
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
			log.warn("", e);
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
		if (errors.ifNotEmail(email, MessageResolver.getMessage(request, "field.email"), 100, true)) {
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
	private WebErrors validateSmsSubmit(String mobile,String username, String loginPassword, String captcha,
			CmsSite site, HttpServletRequest request, HttpServletResponse response) {
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		WebErrors errors = WebErrors.create(request);
		Serializable autoCodeTime = session.getAttribute(request, "AUTO_CODE_CREAT_TIME");// 验证码有效时间
		Serializable autoCode = session.getAttribute(request, "AUTO_CODE");// 验证码值
		// 判断验证码是否在有效时间范围
		if (autoCodeTime != null && autoCode != null) {
			Long effectiveTime = Long.parseLong(autoCodeTime.toString());
			if (effectiveTime > new Date().getTime()) {
				// 验证码验证码是否正确
				if (captcha.equals(autoCode.toString())) {
					session.setAttribute(request, response, "AUTO_CODE_CREAT_TIME", null);					
				} else {
					// 验证码不正确
					errors.addErrorCode("error.invalidCaptcha");
				}
			} else {
				// 验证码失效
				errors.addErrorCode("error.invalidCaptcha");//loseEfficacyCaptcha
			}
		} else {
			// 验证码错误
			errors.addErrorCode("error.invalidCaptcha");
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
		// 手机号存在，返回false。
		if (userExtManager.countByPhone(mobile) != 0) {
			errors.addErrorCode("error.mobilePhoneExist");
			return errors;
		}
		return errors;
	}

	private WebErrors validateActive(String username, String activationCode, HttpServletRequest request,
			HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		if (StringUtils.isBlank(username) || StringUtils.isBlank(activationCode)) {
			errors.addErrorCode("error.exceptionParams");
			return errors;
		}
		UnifiedUser user = unifiedUserMng.getByUsername(username);
		if (user == null) {
			errors.addErrorCode("error.usernameNotExist");
			return errors;
		}
		/*
		 * firefox访问链接二次访问，现简单不验证 if (user.getActivation() ||
		 * StringUtils.isBlank(user.getActivationCode())) {
		 * errors.addErrorCode("error.usernameActivated"); return errors; } if
		 * (!user.getActivationCode().equals(activationCode)) {
		 * errors.addErrorCode("error.exceptionActivationCode"); return errors; }
		 */
		if (StringUtils.isNotBlank(user.getActivationCode()) && !user.getActivationCode().equals(activationCode)) {
			errors.addErrorCode("error.exceptionActivationCode");
			return errors;
		}
		return errors;
	}

	@Autowired
	private CmsWebserviceMng cmsWebserviceMng;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private ConfigMng configMng;
	@Autowired
	private CmsConfigItemMng cmsConfigItemMng;
	@Autowired
	private CmsUserExtMng userExtManager;
}
