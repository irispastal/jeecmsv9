package com.jeecms.cms.action.front;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentAttachment;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.manager.main.ContentCountMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.security.encoder.PwdEncoder;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

@Controller
public class AttachmentAct {
	private static final Logger log = LoggerFactory.getLogger(AttachmentAct.class);

	@RequestMapping(value = "/attachment.jspx", method = RequestMethod.GET)
	public void attachment(Integer cid, Integer i, Long t, String k, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws IOException {
		if (cid == null) {
			ResponseUtils.renderText(response, "downlaod error!");
		}
		CmsConfig config = CmsUtils.getSite(request).getConfig();
		String code = config.getDownloadCode();
		int h = config.getDownloadTime() * 60 * 60 * 1000;
		if (pwdEncoder.isPasswordValid(k, cid + ";" + i + ";" + t, code)) {
			long curr = System.currentTimeMillis();
			if (t + h > curr) {
				Content c = contentMng.findById(cid);
				if (c != null) {
					List<ContentAttachment> list = c.getAttachments();
					if (list.size() > i) {
						contentCountMng.downloadCount(c.getId());
						ContentAttachment ca = list.get(i);
						response.sendRedirect(ca.getPath());
						return;
					} else {
						log.info("download index is out of range: {}", i);
					}
				} else {
					log.info("Content not found: {}", cid);
				}
			} else {
				log.info("download time is expired!");
			}
		} else {
			log.info("download key error!");
		}
		ResponseUtils.renderText(response, "downlaod error!");
	}

	/**
	 * 获得下载key和time
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/attachment_url.jspx", method = RequestMethod.GET)
	public void url(Integer cid, Integer n, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if (cid == null || n == null) {
			return;
		}
		CmsConfig config = CmsUtils.getSite(request).getConfig();
		String code = config.getDownloadCode();
		long t = System.currentTimeMillis();
		JSONArray arr = new JSONArray();
		String key;
		for (int i = 0; i < n; i++) {
			key = pwdEncoder.encodePassword(cid + ";" + i + ";" + t, code);
			arr.put("&t=" + t + "&k=" + key);
		}
		ResponseUtils.renderText(response, arr.toString());
	}

	@RequestMapping(value = "/downdoc.jspx", method = RequestMethod.GET)
	public String down(Integer cid, HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		WebErrors errors = WebErrors.create(request);
		if (cid == null) {
			errors.addErrorCode("error.required", "cid");
			return FrontUtils.showError(request, response, model, errors);
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		ContentDoc doc = contentMng.findById(cid).getContentDoc();
		if (doc == null) {
			errors.addErrorCode("error.hasNotFundDoc");
			return FrontUtils.showError(request, response, model, errors);
		} else {
			boolean hasNeedGrain = checkUserGrain(user, doc.getDownNeed());
			if (hasNeedGrain) {
				CmsUser downUser = CmsUtils.getUser(request);
				contentDocMng.operateDocGrain(downUser, doc);
				downLoadFile(request, response, doc);
			} else {
				errors.addErrorCode("error.hasNoEnoughGrain", doc.getDownNeed());
				return FrontUtils.showError(request, response, model, errors);
			}
			return null;
		}
	}

	private boolean checkUserGrain(CmsUser user, Integer downNeed) {
		if (user.getGrain() >= downNeed) {
			return true;
		} else {
			return false;
		}
	}

	private void downLoadFile(HttpServletRequest request, HttpServletResponse response, ContentDoc doc)
			throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		// 解决中文乱码
		String filename = doc.getContent().getTitle() + "." + doc.getFileSuffix();
		// 文件相对路径
		String path = doc.getDocPath();
		String ctx = site.getContextPath();
		if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(ctx)) {
			path = path.substring(ctx.length());
		}
		// 文件绝对路径
		String fileRealPath = realPathResolver.get(path);
		File file = new File(fileRealPath);
		if (file.exists()) {
			byte[] byteArray = FileUtils.readFileToByteArray(file);
			try {
				filename = new String(filename.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
			}
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			ServletOutputStream out = response.getOutputStream();
			out.write(byteArray);
			out.close();
		} else {
			ResponseUtils.renderJson(response, WebErrors.create(request).getMessage("content.doc.hasDelete"));
		}
	}
	
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentCountMng contentCountMng;
	@Autowired
	private PwdEncoder pwdEncoder;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private ContentDocMng contentDocMng;

}
