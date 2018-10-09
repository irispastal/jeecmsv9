package com.jeecms.core.entity;

import com.alibaba.fastjson.JSONObject;
import com.jeecms.core.entity.base.BaseCmsUserIdcard;
import org.apache.commons.lang.StringUtils;

public class CmsUserIdcard extends BaseCmsUserIdcard {
    public CmsUserIdcard(Integer id) {
        super(id);
    }

    public CmsUserIdcard() {
        super();
    }

    public JSONObject convertoJSON() {
        JSONObject object = new JSONObject();
        if (getId() != null) {
            object.put(PROP_ID, getId());
        }

        if (StringUtils.isNotBlank(getIdcard())) {
            object.put(PROP_IDCARD, getIdcard());
        }

        if (StringUtils.isNotBlank(getMobile())) {
            object.put(PROP_MOBILE, getMobile());
        }

        if (StringUtils.isNotBlank(getRealname())) {
            object.put(PROP_REALNAME, getRealname());
        }

        if (StringUtils.isNotBlank(getAddress())) {
            object.put(PROP_ADDRESS, getAddress());
        }

        return object;
    }
}
