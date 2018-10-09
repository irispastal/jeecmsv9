package com.jeecms.cms.dao.main;

import java.util.List;

import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
/**
 * 自定义表单字段dao层
 * @author Administrator
 *
 */
public interface CustomFormFiledDao {
	public Pagination getPage(int pageNo, int pageSize);
	
	public CustomFormFiled findById(Integer id);
	
	public CustomFormFiled deleteById(Integer id);
	
	public CustomFormFiled save(CustomFormFiled bean);
	
	public CustomFormFiled updateByUpdater(Updater<CustomFormFiled> updater);
	
	public List<CustomFormFiled> getList(Boolean displayInList, Integer formId);

}
