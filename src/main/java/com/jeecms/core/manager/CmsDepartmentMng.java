package com.jeecms.core.manager;

import java.util.List;

import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsDepartment;

public interface CmsDepartmentMng {

	/**
	 * 
	 * @param parentId 父层部门id
	 * @param all 是否查询所有部门
	 * @return
	 */
	public List<CmsDepartment> getList(Integer parentId,boolean all);
	
	public Pagination getPage( String name, int pageNo,int pageSize);

	public CmsDepartment findById(Integer id);
	
	public CmsDepartment findByName(String name);
	
	public CmsDepartment save(CmsDepartment bean);
	
	public CmsDepartment save(CmsDepartment bean,Integer channelIds[],Integer[] controlChannelIds,Integer[]ctgIds);

	public CmsDepartment update(CmsDepartment bean);
	
	public CmsDepartment update(CmsDepartment bean,Integer channelIds[],Integer[] controlChannelIds,Integer[]ctgIds);

	public void updatePriority(Integer[] ids, Integer[] priorities);

	public CmsDepartment deleteById(Integer id);
	
	public CmsDepartment[] deleteByIds(Integer ids[]);

	public boolean nameExist(String name);


}