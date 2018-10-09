package com.jeecms.core.dao;

import java.util.List;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsWorkflow;

public interface CmsWorkflowDao {
	public Pagination getPage(Integer siteId,int pageNo, int pageSize);
	
	public List<CmsWorkflow> getList(Integer siteId,Boolean disabled);

	public CmsWorkflow findById(Integer id);

	public CmsWorkflow save(CmsWorkflow bean);

	public CmsWorkflow updateByUpdater(Updater<CmsWorkflow> updater);

	public CmsWorkflow deleteById(Integer id);
}