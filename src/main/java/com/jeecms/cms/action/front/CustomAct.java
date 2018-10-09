package com.jeecms.cms.action.front;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.cms.manager.main.CustomRecordMng;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.util.CmsUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;


@Controller
public class CustomAct {
	private static final Logger log = LoggerFactory.getLogger(CustomAct.class);
	
	@RequestMapping("/record_save.jspx")
	public void save(CustomRecord bean,Integer formId,String captcha,HttpServletResponse response,HttpServletRequest request){
		bean=new CustomRecord();
		bean.init();
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		try {
			if (!imageCaptchaService.validateResponseForID(session
					.getSessionId(request, response), captcha)) {
				json.put("success", false);
				json.put("status", 1);
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
		} catch (CaptchaServiceException e) {
			json.put("success", false);
			json.put("status", 1);
			ResponseUtils.renderJson(response, json.toString());
			log.warn("", e);
			return;
		}
		//需要用户登陆
		if (user == null) {
			json.put("success", false);
			json.put("status", 3);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (formId!=null) {
			CustomForm form= customFormMng.findById(formId);
			Date now = new Date();
			if(form.getStartTime()!=null&&form.getEndTime()!=null){
				if (form.getStartTime().getTime()>now.getTime()) {
					json.put("success", false);
					json.put("status", 4);
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				if (form.getEndTime().getTime()<now.getTime()) {
					json.put("success", false);
					json.put("status", 5);
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			}
			
			//判断本站是否可用
			if (!form.getAllSite()&&form.getSite().getId()!=site.getId()) {
				json.put("success", false);
				json.put("status", 2);
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
		}
		if (formId!=null&&user.getId()!=null) {
		  long submitNum=manager.getDaySubNum(formId, user.getId());//今日提交数
		  CustomForm form= customFormMng.findById(formId);
		  if (form.getDayLimit()!=0) {
			  if (submitNum >= (long)form.getDayLimit()) {
				  	json.put("success", false);
					json.put("status", 6);
					ResponseUtils.renderJson(response, json.toString());
					return;
			  }
		  }
		 
		}
		bean = manager.save(bean,formId,request);
		json.put("success", true);
		json.put("status", 0);
		ResponseUtils.renderJson(response, json.toString());		
		
	}
	
	
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private CustomRecordMng manager;
	@Autowired
	private CustomFormMng customFormMng;
}
