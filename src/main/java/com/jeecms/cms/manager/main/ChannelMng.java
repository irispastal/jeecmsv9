package com.jeecms.cms.manager.main;

import java.util.List;
import java.util.Map;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.ChannelExt;
import com.jeecms.cms.entity.main.ChannelTxt;
import com.jeecms.common.page.Pagination;

/**
 * 栏目管理接口
 * 
 */
public interface ChannelMng {
	public List<Channel> getList();
	/**
	 * 获得顶级栏目
	 * 
	 * @param siteId
	 *            站点ID
	 * @param hasContentOnly
	 *            是否只获得有内容的栏目
	 * @return
	 */
	public List<Channel> getTopList(Integer siteId, boolean hasContentOnly);

	public List<Channel> getTopListByRigth(Integer userId, Integer siteId,boolean hasContentOnly);

	public List<Channel> getTopListForTag(Integer siteId, boolean hasContentOnly);

	public Pagination getTopPageForTag(Integer siteId, boolean hasContentOnly,int pageNo, int pageSize);

	public List<Channel> getChildList(Integer parentId, boolean hasContentOnly);

	public List<Channel> getChildListByRight(Integer userId, Integer siteId,Integer parentId, boolean hasContentOnly);

	public List<Channel> getChildListForTag(Integer parentId,boolean hasContentOnly);
	
	public List<Channel> getBottomList(Integer siteId,boolean hasContentOnly);

	public Pagination getChildPageForTag(Integer parentId,boolean hasContentOnly, int pageNo, int pageSize);

	/**
	 * 获取部门 顶层栏目内容权限
	 * @param departId
	 * @param userId
	 * @param siteId
	 * @param hasContentOnly
	 * @return
	 */
	public List<Channel> getTopListForDepartId(Integer departId,Integer userId,Integer siteId,boolean hasContentOnly);
	/**
	 * 获取部门 顶层栏目控制权限
	 * @param departId
	 * @param userId
	 * @param siteId
	 * @param hasContentOnly
	 * @return
	 */
	public List<Channel> getControlTopListForDepartId(Integer departId,Integer userId,Integer siteId,boolean hasContentOnly);
	
	/**
	 * 获取下级 栏目内容权限
	 * @param departId
	 * @param siteId
	 * @param parentId
	 * @param hasContentOnly
	 * @return
	 */
	public List<Channel> getChildListByDepartId(Integer departId,Integer siteId,Integer parentId, boolean hasContentOnly);
	
	/**
	 * 获取下级栏目控制权限
	 * @param departId
	 * @param siteId
	 * @param parentId
	 * @param hasContentOnly
	 * @return
	 */
	public List<Channel> getControlChildListByDepartId(Integer departId,Integer siteId,Integer parentId, boolean hasContentOnly);

	public Channel findByPath(String path, Integer siteId);

	public Channel findByPathForTag(String path, Integer siteId);

	public Channel findById(Integer id);

	public Channel save(Channel bean, ChannelExt ext, ChannelTxt txt,
			Integer[] viewGroupIds, Integer[] contriGroupIds,
			Integer[] userIds, Integer siteId, Integer parentId,
			Integer modelId,Integer workflowId,
			Integer[]modelIds,String[] tpls,String[] mtpls
			,boolean isCopy);
	
	public Channel copy(Integer cid,String solution, String mobileSolution, Integer siteId, Map<String, String> pathMap);
	

	public Channel update(Channel bean, ChannelExt ext, ChannelTxt txt,
			Integer[] viewGroupIds, Integer[] contriGroupIds,
			Integer[] userIds, Integer parentId, Map<String, String> attr, Integer modelId,
			Integer workflowId,Integer[]modelIds,String[] tpls,String[] mtpls);

	public Channel deleteById(Integer id);

	public Channel[] deleteByIds(Integer[] ids);

	public Channel[] updatePriority(Integer[] ids, Integer[] priority);
	
	public void initWorkFlow(Integer workflowId);

	public String checkDelete(Integer id);
}