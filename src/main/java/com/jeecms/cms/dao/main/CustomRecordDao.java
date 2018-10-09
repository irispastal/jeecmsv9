package com.jeecms.cms.dao.main;

import java.util.Date;
import java.util.List;

import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.common.page.Pagination;
/**
 * 自定义表单提交记录dao层
 * @author Administrator
 *
 */
public interface CustomRecordDao {
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
	
	public CustomRecord findById(Integer id);
	
	/**
	 * 查询该用户时间范围内提交记录
	 * @param formId
	 * @param userId
	 * @param startTime 
	 * @param endTime
	 * @return
	 */
	public long getSubNum(Integer formId, Integer userId,Date startTime,Date endTime);
	
}
