package com.jeecms.cms.manager.main;

import java.util.List;

import com.jeecms.cms.entity.main.CmsModelItem;
import com.jeecms.cms.entity.main.CustomFormFiled;
import com.jeecms.common.page.Pagination;
/**
 * 自定义表单自定义字段manager
 * @author Administrator
 *
 */
public interface CustomFormFiledMng {
	public Pagination getPage(int pageNo, int pageSize);
	
	public CustomFormFiled findById(Integer id);
	
	public CustomFormFiled deleteById(Integer id);
	
	public CustomFormFiled save(CustomFormFiled bean);
	
	public CustomFormFiled updateByUpdater(CustomFormFiled bean);
	
	/**
	 * @param displayInList 是否展示在列表中
	 * @param formId
	 * @return
	 */
	public List<CustomFormFiled> getList(Boolean displayInList, Integer formId);
	
	public CustomFormFiled save(CustomFormFiled bean,Integer formId);
	
	public CustomFormFiled[] deleteByIds(Integer[] ids);
	
	public CustomFormFiled[]  updatePriority(Integer[] wid,Integer[] priority);

}
