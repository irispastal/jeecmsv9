package com.jeecms.core.dao.impl;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserIdcardDao;
import com.jeecms.core.entity.CmsUserIdcard;
import org.springframework.stereotype.Repository;

@Repository
public class CmsUserIdcardDaoImpl extends HibernateBaseDao<CmsUserIdcard, Integer> implements CmsUserIdcardDao {
    @Override
    protected Class<CmsUserIdcard> getEntityClass() {
        return CmsUserIdcard.class;
    }

    @Override
    public CmsUserIdcard findById(Integer id) {
        CmsUserIdcard entity = get(id);
        return entity;
    }

    @Override
    public Pagination getPage(Integer userid, Integer pageNo, Integer pageSize) {
        Finder f = Finder.create("select bean from CmsUserIdcard bean");
        if (userid != null) {
            f.append(" where bean.user.id=:userid");
            f.setParam("userid", userid);
        }
        f.append(" order by bean.id desc");

        return find(f, pageNo, pageSize);
    }

    @Override
    public CmsUserIdcard save(CmsUserIdcard bean) {
        getSession().save(bean);
        return bean;
    }

    @Override
    public CmsUserIdcard deleteById(Integer id) {
        CmsUserIdcard entity = super.get(id);
        if (entity != null) {
            entity.getUser().getUserIdcardSet().remove(entity);
            entity.setUser(null);
            getSession().delete(entity);
        }
        return entity;
    }
}
