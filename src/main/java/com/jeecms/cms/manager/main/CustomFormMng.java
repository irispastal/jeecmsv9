package com.jeecms.cms.manager.main;

import java.util.List;

import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.common.page.Pagination;
/**
 * 自定义表单manager层
 * @author Administrator
 *
 */
public interface CustomFormMng {
	public Pagination getPage(Integer siteId,Boolean memberMeun,int pageNo, int pageSize);
	
	public CustomForm findById(Integer id);
	
	public CustomForm deleteById(Integer id);
	
	public CustomForm save(CustomForm bean);
	
	public CustomForm save(CustomForm bean,Integer workflowId);
	
	public CustomForm[] updatePriority(Integer[] ids,Integer[] prioritys);
	
	public CustomForm[] deleteByIds(Integer[] ids);
	
	public CustomForm update(CustomForm bean,Integer workflowId);
	/**
	 * 获取列表
	 * @param siteId
	 * @param memberMeun 是否会员菜单
	 * @param enable 是否启用
	 * @return
	 */
	public List<CustomForm> getList(Boolean memberMeun,Boolean enable,Integer siteId);

}
