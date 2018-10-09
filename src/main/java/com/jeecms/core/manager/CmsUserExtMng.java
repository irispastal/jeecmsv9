package com.jeecms.core.manager;

import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;

public interface CmsUserExtMng {
	public CmsUserExt findById(Integer userId);
	
	public CmsUserExt save(CmsUserExt ext, CmsUser user);

	public CmsUserExt update(CmsUserExt ext, CmsUser user);
	
	public void clearDayCount();
	
	public int countByPhone(String mobile);

	public CmsUserExt findByPhone(String mobile);
}