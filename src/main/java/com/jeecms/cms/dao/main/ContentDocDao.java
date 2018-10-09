package com.jeecms.cms.dao.main;

import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.common.hibernate4.Updater;

public interface ContentDocDao {
	public ContentDoc findById(Integer id);

	public ContentDoc save(ContentDoc bean);

	public ContentDoc updateByUpdater(Updater<ContentDoc> updater);
}