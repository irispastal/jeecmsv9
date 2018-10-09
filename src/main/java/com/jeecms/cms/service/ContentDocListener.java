package com.jeecms.cms.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.service.ContentListenerAbstract;
import com.jeecms.common.web.springmvc.RealPathResolver;

@Component
public class ContentDocListener extends ContentListenerAbstract {
	private static final Logger log = LoggerFactory.getLogger(ContentDocListener.class);
	/**
	 * 文件路径
	 */
	private static final String DOC_PATH = "docPath";
	/**
	 * 是否已审核
	 */
	private static final String IS_CHECKED = "isChecked";

	@Override
	public void afterSave(Content content) {
		if (content.isChecked()&&content.getContentDoc()!=null) {
			contentDocMng.createPdfFile(content.getContentDoc());
		}
	}

	@Override
	public Map<String, Object> preChange(Content content) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IS_CHECKED, content.isChecked());
		if(content.getContentDoc()!=null){
			map.put(DOC_PATH, content.getDocPath());
		}
		return map;
	}

	@Override
	public void afterChange(Content content, Map<String, Object> map) {
		if(content.getContentDoc()!=null){
			boolean curr = content.isChecked();
			boolean pre = (Boolean) map.get(IS_CHECKED);
			String currPath=content.getDocPath();
			String prePath = (String) map.get(DOC_PATH);
			boolean hasChanged=false;
			if(StringUtils.isNotBlank(currPath)){
				if(StringUtils.isBlank(prePath)){
					hasChanged=true;
				}else if(!prePath.equals(currPath)){
					hasChanged=true;
				}
			}
			if (pre && !curr) {
				deletePdfFile(content);
			} else if (!pre && curr) {
				contentDocMng.createPdfFile(content.getContentDoc());
			} else if (pre && curr) {
				if(hasChanged){
					contentDocMng.createPdfFile(content.getContentDoc());
				}
			}
		}
	}

	@Override
	public void afterDelete(Content content) {
		deleteDocFile(content);
	}
	
	//历史方法
	@Deprecated
	private void deleteSwfFile(Content content){
		String ctx=content.getSite().getContextPath();
		String swfPath=content.getSwfPath();
		Integer swfNum=content.getContentDoc().getSwfNum();
		if(StringUtils.isNotBlank(ctx)&&StringUtils.isNotBlank(swfPath)){
			swfPath=swfPath.substring(ctx.length());
			for(Integer i=0;i<swfNum;i++){
				File swfFile=new File(realPathResolver.get(swfPath+"_"+i+".swf"));
				swfFile.delete();
			}
		}
	}
	//删除转换后pdf文档
	private void deletePdfFile(Content content){
		String ctx=content.getSite().getContextPath();
		String pdfPath=content.getPdfPath();
		if(StringUtils.isNotBlank(ctx)&&StringUtils.isNotBlank(pdfPath)){
			pdfPath=pdfPath.substring(ctx.length());
			File pdfFile=new File(realPathResolver.get(pdfPath));
			if(pdfFile.exists()){
				pdfFile.delete();
			}
		}
	}
	//删除原文档
	private void deleteDocFile(Content content){
		String ctx=content.getSite().getContextPath();
		ContentDoc contentDoc=content.getContentDoc();
		String docPath=content.getDocPath();
		if(StringUtils.isNotBlank(docPath)&&StringUtils.isNotBlank(ctx)){
			docPath=docPath.substring(ctx.length());
		}
		if(contentDoc!=null){
			File doc=new File(realPathResolver.get(docPath));
			if(doc.exists()){
				doc.delete();
			}
			deletePdfFile(content);
			log.info("delete doc file.."+doc.getName());
		}
	}
	@Autowired
	private ContentDocMng contentDocMng;
	@Autowired
	private RealPathResolver realPathResolver;
}
