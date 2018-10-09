package com.jeecms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.assist.CmsAcquisitionReplaceDao;
import com.jeecms.cms.entity.assist.CmsAcquisitionReplace;
import com.jeecms.cms.manager.assist.CmsAcquisitionReplaceMng;
import com.jeecms.common.hibernate4.Updater;
@Service
@Transactional
public class CmsAcquisitionReplaceMngImpl implements CmsAcquisitionReplaceMng {

	@Override
	public CmsAcquisitionReplace save(CmsAcquisitionReplace bean) {
		return cmsAcquisitionReplaceDao.save(bean);
	}

	@Override
	public CmsAcquisitionReplace updateByUpdater(Updater<CmsAcquisitionReplace> updater) {		
		return cmsAcquisitionReplaceDao.updateByUpdater(updater);
	}
	
	@Transactional(readOnly = true)
	public List<CmsAcquisitionReplace> getList(Integer acquisitionId) {
		return cmsAcquisitionReplaceDao.getList(acquisitionId);
	}

	@Autowired
	private CmsAcquisitionReplaceDao cmsAcquisitionReplaceDao;

	
}
