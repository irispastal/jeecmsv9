package com.jeecms.cms.action.member;


import static com.jeecms.common.page.SimplePage.cpn;
import static com.jeecms.cms.Constants.TPLDIR_MEMBER;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.cms.manager.main.CustomFormFiledMng;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.cms.manager.main.CustomRecordMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

/**
 * 自定义表单Action
 * 
 * @author 
 * 
 */
@Controller
public class CustomFormAct {
	private static final Logger log = LoggerFactory.getLogger(CustomFormAct.class);
	
	public static final String MEMBER_CUSTOM_LIST = "tpl.customLists";
	
	/**
	 * 自定义表单记录列表
	 * @param formId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/record.jspx", method = RequestMethod.GET)
	public String List(Integer formId,Integer pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		Pagination pagination = null;
		
		if (formId!=null) {
			pagination=customRecordMng.getPage(formId,null, user.getId(), cpn(pageNo), CookieUtils.getPageSize(request));			
			List<CustomFormFiled> fileds= customFormFiledMng.getList(true,formId);	
			model.addAttribute("fileds", fileds);
		}	
		if (site!=null) {
			List<CustomForm> forms= customFormMng.getList(true,null,site.getId());
			model.addAttribute("forms", forms);
		}
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("formId", formId);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_CUSTOM_LIST);
	}
	
	@Autowired
	private CustomFormFiledMng customFormFiledMng;
	@Autowired
	private CustomRecordMng customRecordMng;
	@Autowired
	private CustomFormMng customFormMng;
}
