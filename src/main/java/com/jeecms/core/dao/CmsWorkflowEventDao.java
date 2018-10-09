package com.jeecms.core.dao;

import java.util.List;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsWorkflowEvent;

public interface CmsWorkflowEventDao {
	
	public List<CmsWorkflowEvent> getListByWorkFlowId(Integer workflowId);
	
	public List<CmsWorkflowEvent> getListByUserId(Integer userId);
	
	public Pagination getPage(int pageNo, int pageSize);

	public CmsWorkflowEvent findById(Integer id);
	
	public List<CmsWorkflowEvent> find(Integer dataTypeId, Integer dataId);

	public CmsWorkflowEvent save(CmsWorkflowEvent bean);

	public CmsWorkflowEvent updateByUpdater(Updater<CmsWorkflowEvent> updater);

	public CmsWorkflowEvent deleteById(Integer id);

}