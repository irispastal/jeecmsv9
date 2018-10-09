package com.jeecms.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsWorkflowDao;
import com.jeecms.core.entity.CmsWorkflow;

@Repository
public class CmsWorkflowDaoImpl extends HibernateBaseDao<CmsWorkflow, Integer> implements CmsWorkflowDao {
	public Pagination getPage(Integer siteId,int pageNo, int pageSize) {
		Finder f=Finder.create(" from CmsWorkflow bean");
		if(siteId!=null){
			f.append(" where bean.site.id=:siteId").setParam("siteId", siteId);
		}
		f.append(" order by bean.priority desc");
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsWorkflow> getList(Integer siteId,Boolean disabled){
		Finder f=Finder.create(" from CmsWorkflow bean where 1=1 ");
		if(siteId!=null){
			f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		}
		if(disabled!=null){
			if(disabled){
				f.append(" and bean.disabled=true");
			}else{
				f.append(" and bean.disabled=false");
			}
		}
		return find(f);
	}

	public CmsWorkflow findById(Integer id) {
		CmsWorkflow entity = get(id);
		return entity;
	}

	public CmsWorkflow save(CmsWorkflow bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsWorkflow deleteById(Integer id) {
		CmsWorkflow entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsWorkflow> getEntityClass() {
		return CmsWorkflow.class;
	}
}