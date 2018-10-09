package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.ContentShareCheckDao;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentShareCheck;
import com.jeecms.cms.entity.main.ContentRecord.ContentOperateType;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.ContentRecordMng;
import com.jeecms.cms.manager.main.ContentShareCheckMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;

@Service
@Transactional
public class ContentShareCheckMngImpl implements ContentShareCheckMng {

	public ContentShareCheck findById(Integer id) {
		return dao.findById(id);
	}

	public List<ContentShareCheck> getList(Integer contentId, Integer channelId) {
		return dao.getList(contentId, channelId);
	}

	public Pagination getPageForShared(String title, Byte status,
			Integer siteId, Integer targetSiteId, Integer requestSiteId, int pageNo, int pageSize) {
		return dao.getPageForShared(title, status, siteId, targetSiteId,requestSiteId,
				pageNo, pageSize);
	}

	public ContentShareCheck save(ContentShareCheck check) {
		dao.save(check);
		return check;
	}

	public ContentShareCheck save(ContentShareCheck bean, Content content,
			Channel channel,CmsUser user) {
		bean.init();
		bean.setContent(content);
		bean.setChannel(channel);
		dao.save(bean);
		content.setContentShareCheck(bean);
		contentRecordMng.record(content, user, ContentOperateType.shared);
		return bean;
	}

	public ContentShareCheck update(ContentShareCheck bean) {
		if(bean.getContent()!=null&&bean.getChannel()!=null){
			if(bean.getShareValid()!=null&&bean.getShareValid()){
				contentMng.updateByChannelIds(bean.getContent().getId()
						, new Integer[]{bean.getChannel().getId()}, 
						Content.CONTENT_CHANNEL_ADD);
			}else{
				contentMng.updateByChannelIds(bean.getContent().getId()
						, new Integer[]{bean.getChannel().getId()}, 
						Content.CONTENT_CHANNEL_DEL);
			}
		}
		bean=dao.update(bean);
		return bean;
	}

	public ContentShareCheck deleteById(Integer id) {
		ContentShareCheck share= dao.findById(id);
		if(share!=null){
			if(share.getContent()!=null&&share.getChannel()!=null){
				contentMng.updateByChannelIds(share.getContent().getId()
						, new Integer[]{share.getChannel().getId()}, 
						Content.CONTENT_CHANNEL_DEL);
			}
		}
		dao.deleteById(id);
		return share;
	}

	public ContentShareCheck[] deleteByIds(Integer[] ids) {
		if(ids!=null&&ids.length>0){
			ContentShareCheck[] checks=new ContentShareCheck[ids.length];
			for(Integer i=0;i<ids.length;i++){
				checks[i]=deleteById(ids[i]);
			}
			return checks;
		}else{
			return null;
		}
	}

	private ContentShareCheckDao dao;
	
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentRecordMng contentRecordMng;

	@Autowired
	public void setDao(ContentShareCheckDao dao) {
		this.dao = dao;
	}

}