package com.jeecms.core.dao;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsWorkflowEventUser;

public interface CmsWorkflowEventUserDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsWorkflowEventUser findById(Integer id);

	public CmsWorkflowEventUser save(CmsWorkflowEventUser bean);

	public CmsWorkflowEventUser updateByUpdater(Updater<CmsWorkflowEventUser> updater);

	public void deleteByEvent(Integer eventId);
	
	public CmsWorkflowEventUser deleteById(Integer id);
	
}