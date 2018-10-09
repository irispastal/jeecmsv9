package com.jeecms.core.manager;

import java.util.Date;
import java.util.List;

import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflowEvent;
import com.jeecms.core.entity.CmsWorkflowRecord;

public interface CmsWorkflowRecordMng {
	
	public List<CmsWorkflowRecord> getList(Integer eventId,Integer userId);

	public CmsWorkflowRecord save(CmsSite site, CmsWorkflowEvent event,
			CmsUser user, String opinion,Date recordTime, Integer type);

}