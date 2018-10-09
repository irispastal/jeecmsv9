package com.jeecms.core.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsDepartmentDao;
import com.jeecms.core.entity.CmsDepartment;

@Repository
public class CmsDepartmentDaoImpl extends
		HibernateBaseDao<CmsDepartment, Integer> implements CmsDepartmentDao {
	@SuppressWarnings("unchecked")
	public List<CmsDepartment> getList(Integer parentId,boolean all){
		Finder f = Finder.create("from CmsDepartment bean");
		if(!all){
			if(parentId!=null){
				f.append(" where bean.parent.id=:parentId").setParam("parentId", parentId);
			}else{
				f.append(" where bean.parent is null ");
			}
		}
		f.append(" order by bean.priority asc ");
		return find(f);
	}
	public Pagination getPage( String name, int pageNo,int pageSize) {
		Finder f = Finder.create("from CmsDepartment bean where 1=1");
		if (StringUtils.isNotBlank(name)) {
			f.append(" and bean.name like :name");
			f.setParam("name", "%" + name + "%");
		}
		f.append(" order by bean.priority asc,weights desc");
		return find(f, pageNo, pageSize);
	}
	

	public CmsDepartment findById(Integer id) {
		CmsDepartment entity = get(id);
		return entity;
	}
	
	public CmsDepartment findByName(String name) {
		return findUniqueByProperty("name", name);
	}

	public CmsDepartment save(CmsDepartment bean) {
		getSession().save(bean);
		return bean;
	}


	public CmsDepartment deleteById(Integer id) {
		CmsDepartment entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	protected Class<CmsDepartment> getEntityClass() {
		return CmsDepartment.class;
	}


}