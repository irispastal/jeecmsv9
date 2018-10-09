package com.jeecms.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jeecms.cms.entity.assist.CmsGuestbookCtg;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.common.hibernate4.PriorityInterface;
import com.jeecms.core.entity.base.BaseCmsDepartment;



public class CmsDepartment extends BaseCmsDepartment  implements PriorityInterface {
	private static final long serialVersionUID = 1L;
	
	public JSONObject convertToJson(Boolean isList){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (StringUtils.isNotBlank(getName())) {
			json.put("name", getName());
		}else{
			json.put("name", "");
		}
		if (getPriority()!=null) {
			json.put("priority", getPriority());
		}else{
			json.put("priority", "");
		}
		if (getWeights()!=null) {
			json.put("weights", getWeights());
		}else{
			json.put("weights", "");
		}
		if (isList!=null && !isList) {
			if (getChild()!=null&&getChild().size()>0) {
				Set<CmsDepartment> set = getChild();
				JSONArray jsonArray = new JSONArray();
				int index = 0 ;
				for(CmsDepartment department : set){
					jsonArray.put(index,department.convertToJson(isList));
					index++;
				}
				json.put("child", jsonArray);
				json.put("hasChild", true);
			}else{
				json.put("hasChild", false);
			}
			Set<Channel> channels = getChannels();
			JSONArray channelArray = new JSONArray();
			if (channels!=null&&channels.size()>0) {
				for (Channel channel : channels) {
					if (channel.getChild()==null||channel.getChild().size()==0) {
						channelArray.put(easyJsonToChannel(channel));
					}
				}
			}
			json.put("channels", channelArray);
			Set<Channel> controlChannel = getControlChannels();
			JSONArray controlArray = new JSONArray();
			if (controlChannel!=null&&controlChannel.size()>0) {
				for (Channel channel : controlChannel) {
					if (channel.getChild()==null||channel.getChild().size()==0) {
						controlArray.put(easyJsonToChannel(channel));
					}
				}
			}
			json.put("parents", getNodeListId());
			json.put("controlChannels", controlArray);
			Set<CmsGuestbookCtg> ctgs = getGuestBookCtgs();
			JSONArray ctgArray = new JSONArray();
			if (ctgs!=null && ctgs.size()>0) {
				for (CmsGuestbookCtg ctg : ctgs) {
					ctgArray.put(ctg.convertToJson());
				}
			}
			json.put("ctgs", ctgArray);
		}
		if (getParent()!=null && getParent().getId()!=null) {
			json.put("pid", getParent().getId());
		}else{
			json.put("pid", "");
		}
		return json;
	}
	
	public List<Integer> getNodeListId(){
		List<CmsDepartment> departments = getNodeList();
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < departments.size(); i++) {
			ids.add(departments.get(i).getId());
		}
		return ids;
	}
	
	public List<String> getNodeListName(){
		List<CmsDepartment> departments = getNodeList();
		List<String> names = new ArrayList<>();
		for (int i = 0; i < departments.size(); i++) {
			names.add(departments.get(i).getName());
		}
		return names;
	}
	
	public List<CmsDepartment> getNodeList(){
		LinkedList<CmsDepartment> departments = new LinkedList<CmsDepartment>();
		CmsDepartment department = this;
		while (department!=null) {
			departments.addFirst(department);
			department = department.getParent();
		}
		return departments;
	}
	
	private JSONObject easyJsonToChannel(Channel channel) {
		JSONObject json = new JSONObject();
		if (channel.getId()!=null) {
			json.put("channelId", channel.getId());
		}else{
			json.put("channelId", "");
		}
		if (StringUtils.isNotBlank(channel.getName())) {
			json.put("channelName", channel.getName());
		}else{
			json.put("channelName", "");
		}
		return json;
	}

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsDepartment () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsDepartment (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsDepartment (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Integer priority,
		java.lang.Integer weights) {

		super (
			id,
			name,
			priority,
			weights);
	}

/*[CONSTRUCTOR MARKER END]*/

	/**
	 * 获得深度
	 * 
	 * @return 第一层为0，第二层为1，以此类推。
	 */
	public int getDeep() {
		int deep = 0;
		CmsDepartment parent = getParent();
		while (parent != null) {
			deep++;
			parent = parent.getParent();
		}
		return deep;
	}
	
	public  static List<CmsDepartment> getListForSelect(List<CmsDepartment> topList) {
		List<CmsDepartment> list = new ArrayList<CmsDepartment>();
		for (CmsDepartment c : topList) {
			addChildToList(list, c);
		}
		return list;
	}
	
	/**
	 * 递归将子部门加入列表
	 * 
	 * @param list
	 *            部门容器
	 * @param depart
	 *            待添加的部门，且递归添加子部门
	 */
	private static void addChildToList(List<CmsDepartment> list, CmsDepartment depart) {
		list.add(depart);
		Set<CmsDepartment> child = depart.getChild();
		for (CmsDepartment c : child) {
			addChildToList(list, c);
		}
	}
	
	public Integer[] getChannelIds() {
		Set<Channel> channels = getChannels();
		return Channel.fetchIds(channels);
	}
	public Integer[] getGuestBookCtgIds() {
		Set<CmsGuestbookCtg> channels = getGuestBookCtgs();
		return CmsGuestbookCtg.fetchIds(channels);
	}
	public Set<Integer> getChannelIds(Integer siteId) {
		Set<Channel> channels = getChannels();
		Set<Integer> ids = new HashSet<Integer>();
		for (Channel c : channels) {
			if (c.getSite().getId().equals(siteId)) {
				ids.add(c.getId());
			}
		}
		return ids;
	}
	public Set<Integer> getControlChannelIds(Integer siteId) {
		Set<Channel> channels = getControlChannels();
		Set<Integer> ids = new HashSet<Integer>();
		for (Channel c : channels) {
			if (c.getSite().getId().equals(siteId)) {
				ids.add(c.getId());
			}
		}
		return ids;
	}
	public void addToChannels(Channel channel) {
		if (channel == null) {
			return;
		}
		Set<Channel> set = getChannels();
		if (set == null) {
			set = new HashSet<Channel>();
			setChannels(set);
		}
		set.add(channel);
	}
	public void addToControlChannels(Channel channel) {
		if (channel == null) {
			return;
		}
		Set<Channel> set = getControlChannels();
		if (set == null) {
			set = new HashSet<Channel>();
			setControlChannels(set);
		}
		set.add(channel);
	}
	public void addToGuestBookCtgs(CmsGuestbookCtg ctg) {
		if (ctg == null) {
			return;
		}
		Set<CmsGuestbookCtg> set =getGuestBookCtgs();
		if (set == null) {
			set = new HashSet<CmsGuestbookCtg>();
			setGuestBookCtgs(set);
		}
		set.add(ctg);
	}
	
	public static Integer[] fetchIds(Collection<CmsDepartment> departments) {
		if (departments == null) {
			return null;
		}
		Integer[] ids = new Integer[departments.size()];
		int i = 0;
		for (CmsDepartment r : departments) {
			ids[i++] = r.getId();
		}
		return ids;
	}

	public void init() {
		if (getPriority()==null) {
			setPriority(1);
		}
		if (getWeights()==null) {
			setWeights(1);
		}
	}

}