package com.jeecms.cms.manager.main;

import java.util.List;

import com.jeecms.cms.entity.main.IntelligentForm;
import com.jeecms.common.page.Pagination;

public interface IntelligentFormMng {
	
	public Pagination getPage(int pageNo, int pageSize);
	
	public List<IntelligentForm> getList();

	public IntelligentForm findById(Integer id);

	public IntelligentForm save(IntelligentForm bean);

	public IntelligentForm update(IntelligentForm bean);

	public IntelligentForm deleteById(Integer id);
	
	public IntelligentForm[] deleteByIds(Integer [] ids);

}
