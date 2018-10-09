package com.jeecms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeecms.cms.dao.assist.CmsAcquisitionShieldDao;
import com.jeecms.cms.entity.assist.CmsAcquisitionShield;
import com.jeecms.cms.manager.assist.CmsAcquisitionShieldMng;
import com.jeecms.common.hibernate4.Updater;
@Service
public class CmsAcquisitionShieldMngImpl implements CmsAcquisitionShieldMng {

	@Override
	public CmsAcquisitionShield save(CmsAcquisitionShield bean) {
		return cmsAcquisitionShieldDao.save(bean);
	}

	@Override
	public CmsAcquisitionShield updateByUpdater(Updater<CmsAcquisitionShield> updater) {
		return cmsAcquisitionShieldDao.updateByUpdater(updater);
	}
	
	@Override
	public List<CmsAcquisitionShield> getList(Integer acquisitionId) {		
		return cmsAcquisitionShieldDao.getList(acquisitionId);
	}
	
	@Autowired
	private CmsAcquisitionShieldDao cmsAcquisitionShieldDao;

}
