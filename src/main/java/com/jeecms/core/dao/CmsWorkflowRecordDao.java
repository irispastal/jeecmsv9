package com.jeecms.core.dao;

import java.util.List;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsWorkflowRecord;

public interface CmsWorkflowRecordDao {
	public List<CmsWorkflowRecord> getList(Integer eventId,Integer userId);
	
	public Pagination getPage(int pageNo, int pageSize);

	public CmsWorkflowRecord findById(Integer id);

	public CmsWorkflowRecord save(CmsWorkflowRecord bean);

	public CmsWorkflowRecord updateByUpdater(Updater<CmsWorkflowRecord> updater);

	public CmsWorkflowRecord deleteById(Integer id);
}