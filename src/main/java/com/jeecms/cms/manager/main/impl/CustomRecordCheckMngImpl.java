package com.jeecms.cms.manager.main.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.CustomRecordCheckDao;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.cms.entity.main.CustomRecordCheck;
import com.jeecms.cms.manager.main.CustomRecordCheckMng;
import com.jeecms.common.hibernate4.Updater;
@Service
@Transactional
public class CustomRecordCheckMngImpl implements CustomRecordCheckMng {

	@Override
	public CustomRecordCheck save(CustomRecordCheck check, CustomRecord record) {
		check.setRecord(record);
		check.init();
		dao.save(check);
		record.setCustomRecordCheck(check);
		return check;
	}

	@Override
	public CustomRecordCheck update(CustomRecordCheck bean) {
		Updater<CustomRecordCheck> updater = new Updater<CustomRecordCheck>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	@Autowired
	private CustomRecordCheckDao dao;
}
