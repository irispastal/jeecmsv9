package com.jeecms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jeecms.cms.dao.assist.CmsAcquisitionShieldDao;
import com.jeecms.cms.entity.assist.CmsAcquisitionShield;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.hibernate4.Updater;
@Component
public class CmsAcquisitionShieldDaoImpl extends HibernateBaseDao<CmsAcquisitionShield, Integer> implements CmsAcquisitionShieldDao {

	@Override
	public CmsAcquisitionShield save(CmsAcquisitionShield bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public List<CmsAcquisitionShield> getList(Integer acquisitionId) {
		Finder f=Finder.create("from CmsAcquisitionShield bean where 1 = 1");
		if (acquisitionId!=null) {
			f.append(" and bean.acquisition.id = :acquisitionId");
			f.setParam("acquisitionId", acquisitionId);
		}
		return find(f);
	}
	
	@Override
	protected Class<CmsAcquisitionShield> getEntityClass() {
		return CmsAcquisitionShield.class;
	}


}
