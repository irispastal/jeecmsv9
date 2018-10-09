package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import com.jeecms.cms.dao.main.IntelligentFormDao;
import com.jeecms.cms.entity.main.IntelligentForm;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;

@Repository
public class IntelligentFormDaoImpl extends HibernateBaseDao<IntelligentForm, Integer> implements IntelligentFormDao {

	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public List<IntelligentForm> getList(){
		Finder f = Finder.create("from IntelligentForm bean where 1=1 ");
		f.append(" order by bean.id desc");
		f.setCacheable(true);
		return find(f);
	}

	public IntelligentForm findById(Integer id) {
		IntelligentForm entity = get(id);
		return entity;
	}

	public IntelligentForm save(IntelligentForm bean) {
		getSession().save(bean);
		return bean;
	}

	public IntelligentForm deleteById(Integer id) {
		IntelligentForm entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<IntelligentForm> getEntityClass() {
		return IntelligentForm.class;
	}
	
}
