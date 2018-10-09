package com.jeecms.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsWorkflowRecordDao;
import com.jeecms.core.entity.CmsWorkflowRecord;

@Repository
public class CmsWorkflowRecordDaoImpl extends HibernateBaseDao<CmsWorkflowRecord, Integer> implements CmsWorkflowRecordDao {
	@SuppressWarnings("unchecked")
	public List<CmsWorkflowRecord> getList(Integer eventId,Integer userId){
		Finder f=Finder.create("from CmsWorkflowRecord bean where 1=1 ");
		if(eventId!=null){
			f.append(" and bean.event.id=:eventId").setParam("eventId", eventId);
		}
		if(userId!=null){
			f.append(" and bean.user.id=:userId").setParam("userId", userId);
		}
		f.append(" order by bean.recordTime desc,bean.id desc");
		return find(f);
	}
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public CmsWorkflowRecord findById(Integer id) {
		CmsWorkflowRecord entity = get(id);
		return entity;
	}

	public CmsWorkflowRecord save(CmsWorkflowRecord bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsWorkflowRecord deleteById(Integer id) {
		CmsWorkflowRecord entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsWorkflowRecord> getEntityClass() {
		return CmsWorkflowRecord.class;
	}
}