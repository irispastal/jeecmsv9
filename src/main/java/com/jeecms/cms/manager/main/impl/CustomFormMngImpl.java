package com.jeecms.cms.manager.main.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.CustomFormDao;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsWorkflow;
import com.jeecms.core.manager.CmsWorkflowMng;
@Service
@Transactional
public class CustomFormMngImpl implements CustomFormMng {
	
	@Transactional(readOnly = true)
	public Pagination getPage(Integer siteId,Boolean memberMeun,int pageNo, int pageSize) {
		Pagination page = dao.getPage(siteId ,memberMeun,pageNo, pageSize);
		return page;
	}

	@Transactional(readOnly = true)
	public CustomForm findById(Integer id) {
		return dao.findById(id);
	}

	@Override
	public CustomForm deleteById(Integer id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public CustomForm save(CustomForm bean) {
		bean.init();
		dao.save(bean);
		return bean;
	}
	
	@Override
	public CustomForm save(CustomForm bean,Integer workflowId) {
		bean.init();
		if (workflowId!=null) {
		  CmsWorkflow workflow = cmsWorkflowMng.findById(workflowId);
		  bean.setWorkflow(workflow);
		}
		dao.save(bean);
		return bean;
	}
	
	@Override
	public CustomForm[] updatePriority(Integer[] ids, Integer[] prioritys) {
		int len = ids.length;
		CustomForm[] beans = new CustomForm[len];
		for (int i = 0; i < len; i++) {
			beans[i] = findById(ids[i]);
			beans[i].setPriority(prioritys[i]);
		}
		return beans;
	}

	@Override
	public CustomForm[] deleteByIds(Integer[] ids) {
		CustomForm[] beans = new CustomForm[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	@Transactional(readOnly=true)
	public List<CustomForm> getList(Boolean memberMeun,Boolean enable,Integer siteId) {
		return dao.getList(memberMeun,enable, siteId);
	}
	
	@Override
	public CustomForm update(CustomForm bean,Integer workflowId) {
		Updater<CustomForm> updater = new Updater<CustomForm>(bean);
		CustomForm entity = dao.updateByUpdater(updater);
		if(workflowId!=null){
			entity.setWorkflow(cmsWorkflowMng.findById(workflowId));
		}
		return entity;
	}
	
	@Autowired
	private CmsWorkflowMng cmsWorkflowMng;

	@Autowired
	private CustomFormDao dao;

}
