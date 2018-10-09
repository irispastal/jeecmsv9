package com.jeecms.cms.manager.main.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.ContentDocDao;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.manager.main.ContentCountMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.office.FileUtils;
import com.jeecms.common.office.OpenOfficeConverter;
import com.jeecms.common.office.PdfToSwfConverter;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;

@Service
@Transactional
public class ContentDocMngImpl implements ContentDocMng {
	public ContentDoc save(ContentDoc doc, Content content) {
		if (StringUtils.isBlank(doc.getDocPath())) {
			return null;
		} else {
			String[] fileSuffixs=doc.getDocPath().split("\\.");
			String fileSuffix=fileSuffixs[fileSuffixs.length-1];
			doc.setFileSuffix(fileSuffix);
			doc.setContent(content);
			doc.init();
			dao.save(doc);
			content.setContentDoc(doc);
			return doc;
		}
	}

	public ContentDoc update(ContentDoc doc, Content content) {
		ContentDoc entity = dao.findById(content.getId());
		if (entity == null) {
			entity = save(doc, content);
			content.getContentDocSet().add(entity);
			return entity;
		} else {
			if (StringUtils.isBlank(doc.getDocPath())) {
				content.getContentDocSet().clear();
				return null;
			} else {
				String[] fileSuffixs=doc.getDocPath().split("\\.");
				String fileSuffix=fileSuffixs[fileSuffixs.length-1];
				doc.setFileSuffix(fileSuffix);
				Updater<ContentDoc> updater = new Updater<ContentDoc>(doc);
				entity = dao.updateByUpdater(updater);
				return entity;
			}
		}
	}
	
	public ContentDoc operateDocGrain(CmsUser downUser, ContentDoc doc) {
		downUser.setGrain(downUser.getGrain() - doc.getDownNeed());
		userMng.updateUser(downUser);
		CmsUser owner = doc.getContent().getUser();
		owner.setGrain(doc.getDownNeed() + owner.getGrain());
		userMng.updateUser(owner);
		doc.setGrain(doc.getGrain() + doc.getDownNeed());
		contentCountMng.downloadCount(doc.getId());
		update(doc,doc.getContent());
		return doc;
	}
	
	public ContentDoc createSwfFile(ContentDoc doc){
			CmsSite site=doc.getContent().getSite();
			CmsConfig config=site.getConfig();
			String swfhome=config.getSwftoolsHome();
			String ctx=config.getContextPath();
			String path = doc.getDocPath();
			//swf文件放在和文档目录同级目录下
			String swfPath=FileUtils.getFilePath(path);
			if(StringUtils.isNotBlank(ctx)&&path.indexOf(ctx)!=-1){
				path = path.split(ctx)[1];
			}
			//获取文件真实路径
			String fileRealPath = realPathResolver.get(path);
			String fileName=FileUtils.getFileName(path);
			String outPdfRealPath = realPathResolver.get(FileUtils.getFilePath(path));
			File pdfFile ;
			//pdf文件
			boolean isPDF=false;
			if(fileRealPath.endsWith(OpenOfficeConverter.PDF)){
				isPDF=true;
				pdfFile=new File(fileRealPath);
			}else{
				//转换文档成pdf
				pdfFile= openOfficeConverter.convertToPdf(fileRealPath,outPdfRealPath + "/",fileName);
			}
			//获取要转换的swf真实路径
			String outSwfRealPath = realPathResolver.get(FileUtils.getFilePath(path))+ "/";
			//swf文件名称和转换的pdf文件名相同(%分页分割swf文件生成)
			String swfFileName=fileName+"_%.swf";
			//转换pdf为swf
			int processStatus=1;
			try {
				processStatus = PdfToSwfConverter.convertPDF2SWF(swfhome, pdfFile.getAbsolutePath(), outSwfRealPath,swfFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//更新文档对象swf路径为swf相对路径（不带文件名后缀）
			String swfFile=swfPath+fileName;
			doc.setSwfPath(swfFile);
			//设置swf文件总数
			Integer swfNums=FileUtils.listFiles(new File(outSwfRealPath), fileName, ".swf").size();
			if(swfNums==0){
				swfNums=1;
			}
			doc.setSwfNum(swfNums);
			doc=update(doc,doc.getContent());
			//删除pdf文件
			if(processStatus==0&&!isPDF){
				pdfFile.delete();
			}
			return doc;
	}
	
	public ContentDoc createPdfFile(ContentDoc doc){
		CmsSite site=doc.getContent().getSite();
		CmsConfig config=site.getConfig();
		String ctx=config.getContextPath();
		String path = doc.getDocPath();
		if(StringUtils.isNotBlank(ctx)&&path.startsWith(ctx)){
			path = path.split(ctx)[1];
		}
		//获取文件真实路径
		String fileRealPath = realPathResolver.get(path);
		String fileName=FileUtils.getFileName(path);
		String outPdfRealPath = realPathResolver.get(FileUtils.getFilePath(path));
		String pdfPath=FileUtils.getFilePath(path)+fileName+".pdf";
		if(!fileRealPath.endsWith(OpenOfficeConverter.PDF)){
			//转换文档成pdf
			openOfficeConverter.convertToPdf(fileRealPath,outPdfRealPath + "/",fileName);
		}
		if(StringUtils.isNotBlank(ctx)){
			pdfPath=ctx+pdfPath;
		}
		doc.setPdfPath(pdfPath);
		doc=update(doc,doc.getContent());
		return doc;
	}

	private ContentDocDao dao;
	@Autowired
	private OpenOfficeConverter openOfficeConverter;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private CmsUserMng userMng;
	@Autowired
	private ContentCountMng contentCountMng;

	@Autowired
	public void setDao(ContentDocDao dao) {
		this.dao = dao;
	}
}