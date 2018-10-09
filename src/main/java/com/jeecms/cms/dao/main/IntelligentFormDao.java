package com.jeecms.cms.dao.main;

import java.util.List;

import com.jeecms.cms.entity.main.IntelligentForm;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;

public interface IntelligentFormDao {
	
	public Pagination getPage(int pageNo, int pageSize);
	
	public List<IntelligentForm> getList();

	public IntelligentForm findById(Integer id);

	public IntelligentForm save(IntelligentForm bean);

	public IntelligentForm updateByUpdater(Updater<IntelligentForm> updater);

	public IntelligentForm deleteById(Integer id);

}
