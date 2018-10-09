package com.jeecms.core.manager.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsSmsDao;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsSms;
import com.jeecms.core.manager.CmsConfigMng;
import com.jeecms.core.manager.CmsSmsMng;
@Service
@Transactional
public class CmsSmsMngImpl implements CmsSmsMng {

	@Transactional(readOnly = true)
	public Pagination getPage(Byte source,int pageNo, int pageSize) {
		Pagination page = dao.getPage(source,pageNo, pageSize);
		return page;
	}

	@Transactional(readOnly = true)
	public List<CmsSms> getList() {
		return dao.getList();
	}

	@Override
	public CmsSms findById(Integer id) {
		CmsSms entity = dao.findById(id);
		return entity;
	}

	@Override
	public CmsSms save(CmsSms bean) {
		dao.save(bean);
		return bean;
	}

	@Override
	public CmsSms update(CmsSms bean) {
		Updater<CmsSms> updater = new Updater<CmsSms>(bean);
		if (StringUtils.isBlank(bean.getAccessKeyId())) {
			updater.exclude("accessKeyId");
		}
		if (StringUtils.isBlank(bean.getAccessKeySecret())) {
			updater.exclude("accessKeySecret");
		}
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	@Override
	public CmsSms deleteById(Integer id) {
		CmsSms bean = dao.deleteById(id);
		return bean;
	}

	@Override
	public CmsSms[] deleteByIds(Integer[] ids) {
		CmsSms[] beans = new CmsSms[ids.length];
		for (int i = 0; i < beans.length; i++) {
			//查询设置中是否引用了当前配置
			CmsConfig cmsConfig = manager.get();
			Long smsID = cmsConfig.getSmsID();
			if(smsID != null) {
				if(Integer.valueOf(cmsConfig.getSmsID().toString()) == ids[i]) {
					cmsConfig.setSmsID(null);
					manager.update(cmsConfig);
				}				
			}
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}
	
	private CmsSmsDao dao;
	
	@Autowired
	public void setDao(CmsSmsDao dao){
		this.dao = dao;
	}

	@Override
	public CmsSms findBySource(Byte source) {
		CmsSms sms = dao.findBySource(source);
		return sms;
	}
	@Autowired
	private CmsConfigMng manager;
}
