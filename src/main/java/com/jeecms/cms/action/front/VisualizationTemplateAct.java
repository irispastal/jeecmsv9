package com.jeecms.cms.action.front;

import static com.jeecms.cms.Constants.TPLDIR_SPECIAL;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
@Controller
public class VisualizationTemplateAct {
	
	@RequestMapping(value = "/generateTagTemplate.jspx", method = RequestMethod.GET)
	public String reply_view(String templateStr, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws UnsupportedEncodingException {
			FrontUtils.frontData(request, model, CmsUtils.getSite(request));
			return FrontUtils.getTplPath(request, "/WEB-INF/common",
					"","tpl.tagTemplate");
	}
}
