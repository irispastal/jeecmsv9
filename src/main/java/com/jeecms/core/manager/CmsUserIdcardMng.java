package com.jeecms.core.manager;

import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserIdcard;

public interface CmsUserIdcardMng {
    public CmsUserIdcard findById(Integer id);

    public CmsUserIdcard save(CmsUserIdcard bean);

    public CmsUserIdcard update(CmsUserIdcard bean);

    public CmsUserIdcard deleteById(Integer id);

    public Pagination page(Integer userid, Integer pageNo, Integer pageSize);

    public CmsUserIdcard register(CmsUser user, String idCard);
}
