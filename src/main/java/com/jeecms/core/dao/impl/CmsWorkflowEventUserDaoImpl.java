package com.jeecms.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsWorkflowEventUserDao;
import com.jeecms.core.entity.CmsWorkflowEventUser;

@Repository
public class CmsWorkflowEventUserDaoImpl extends HibernateBaseDao<CmsWorkflowEventUser, Integer> implements CmsWorkflowEventUserDao {
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public CmsWorkflowEventUser findById(Integer id) {
		CmsWorkflowEventUser entity = get(id);
		return entity;
	}

	public CmsWorkflowEventUser save(CmsWorkflowEventUser bean) {
		getSession().save(bean);
		return bean;
	}
	
	public void deleteByEvent(Integer eventId){
		Query query=getSession().createQuery("delete from CmsWorkflowEventUser bean where bean.event.id=:eventId");
		query.setParameter("eventId", eventId).executeUpdate();
	}
	

	public CmsWorkflowEventUser deleteById(Integer id) {
		CmsWorkflowEventUser entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsWorkflowEventUser> getEntityClass() {
		return CmsWorkflowEventUser.class;
	}
}