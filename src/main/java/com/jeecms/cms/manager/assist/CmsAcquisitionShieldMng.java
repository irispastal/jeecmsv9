package com.jeecms.cms.manager.assist;

import java.util.List;

import com.jeecms.cms.entity.assist.CmsAcquisitionShield;
import com.jeecms.common.hibernate4.Updater;
/**
 * 采集管理批量替换MANGER
 * @author Administrator
 *
 */
public interface CmsAcquisitionShieldMng {
	
	public CmsAcquisitionShield save(CmsAcquisitionShield bean);
	
	public CmsAcquisitionShield updateByUpdater(Updater<CmsAcquisitionShield> updater);
	
	public List<CmsAcquisitionShield> getList(Integer acquisitionId);
}
