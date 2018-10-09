package com.jeecms.core.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.entity.assist.CmsGuestbookCtg;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.manager.assist.CmsGuestbookCtgMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsDepartmentDao;
import com.jeecms.core.entity.CmsDepartment;
import com.jeecms.core.manager.CmsDepartmentMng;

@Service
@Transactional
public class CmsDepartmentMngImpl implements CmsDepartmentMng {
	
	@Transactional(readOnly = true)
	public CmsDepartment findByName(String name) {
		return dao.findByName(name);
	}
	
	@Transactional(readOnly = true)
	public List<CmsDepartment> getList(Integer parentId,boolean all){
		return dao.getList(parentId,all);
	}
	
	@Transactional(readOnly = true)
	public Pagination getPage(String name, int pageNo,
			int pageSize) {
		return dao.getPage( name, pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public CmsDepartment findById(Integer id) {
		CmsDepartment entity = dao.findById(id);
		return entity;
	}

	public CmsDepartment save(CmsDepartment bean) {
		dao.save(bean);
		return bean;
	}
	
	public CmsDepartment save(CmsDepartment bean,Integer channelIds[],Integer[] controlChannelIds,Integer[]ctgIds) {
		dao.save(bean);
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				bean.addToChannels(channel);
				while(channel.getParent()!=null){
					channel = channelMng.findById(channel.getParent().getId());
					bean.addToChannels(channel);
				}
			}
		}
		if (controlChannelIds != null) {
			Channel channel;
			for (Integer cid : controlChannelIds) {
				channel = channelMng.findById(cid);
				bean.addToControlChannels(channel);
				while(channel.getParent()!=null){
					channel = channelMng.findById(channel.getParent().getId());
					bean.addToControlChannels(channel);
				}
			}
		}
		if(ctgIds!=null){
			CmsGuestbookCtg ctg;
			for(Integer cid:ctgIds){
				ctg=guestBookCtgMng.findById(cid);
				bean.addToGuestBookCtgs(ctg);
			}
		}
		return bean;
	}

	public CmsDepartment update(CmsDepartment bean) {
		Updater<CmsDepartment> updater = new Updater<CmsDepartment>(bean);
		CmsDepartment entity = dao.updateByUpdater(updater);
		return entity;
	}
	
	public CmsDepartment update(CmsDepartment bean,Integer channelIds[],Integer[] controlChannelIds,Integer[]ctgIds) {
		CmsDepartment entity = findById(bean.getId());
		Updater<CmsDepartment> updater = new Updater<CmsDepartment>(bean);
		updater.include("parent");
		entity = dao.updateByUpdater(updater);
		entity.getChannels().clear();
		entity.getControlChannels().clear();
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				entity.addToChannels(channel);
				while(channel.getParent()!=null){
					channel = channelMng.findById(channel.getParent().getId());
					entity.addToChannels(channel);
				}
			}
		}
		if (controlChannelIds != null) {
			Channel channel;
			for (Integer cid : controlChannelIds) {
				channel = channelMng.findById(cid);
				entity.addToControlChannels(channel);
				while(channel.getParent()!=null){
					channel = channelMng.findById(channel.getParent().getId());
					entity.addToControlChannels(channel);
				}
			}
		}
		entity.getGuestBookCtgs().clear();
		if(ctgIds!=null){
			CmsGuestbookCtg ctg;
			for(Integer cid:ctgIds){
				ctg=guestBookCtgMng.findById(cid);
				entity.addToGuestBookCtgs(ctg);
			}
		}
		return entity;
	}

	public void updatePriority(Integer[] ids, Integer[] priorities) {
		if (ids == null || priorities == null || ids.length <= 0
				|| ids.length != priorities.length) {
			return;
		}
		CmsDepartment department;
		for (int i = 0, len = ids.length; i < len; i++) {
			department = findById(ids[i]);
			department.setPriority(priorities[i]);
		}
	}

	public CmsDepartment deleteById(Integer id) {
		CmsDepartment bean = dao.deleteById(id);
		return bean;
	}

	public CmsDepartment[] deleteByIds(Integer[] ids) {
		CmsDepartment[] beans = new CmsDepartment[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}
	public boolean nameExist(String name) {
		return dao.findByName(name)!=null;
	}

	private CmsDepartmentDao dao;
	
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private CmsGuestbookCtgMng guestBookCtgMng;

	@Autowired
	public void setDao(CmsDepartmentDao dao) {
		this.dao = dao;
	}

}