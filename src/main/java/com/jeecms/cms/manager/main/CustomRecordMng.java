package com.jeecms.cms.manager.main;


import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;
/**
 * 自定义表单提交记录manager
 * @author Administrator
 *
 */
public interface CustomRecordMng {
	/**
	 * @param formId
	 * @param status 审核状态：null 所有状态 1审核中 2已终审
	 * @param userId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pagination getPage(Integer formId, Integer status,Integer userId, int pageNo, int pageSize); 
		
	public List<CustomRecord> getList(Integer formId);
	
	public CustomRecord save(CustomRecord bean);
	
	public CustomRecord deleteById(Integer id);
	
	public CustomRecord[] deleteByIds(Integer[] ids);
	
	public CustomRecord findById(Integer id);
	
	public CustomRecord save(CustomRecord bean,Integer formId,HttpServletRequest request);
	
	public CustomRecord check(Integer id, CmsUser user);

	public CustomRecord[] check(Integer[] ids, CmsUser user);
	
	/**
	 * 查询该用户当日提交记录
	 * @param formId
	 * @param userId
	 * @return
	 */
	public long getDaySubNum(Integer formId,Integer userId);
	
}
