package com.jeecms.cms.manager.main;

import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.cms.entity.main.CustomRecordCheck;

/*
 * 自定义表单审核manger
 * @author Administrator
 *
 */
public interface CustomRecordCheckMng {
	public CustomRecordCheck save(CustomRecordCheck check, CustomRecord record);

	public CustomRecordCheck update(CustomRecordCheck bean);
}
