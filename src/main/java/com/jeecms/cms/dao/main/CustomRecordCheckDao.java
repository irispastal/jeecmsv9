package com.jeecms.cms.dao.main;

import com.jeecms.cms.entity.main.CustomRecordCheck;
import com.jeecms.common.hibernate4.Updater;

public interface CustomRecordCheckDao {
	public CustomRecordCheck findById(Long id);

	public CustomRecordCheck save(CustomRecordCheck bean);

	public CustomRecordCheck updateByUpdater(Updater<CustomRecordCheck> updater);
}
