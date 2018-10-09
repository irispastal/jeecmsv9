package com.jeecms.cms.dao.main.impl;

import org.springframework.stereotype.Component;

import com.jeecms.cms.dao.main.CustomRecordCheckDao;
import com.jeecms.cms.entity.main.CustomRecordCheck;
import com.jeecms.common.hibernate4.HibernateBaseDao;
@Component
public class CustomRecordCheckDaoImpl extends HibernateBaseDao<CustomRecordCheck, Long> implements CustomRecordCheckDao {

	@Override
	public CustomRecordCheck findById(Long id) {
		CustomRecordCheck entity=get(id);
		return entity;
	}

	@Override
	public CustomRecordCheck save(CustomRecordCheck bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<CustomRecordCheck> getEntityClass() {
		return CustomRecordCheck.class;
	}

}
