package com.jeecms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jeecms.cms.dao.assist.CmsAcquisitionReplaceDao;
import com.jeecms.cms.entity.assist.CmsAcquisitionReplace;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
@Component
public class CmsAcquisitionReplaceDaoImpl extends HibernateBaseDao<CmsAcquisitionReplace, Integer> implements CmsAcquisitionReplaceDao {

	@Override
	public CmsAcquisitionReplace save(CmsAcquisitionReplace bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public List<CmsAcquisitionReplace> getList(Integer acquisitionId) {
		Finder f=Finder.create("from CmsAcquisitionReplace bean where 1 = 1");
		if (acquisitionId!=null) {
			f.append(" and bean.acquisition.id = :acquisitionId");
			f.setParam("acquisitionId", acquisitionId);
		}
		return find(f);
	}

	@Override
	protected Class<CmsAcquisitionReplace> getEntityClass() {
		return CmsAcquisitionReplace.class;
	}
}
