package com.jeecms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.ContentDocDao;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.common.hibernate4.HibernateBaseDao;

@Repository
public class ContentDocDaoImpl extends HibernateBaseDao<ContentDoc, Integer>
		implements ContentDocDao {
	public ContentDoc findById(Integer id) {
		ContentDoc entity = get(id);
		return entity;
	}

	public ContentDoc save(ContentDoc bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ContentDoc> getEntityClass() {
		return ContentDoc.class;
	}
}