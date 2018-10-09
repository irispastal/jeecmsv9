package com.jeecms.core.dao;

import java.util.Date;
import java.util.List;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsSmsRecord;

public interface CmsSmsRecordDao {
	public Pagination getPage(Byte smsId,int pageNo, int pageSize, String phone, Integer validateType, String username, Date drawTimeBegin, Date drawTimeEnd);
	
	public List<CmsSmsRecord> getList(Integer smsId);

	public CmsSmsRecord findById(Integer id);

	public CmsSmsRecord save(CmsSmsRecord bean);

	public CmsSmsRecord updateByUpdater(Updater<CmsSmsRecord> updater);

	public CmsSmsRecord deleteById(Integer id);

	public List<CmsSmsRecord> findByPhone(String phone,Date startTime,Date endTime);
}
