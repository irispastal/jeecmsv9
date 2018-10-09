package com.jeecms.cms.dao.main.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import com.jeecms.cms.dao.main.CustomRecordDao;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
/**
 * customRecord 自定义表单提交记录DAO实现类层
 * @author Administrator
 *
 */
@Component
public class CustomRecordDaoImpl extends HibernateBaseDao<CustomRecord, Integer> implements CustomRecordDao {
	
	@Override
	public Pagination getPage(Integer formId, Integer status,Integer userId, int pageNo, int pageSize) {
		Finder f=Finder.create("from CustomRecord bean where 1=1");
		
		if (formId!=null) {
			f.append(" and bean.form.id=:formId");
			f.setParam("formId", formId);
		}
		if (status!=null) {
			f.append(" and bean.status=:status").setParam("status", status);
		}
		if (userId!=null) {
			f.append(" and bean.user.id=:userId").setParam("userId", userId);
		}
		
		f.append(" order by bean.createTime desc");
		return find(f, pageNo, pageSize);
	}

	
	@Override
	public List<CustomRecord> getList(Integer formId) {
		Finder f=Finder.create(" from CustomRecord bean where bean.form.id=:id order by bean.id desc");
		f.setParam("id", formId);
		return find(f);
	}

	@Override
	public CustomRecord save(CustomRecord bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public CustomRecord deleteById(Integer id) {
		CustomRecord bean = super.get(id);
		if (bean != null) {
			getSession().delete(bean);
		}
		return bean;
	}

	@Override
	public CustomRecord findById(Integer id) {
		CustomRecord bean= get(id);
		return bean;
	}

	@Override
	public long getSubNum(Integer formId, Integer userId,Date startTime,Date endTime) {
		String hql="select count(*) from CustomRecord bean where bean.createTime > ? and bean.createTime < ?";
		
		Query query= getSession().createQuery(hql).setParameter(0, startTime);
		query.setParameter(1, endTime);
		return  (Long) query.uniqueResult();
	}
	
	@Override
	protected Class<CustomRecord> getEntityClass() {
		// TODO Auto-generated method stub
		return CustomRecord.class;
	}

	
}
