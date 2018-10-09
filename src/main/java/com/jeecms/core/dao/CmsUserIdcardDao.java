package com.jeecms.core.dao;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUserIdcard;

import java.util.List;

public interface CmsUserIdcardDao {

    public CmsUserIdcard findById(Integer id);

    public Pagination getPage(Integer userid, Integer pageNo, Integer pageSize);

    public CmsUserIdcard save(CmsUserIdcard idcard);

    public CmsUserIdcard updateByUpdater(Updater<CmsUserIdcard> updater);

    public CmsUserIdcard deleteById(Integer id);
}
