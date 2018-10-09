package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jeecms.cms.dao.main.CustomFormFiledDao;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.cms.manager.main.CustomFormFiledMng;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
@Service
@Transactional
public class CustomFormFiledMngImpl implements CustomFormFiledMng {

	@Transactional(readOnly = true)
	public Pagination getPage(int pageNo, int pageSize) {
		return dao.getPage(pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public CustomFormFiled findById(Integer id) {
		return dao.findById(id);
	}

	@Override
	public CustomFormFiled deleteById(Integer id) {
		return dao.deleteById(id);
	}

	@Override
	public CustomFormFiled save(CustomFormFiled bean) {
		return dao.save(bean);
	}

	@Override
	public CustomFormFiled updateByUpdater(CustomFormFiled bean) {
		Updater<CustomFormFiled> updater = new Updater<CustomFormFiled>(bean);
		CustomFormFiled entity = dao.updateByUpdater(updater);
		return entity;
	}

	public CustomFormFiled[] deleteByIds(Integer[] ids) {
		CustomFormFiled[] beans = new CustomFormFiled[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	@Transactional(readOnly = true)
	public List<CustomFormFiled> getList(Boolean displayInList, Integer formId) {
		return dao.getList(displayInList,formId);
	}

	@Override
	public CustomFormFiled save(CustomFormFiled bean, Integer formId) {
		bean.setForm(customFormMng.findById(formId));
		return save(bean);
	}
	
	@Override
	public CustomFormFiled[] updatePriority(Integer[] wid, Integer[] priority) {
		int len = wid.length;
		CustomFormFiled[] beans = new CustomFormFiled[len];
		for (int i = 0; i < len; i++) {
			beans[i] = findById(wid[i]);
			beans[i].setPriority(priority[i]);
		}
		return beans;
	}
	
	@Autowired
	private CustomFormFiledDao dao;
	
	@Autowired
	private CustomFormMng customFormMng;

}
