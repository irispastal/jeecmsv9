package com.jeecms.cms.entity.main;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.jeecms.cms.entity.main.base.BaseContentShareCheck;
import com.jeecms.common.util.DateUtils;



public class ContentShareCheck extends BaseContentShareCheck {
	private static final long serialVersionUID = 1L;
	/**
	 * 待审核
	 */
	public static final byte CHECKING = 0;
	/**
	 * 审核通过
	 */
	public static final byte CHECKED = 1;
	/**
	 * 推送
	 */
	public static final byte PUSHED = 2;

	public JSONObject convertToJson(){
		JSONObject json = new JSONObject();
		if (getId()!=null) {
			json.put("id", getId());
		}else{
			json.put("id", "");
		}
		if (getCheckStatus()!=null) {
			json.put("checkStatus", getCheckStatus());
		}else{
			json.put("checkStatus", "");
		}
		if (getChannel()!=null&&getChannel().getSite()!=null&&StringUtils.isNotBlank(getChannel().getSite().getName())) {
			json.put("channelSiteName", getChannel().getSite().getName());
		}else{
			json.put("channelSiteName", "");
		}
		if (getChannel()!=null&&StringUtils.isNotBlank(getChannel().getName())) {
			json.put("channelName", getChannel().getName());
		}else{
			json.put("channelName", "");
		}
		if (getContent()!=null&&getContent().getShared()!=null) {
			json.put("shared", getContent().getShared());
		}else{
			json.put("shared", "");
		}
		if (getContent()!=null&&getContent().getSiteId()!=null) {
			json.put("contentSiteId", getContent().getSiteId());
		}else{
			json.put("contentSiteId", "");
		}
		if (getContent()!=null&&getContent().getChannel()!=null&&StringUtils.isNotBlank(getContent().getChannel().getName())) {
			json.put("contentChannelName", getContent().getChannel().getName());
		}else{
			json.put("contentChannelName", "");
		}
		if (getContent()!=null&&StringUtils.isNotBlank(getContent().getUrl())) {
			json.put("url", getContent().getUrl());
		}else{
			json.put("url", "");
		}
		if (getContent()!=null&&StringUtils.isNotBlank(getContent().getTitle())) {
			json.put("title", getContent().getTitle());
		}else{
			json.put("title", "");
		}
		if (getContent()!=null&&StringUtils.isNotBlank(getContent().getCheckOpinion())) {
			json.put("checkOpinion", getContent().getCheckOpinion());
		}else{
			json.put("checkOpinion", "");
		}
		if (getContent()!=null&&getContent().getSite()!=null&&StringUtils.isNotBlank(getContent().getSite().getName())) {
			json.put("contentSiteName", getContent().getSite().getName());
		}else{
			json.put("contentSiteName", "");
		}
		if (getContent()!=null&&getContent().getUser()!=null&&StringUtils.isNotBlank(getContent().getUser().getUsername())) {
			json.put("username", getContent().getUser().getUsername());
		}else{
			json.put("username", "");
		}
		if (getContent()!=null&&getContent().getReleaseDate()!=null) {
			json.put("releaseDate", DateUtils.parseDateToDateStr(getContent().getReleaseDate()));
		}else{
			json.put("releaseDate", "");
		}
		if (getContent()!=null&&getContent().getId()!=null) {
			json.put("contentId", getContent().getId());
		}else{
			json.put("contentId", "");
		}
		if (getContent()!=null) {
			json.put("hasDeleteRight", getContent().isHasDeleteRight());
		}else{
			json.put("hasDeleteRight", "");
		}
		return json;
	}
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public ContentShareCheck () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public ContentShareCheck (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public ContentShareCheck (
		java.lang.Integer id,
		com.jeecms.cms.entity.main.Content content,
		com.jeecms.cms.entity.main.Channel channel,
		java.lang.Byte checkStatus,
		java.lang.Boolean shareValid) {

		super (
			id,
			content,
			channel,
			checkStatus,
			shareValid);
	}
	public void init() {
		byte status=0;
		if(getCheckStatus()==null){
			setCheckStatus(status);
		}
		if(getShareValid()==null){
			setShareValid(true);
		}
	}

/*[CONSTRUCTOR MARKER END]*/


}