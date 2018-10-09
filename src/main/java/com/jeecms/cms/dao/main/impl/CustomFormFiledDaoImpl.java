package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.CustomFormFiledDao;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
@Repository
public class CustomFormFiledDaoImpl extends HibernateBaseDao<CustomFormFiled, Integer> implements CustomFormFiledDao {

	@Override
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	@Override
	public CustomFormFiled findById(Integer id) {
		CustomFormFiled bean=get(id);
		return bean;
	}

	@Override
	public CustomFormFiled deleteById(Integer id) {
		CustomFormFiled bean = super.get(id);
		if (bean != null) {
			getSession().delete(bean);
		}
		return bean;
	}

	@Override
	public CustomFormFiled save(CustomFormFiled bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public List<CustomFormFiled> getList(Boolean displayInList, Integer formId) {
		Finder f=Finder.create("from CustomFormFiled bean where 1=1");
		if (formId!=null) {
			f.append(" and bean.form.id=:id");
			f.setParam("id", formId);
		}	
		if (displayInList!=null) {
			f.append(" and bean.displayInList=:displayInList").setParam("displayInList", displayInList);
		}
		f.append(" order by priority");
		return find(f);
	}
	
	@Override
	protected Class<CustomFormFiled> getEntityClass() {
		return CustomFormFiled.class;
	}


}
