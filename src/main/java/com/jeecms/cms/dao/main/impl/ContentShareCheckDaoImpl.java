package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.ContentShareCheckDao;
import com.jeecms.cms.entity.main.ContentShareCheck;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;

@Repository
public class ContentShareCheckDaoImpl extends
		HibernateBaseDao<ContentShareCheck, Integer> implements
		ContentShareCheckDao {
	
	@SuppressWarnings("unchecked")
	public List<ContentShareCheck> getList(Integer contentId, Integer channelId) {
		String hql=" from ContentShareCheck shareCheck where 1=1 ";
		Finder finder=Finder.create(hql);
		if(channelId!=null){
			finder.append(" and shareCheck.content.id=:contentId").setParam("contentId", contentId);
		}
		if(channelId!=null){
			finder.append(" and shareCheck.channel.id=:channelId").setParam("channelId", channelId);
		}
		return find(finder);
	}
	public Pagination getPageForShared(String title, Byte status,
			Integer siteId, Integer targetSiteId,Integer requestSiteId, int pageNo, int pageSize){
		Finder f = Finder.create("from ContentShareCheck shareCheck where 1=1 ");
		if (!StringUtils.isBlank(title)) {
			f.append(" and shareCheck.content.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		if(status!=null){
			f.append(" and shareCheck.checkStatus=:status");
			f.setParam("status", status);
		}
		if(siteId!=null){
			f.append(" and shareCheck.content.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if(targetSiteId!=null){
			f.append(" and shareCheck.channel.site.id=:targetSiteId");
			f.setParam("targetSiteId", targetSiteId);
		}
		f.append(" and (shareCheck.content.site.id=:rsiteId or shareCheck.channel.site.id=:rsiteId)");
		f.setParam("rsiteId", requestSiteId);
		f.append(" order by shareCheck.id desc");
		return find(f, pageNo, pageSize);
	}
	
	public ContentShareCheck findById(Integer id) {
		ContentShareCheck entity = get(id);
		return entity;
	}

	public ContentShareCheck save(ContentShareCheck bean) {
		getSession().save(bean);
		return bean;
	}

	public ContentShareCheck update(ContentShareCheck bean) {
		getSession().update(bean);
		return bean;
	}
	public ContentShareCheck deleteById(Integer id) {
		ContentShareCheck entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	public ContentShareCheck[] deleteByIds(Integer[] ids) {
		ContentShareCheck checks[]=new ContentShareCheck[ids.length];
		if(ids!=null&&ids.length>0){
			for(Integer i=0;i<ids.length;i++){
				checks[i]=findById(ids[i]);
				if (checks[i] != null) {
					getSession().delete(checks[i]);
				}
			}
		}
		return checks;
	}
	
	

	@Override
	protected Class<ContentShareCheck> getEntityClass() {
		return ContentShareCheck.class;
	}
}