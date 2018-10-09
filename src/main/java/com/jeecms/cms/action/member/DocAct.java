package com.jeecms.cms.action.member;

import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jeecms.cms.Constants;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.common.upload.FileRepository;
import com.jeecms.core.entity.CmsGroup;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

/**
 * 会员文库Action
 * 
 * @author tom
 * 
 */
@Controller
public class DocAct  extends AbstractContentMemberAct{
	private static final Logger log = LoggerFactory.getLogger(DocAct.class);

	private static final String DOC_LIST = "tpl.docList";
	private static final String DOC_ADD = "tpl.docAdd";
	private static final String DOC_EDIT = "tpl.docEdit";
	private static final String DOC_UPLOAD = "tpl.docUpload";

	/**
	 * 会员文库列表
	 * 
	 * @param q
	 *            搜索词汇
	 * @param typeId
	 *            所属分类
	 * @param pageNo
	 *            页码
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_list.jspx")
	public String list(String q, Integer modelId,Integer queryChannelId,Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		return super.list(q, modelId, queryChannelId, DOC_LIST, pageNo, request, model);
	}

	/**
	 * 会员文库添加
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_add.jspx")
	public String add(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		boolean hasPermission = checkUserUploadTotal(user.getGroup(), user.getFileTotal());
		return super.add(hasPermission, DOC_ADD, request, response, model);
	}

	/**
	 * 会员文库保存
	 * 
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_save.jspx")
	public String save(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer modelId,
			ContentDoc doc,String captcha,Short charge,Double chargeAmount,
			Boolean rewardPattern,Double rewardRandomMin,
			Double rewardRandomMax,Double[] rewardFix,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		updateUserUploadDocNum(request, 1);
		return super.save(title, author, description, txt, tagStr, 
				channelId,modelId, doc, captcha, 
				null,null,null,null,null,null,null,
				charge,chargeAmount,
				rewardPattern, rewardRandomMin,	rewardRandomMax,rewardFix,
				nextUrl,null,request, response, model);
	}

	/**
	 * 会员文库修改
	 * 
	 * @param id
	 *            文章ID
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_edit.jspx")
	public String edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.edit(id, DOC_EDIT, request, response, model);
	}

	/**
	 * 会有文库更新
	 * 
	 * @param id
	 *            文库ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_update.jspx")
	public String update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer channelId,
			ContentDoc doc,Short charge,Double chargeAmount,
			Boolean rewardPattern,Double rewardRandomMin,
			Double rewardRandomMax,Double[] rewardFix,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.update(id, title, author, description, txt, tagStr,
				channelId,null,null,null,null,null,null,null,
				doc, charge,chargeAmount,
				rewardPattern, rewardRandomMin,	rewardRandomMax,rewardFix,
				nextUrl,null, request, response, model);
	}

	/**
	 * 会员文库删除
	 * 
	 * @param ids
	 *            待删除的文章ID数组
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/doc_delete.jspx")
	public String delete(Integer[] ids, HttpServletRequest request,
			String nextUrl, HttpServletResponse response, ModelMap model) {
		updateUserUploadDocNum(request, -1);
		return super.delete(ids, request, nextUrl, response, model);
	}

	@RequestMapping("/member/doc_upload.jspx")
	public String uploadDoc(
			@RequestParam(value = "doc", required = false) MultipartFile file,
			String docNum, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		WebErrors errors = validateUpload(file, request);
		CmsUser user =CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, DOC_UPLOAD);
		}
		errors = validateUploadFileSize(file, CmsUtils.getUser(request)
				.getGroup().getAllowFileSize(), request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return FrontUtils.showError(request, response, model, errors);
		}
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			String ctx = request.getContextPath();
			fileUrl = fileRepository.storeByExt(site.getLibraryPath(), ext,
					file);
			// 加上部署路径
			fileUrl = ctx + fileUrl;
			model.addAttribute("docPath", fileUrl);
			model.addAttribute("docName", origName);
			model.addAttribute("docNum", docNum);
			model.addAttribute("docExt", ext);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, DOC_UPLOAD);
	}
	
	

	private boolean checkUserUploadTotal(CmsGroup group, Integer number) {
		if (group.getAllowFileTotal().equals(0)) {
			return true;
		} else {
			if (number >= group.getAllowFileTotal()) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	private void updateUserUploadDocNum(HttpServletRequest request,Integer num){
		CmsUser user=CmsUtils.getUser(request);
		user.setFileTotal(user.getFileTotal()+num);
		cmsUserMng.updateUser(user);
	}

	private WebErrors validateUpload(MultipartFile file,
			HttpServletRequest request) {
		int fileSize = (int) (file.getSize() / 1024);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(file, "file", true)) {
			return errors;
		}
		CmsUser user =CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		if(!Constants.LIBRARY_SUFFIX.contains(ext)){
			errors.addErrorCode("error.uploadValidFile",ext);
		}
		//非允许的后缀
		if(!user.isAllowSuffix(ext)){
			errors.addErrorCode("upload.error.invalidsuffix", ext);
			return errors;
		}
		//超过附件大小限制
		if(!user.isAllowMaxFile((int)(file.getSize()/1024))){
			errors.addErrorCode("upload.error.toolarge",origName,user.getGroup().getAllowMaxFile());
			return errors;
		}
		//超过每日上传限制
		if (!user.isAllowPerDay(fileSize)) {
			long laveSize=user.getGroup().getAllowPerDay()-user.getUploadSize();
			if(laveSize<0){
				laveSize=0;
			}
			errors.addErrorCode("upload.error.dailylimit", laveSize);
		}
		return errors;
	}

	private WebErrors validateUploadFileSize(MultipartFile file,
			Integer maxSize, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (!maxSize.equals(0) && file.getSize() > maxSize * 1024) {
			errors.addErrorCode("error.uploadOverSize", maxSize);
		}
		return errors;
	}
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private CmsUserMng cmsUserMng;
}
