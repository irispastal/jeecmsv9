package com.jeecms.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsWorkflowEventDao;
import com.jeecms.core.entity.CmsWorkflowEvent;

@Repository
public class CmsWorkflowEventDaoImpl extends HibernateBaseDao<CmsWorkflowEvent, Integer> implements CmsWorkflowEventDao {
	
	@SuppressWarnings("unchecked")
	public List<CmsWorkflowEvent> getListByWorkFlowId(Integer workflowId){
		Finder f=Finder.create("from CmsWorkflowEvent bean ");
		if(workflowId!=null){
			f.append("where bean.workFlow.id=:workflowId").setParam("workflowId", workflowId);
		}
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsWorkflowEvent> getListByUserId(Integer userId){
		Finder f=Finder.create("from CmsWorkflowEvent bean ");
		if(userId!=null){
			f.append("where bean.initiator.id=:userId").setParam("userId", userId);
		}
		return find(f);
	}
	
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public CmsWorkflowEvent findById(Integer id) {
		CmsWorkflowEvent entity = get(id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsWorkflowEvent>  find(Integer dataTypeId,Integer dataId){
		Finder f=Finder.create(" from CmsWorkflowEvent bean where 1=1 ");
		if(dataTypeId!=null){
			f.append(" and bean.dateType=:dataTypeId").setParam("dataTypeId", dataTypeId);
		}
		if(dataId!=null){
			f.append(" and bean.dateId=:dataId").setParam("dataId", dataId);
		}
		return find(f);
	}

	public CmsWorkflowEvent save(CmsWorkflowEvent bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsWorkflowEvent deleteById(Integer id) {
		CmsWorkflowEvent entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsWorkflowEvent> getEntityClass() {
		return CmsWorkflowEvent.class;
	}
}