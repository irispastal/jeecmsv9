package com.jeecms.core.manager;

import java.util.Date;
import java.util.List;

import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsSms;
import com.jeecms.core.entity.CmsSmsRecord;
import com.jeecms.core.entity.CmsUser;

public interface CmsSmsRecordMng {

	public Pagination getPage(Byte smsId,int pageNo, int pageSize, String phone, Integer validateType, String username, Date drawTimeBegin, Date drawTimeEnd);
	
	public List<CmsSmsRecord> getList(Integer smsId);

	public CmsSmsRecord findById(Integer id);

	public CmsSmsRecord save(CmsSmsRecord bean);

	public CmsSmsRecord updateByUpdater(CmsSmsRecord bean);

	public CmsSmsRecord deleteById(Integer id);
	
	public List<CmsSmsRecord> findByPhone(String phone);

	public CmsSmsRecord[] deleteByIds(Integer[] idArr);
	
	public CmsSmsRecord save(CmsSms sms, String mobilePhone,Integer smsSendType,CmsSite site,CmsUser user);

}
