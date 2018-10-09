package com.jeecms.cms.manager.assist;

import java.util.List;

import com.jeecms.cms.entity.assist.CmsAcquisitionReplace;
import com.jeecms.common.hibernate4.Updater;
/**
 * 采集内容关键词替换业务逻辑接口
 * @author Administrator
 *
 */
public interface CmsAcquisitionReplaceMng {
	
	public CmsAcquisitionReplace save(CmsAcquisitionReplace bean);
	
	public CmsAcquisitionReplace updateByUpdater(Updater<CmsAcquisitionReplace> updater);
	
	public List<CmsAcquisitionReplace>  getList(Integer acquisitionId);
}
