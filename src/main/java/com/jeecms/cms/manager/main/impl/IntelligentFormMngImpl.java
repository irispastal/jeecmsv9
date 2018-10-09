package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jeecms.cms.dao.main.IntelligentFormDao;
import com.jeecms.cms.entity.main.IntelligentForm;
import com.jeecms.cms.manager.main.IntelligentFormMng;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;

@Service
@Transactional
public class IntelligentFormMngImpl implements IntelligentFormMng {

	@Transactional(readOnly = true)
	public Pagination getPage(int pageNo, int pageSize) {
		Pagination page = dao.getPage(pageNo, pageSize);
		return page;
	}
	
	@Transactional(readOnly = true)
	public List<IntelligentForm> getList(){
		List<IntelligentForm> list = dao.getList();
		return list;
	}

	@Transactional(readOnly = true)
	public IntelligentForm findById(Integer id) {
		IntelligentForm entity = dao.findById(id);
		return entity;
	}

	public IntelligentForm save(IntelligentForm bean) {
		dao.save(bean);
		return bean;
	}

	public IntelligentForm update(IntelligentForm bean) {
		Updater<IntelligentForm> updater = new Updater<IntelligentForm>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	public IntelligentForm deleteById(Integer id) {
		IntelligentForm bean = dao.deleteById(id);
		return bean;
	}
	
	public IntelligentForm[] deleteByIds(Integer[] ids) {
		IntelligentForm[] beans = new IntelligentForm[ids.length];
		for (int i = 0,len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private IntelligentFormDao dao;

	@Autowired
	public void setDao(IntelligentFormDao dao) {
		this.dao = dao;
	}
}
