package com.jeecms.core.manager;

import java.util.Set;

import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflowEvent;
import com.jeecms.core.entity.CmsWorkflowEventUser;

public interface CmsWorkflowEventUserMng {
	
	public Set<CmsWorkflowEventUser> save(CmsWorkflowEvent event,Set<CmsUser>users);

	public Set<CmsWorkflowEventUser> update(CmsWorkflowEvent event,Set<CmsUser>users);
	
	public void deleteByEvent(Integer eventId);

}