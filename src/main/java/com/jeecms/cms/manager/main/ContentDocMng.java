package com.jeecms.cms.manager.main;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.core.entity.CmsUser;
public interface ContentDocMng {
	public ContentDoc save(ContentDoc doc, Content content);

	public ContentDoc update(ContentDoc doc, Content content);
	
	public ContentDoc operateDocGrain(CmsUser downUser, ContentDoc doc);
	
	public ContentDoc createSwfFile(ContentDoc doc);
	
	public ContentDoc createPdfFile(ContentDoc doc);
}