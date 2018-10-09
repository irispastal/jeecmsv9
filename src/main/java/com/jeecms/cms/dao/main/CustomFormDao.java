package com.jeecms.cms.dao.main;

import java.util.List;

import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
/**
 * 自定义表单dao层
 * @author Administrator
 *
 */
public interface CustomFormDao {
	public Pagination getPage(Integer siteId,Boolean memberMeun,int pageNo, int pageSize);
	
	public CustomForm findById(Integer id);
	
	public CustomForm deleteById(Integer id);
	
	public CustomForm save(CustomForm bean);
	
	public CustomForm updateByUpdater(Updater<CustomForm> updater);
	
	public List<CustomForm> getList(Boolean memberMeun,Boolean enable,Integer siteId);
	
	
	
}
