package com.jeecms.core.manager.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.core.dao.CmsWorkflowEventDao;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflow;
import com.jeecms.core.entity.CmsWorkflowEvent;
import com.jeecms.core.manager.CmsWorkflowEventMng;
import com.jeecms.core.manager.CmsWorkflowEventUserMng;

@Service
@Transactional
public class CmsWorkflowEventMngImpl implements CmsWorkflowEventMng {
	
	@Transactional(readOnly = true)
	public List<CmsWorkflowEvent> getListByWorkFlowId(Integer workflowId){
		return dao.getListByWorkFlowId(workflowId);
	}
	
	@Transactional(readOnly = true)
	public List<CmsWorkflowEvent> getListByUserId(Integer userId){
		return dao.getListByUserId(userId);
	}

	@Transactional(readOnly = true)
	public CmsWorkflowEvent findById(Integer id) {
		CmsWorkflowEvent entity = dao.findById(id);
		return entity;
	}
	
	@Transactional(readOnly = true)
	public CmsWorkflowEvent find(Integer dataTypeId,Integer dataId){
		List<CmsWorkflowEvent> events=dao.find(dataTypeId,dataId);
		if(events!=null&&events.size()>0){
			return events.get(0);
		}else{
			return null;
		}
	}
	

	public CmsWorkflowEvent end(Integer eventId){
		CmsWorkflowEvent event= findById(eventId);
		event.setEndTime(Calendar.getInstance().getTime());
		event.setHasFinish(true);
		event.setUsers(null);
		event.setNextStep(-1);
		return event;
	}
	
	
	public CmsWorkflowEvent save(CmsWorkflow workflow, CmsUser initiator,
			Set<CmsUser> nextUsers, Integer dateTypeId, Integer dateId,
			Integer step,Boolean hasFinish){
		CmsWorkflowEvent bean=new CmsWorkflowEvent();
		bean.setWorkFlow(workflow);
		bean.setInitiator(initiator);
		bean.setDateType(dateTypeId);
		bean.setDateId(dateId);
		bean.setNextStep(step);
		bean.setHasFinish(hasFinish);
		bean.init();
		if(hasFinish!=null&&hasFinish){
			bean.setEndTime(Calendar.getInstance().getTime());
		}
		bean=dao.save(bean);
		return bean;
	}

	public CmsWorkflowEvent deleteById(Integer id) {
		//清除待审用户列表
		workflowEventUserMng.deleteByEvent(id);
		CmsWorkflowEvent bean = dao.deleteById(id);
		return bean;
	}
	
	public CmsWorkflowEvent[] deleteByIds(Integer[] ids) {
		CmsWorkflowEvent[] beans = new CmsWorkflowEvent[ids.length];
		for (int i = 0,len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}
	
	public void deleteByDate(Integer dataTypeId,Integer dataId){
		CmsWorkflowEvent event = find(dataTypeId, dataId);
		if(event!=null){
			deleteById(event.getDateId());
		}
	}

	private CmsWorkflowEventDao dao;
	@Autowired
	private CmsWorkflowEventUserMng workflowEventUserMng;

	@Autowired
	public void setDao(CmsWorkflowEventDao dao) {
		this.dao = dao;
	}
}