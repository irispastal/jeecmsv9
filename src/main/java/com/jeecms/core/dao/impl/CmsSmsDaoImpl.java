package com.jeecms.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsSmsDao;
import com.jeecms.core.entity.CmsSms;

@Repository
public class CmsSmsDaoImpl extends HibernateBaseDao<CmsSms, Integer> implements CmsSmsDao {

	@Override
	public Pagination getPage(Byte source,int pageNo, int pageSize) {
		Finder f = Finder.create(" from CmsSms bean where 1=1");
		if (source!=null) {
			f.append(" and bean.source=:source").setParam("source", source);
		}
		f.append(" order by bean.createTime desc");
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CmsSms> getList() {
		String hql = "from CmsSms";
		Finder f = Finder.create(hql);
		f.setCacheable(true);
		return find(f);
	}

	@Override
	public CmsSms findById(Integer id) {
		CmsSms entity = get(id);
		return entity;
	}

	@Override
	public CmsSms save(CmsSms bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public CmsSms deleteById(Integer id) {
		CmsSms entity = super.get(id);
		if (entity != null && id != 0) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsSms> getEntityClass() {
		return CmsSms.class;
	}

	@Override
	public CmsSms findBySource(Byte source) {
		Finder f = Finder.create(" from CmsSms bean where bean.source=:source");
		f.setParam("source", source);
		@SuppressWarnings("unchecked")
		List<CmsSms> find = find(f);
		if(find.size() > 0) {
			return find.get(0);
		}
		return null;
	}

}
