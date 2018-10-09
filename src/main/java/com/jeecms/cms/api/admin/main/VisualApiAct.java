package com.jeecms.cms.api.admin.main;

import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.common.office.FileUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
@Controller
public class VisualApiAct {
	//模板文件名
	private final String fileName = "\\tagTemplate.html";
	//模板存放的路径
	private final String foldPath = "\\WEB-INF\\common";
	
	//拷贝的新的文件存放目录
	private final String copyFoldPath = "\\WEB-INF\\common\\visual";
	
	
	@RequestMapping(value = "/visual/save", method = RequestMethod.POST)
	public void reply_view(String templateStr, HttpServletRequest request,
			HttpServletResponse response, ModelMap model){
		   String message = Constants.API_MESSAGE_SUCCESS;
		   String code = ResponseCode.API_CODE_CALL_SUCCESS;
		   String body = "\"\"";
		   try {
			    templateStr = URLDecoder.decode(templateStr,"UTF-8");
			    String realPath = FileUtils.getFileRealPaht(foldPath, fileName, request);
			    FileUtils.writeFile(realPath, FileUtils.dealHTMLContent(realPath,".replace",templateStr));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message = Constants.API_STATUS_FAIL;
				code = ResponseCode.API_CODE_CALL_FAIL;
			}  
			FrontUtils.frontData(request, model, CmsUtils.getSite(request));
			ApiResponse apiResponse = new ApiResponse(request, body, message, code);
			ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	/**
	 * 
	 * @Title: copy_file 指定目录创建对应专题可视化文件，内容复制指定模板
	 * @Description: TODO
	 * @param: @param specialId 专题相关特定标识
	 * @param: @param request
	 * @param: @param response
	 * @param: @param model      
	 * @return: void
	 */
	@RequestMapping(value = "/visual/saveCopyFile", method = RequestMethod.POST)
	public void copy_file(String specialId,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String message = Constants.API_MESSAGE_SUCCESS;
	    String code = ResponseCode.API_CODE_CALL_SUCCESS;
	    String body = "\"\"";
	    try {
	    	//文件名
	    	String fileNewName="/"+System.currentTimeMillis()+specialId+".html";
	    	//指定目录创建文件
	    	FileUtils.createFile(copyFoldPath, fileNewName, request);
//		    //获取指定模板的内容
		    String realPath = FileUtils.getFileRealPaht(foldPath, fileName, request);
//		    //获取新生成文件的真实路径
		    String path = FileUtils.getFileRealPaht(copyFoldPath, fileNewName, request);
		    FileUtils.writeFile(path, FileUtils.dealHTMLContent(realPath));
		 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 message = Constants.API_STATUS_FAIL;
			 code = ResponseCode.API_CODE_CALL_FAIL;
		 }  
		 FrontUtils.frontData(request, model, CmsUtils.getSite(request));
		 ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		 ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
}
