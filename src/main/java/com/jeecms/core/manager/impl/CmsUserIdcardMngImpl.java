package com.jeecms.core.manager.impl;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserIdcardDao;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserIdcard;
import com.jeecms.core.manager.CmsUserIdcardMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmsUserIdcardMngImpl implements CmsUserIdcardMng {
    @Override
    @Transactional(readOnly = true)
    public CmsUserIdcard findById(Integer id) {
        CmsUserIdcard entity = dao.findById(id);
        return entity;
    }

    @Override
    public CmsUserIdcard save(CmsUserIdcard bean) {
        dao.save(bean);
        return bean;
    }

    /**
     *
     * @param bean
     * @return 全量更新
     */
    @Override
    public CmsUserIdcard update(CmsUserIdcard bean) {
        Updater<CmsUserIdcard> updater = new Updater<>(bean);
        return dao.updateByUpdater(updater);
    }

    @Override
    public CmsUserIdcard deleteById(Integer id) {
        return dao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagination page(Integer userid, Integer pageNo, Integer pageSize) {
        return dao.getPage(userid, pageNo, pageSize);
    }

    @Override
    public CmsUserIdcard register(CmsUser user, String idCard) {
        CmsUserIdcard idcard = new CmsUserIdcard();
        String realname = StringUtils.isBlank(user.getRealname()) ? user.getUsername() : user.getRealname();
        idcard.setIdcard(idCard);
        idcard.setMobile(user.getMobile());
        idcard.setAddress(user.getComefrom());
        idcard.setRealname(user.getUsername());
        idcard.setRealname(realname);
        idcard.setUser(user);
        return dao.save(idcard);
    }

    private CmsUserIdcardDao dao;

    @Autowired
    public void setDao(CmsUserIdcardDao dao) {
        this.dao = dao;
    }
}
