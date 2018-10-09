package com.jeecms.core.manager.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.core.dao.CmsWorkflowRecordDao;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflowEvent;
import com.jeecms.core.entity.CmsWorkflowRecord;
import com.jeecms.core.manager.CmsWorkflowRecordMng;

@Service
@Transactional
public class CmsWorkflowRecordMngImpl implements CmsWorkflowRecordMng {
	@Transactional(readOnly = true)
	public List<CmsWorkflowRecord> getList(Integer eventId,Integer userId){
		return dao.getList(eventId, userId);
	}
	
	public CmsWorkflowRecord save(CmsSite site, CmsWorkflowEvent event,
			CmsUser user, String opinion,Date recordTime, Integer type){
		CmsWorkflowRecord bean = new CmsWorkflowRecord();
		bean.setEvent(event);
		bean.setOpinion(opinion);
		bean.setRecordTime(recordTime);
		bean.setSite(site);
		bean.setType(type);
		bean.setUser(user);
		dao.save(bean);
		return bean;
	}
	
	private CmsWorkflowRecordDao dao;

	@Autowired
	public void setDao(CmsWorkflowRecordDao dao) {
		this.dao = dao;
	}
}