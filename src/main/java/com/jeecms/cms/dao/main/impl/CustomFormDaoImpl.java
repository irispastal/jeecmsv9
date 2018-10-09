package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.jeecms.cms.dao.main.CustomFormDao;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
@Repository
public class CustomFormDaoImpl extends HibernateBaseDao<CustomForm, Integer> implements CustomFormDao {

	@Override
	public Pagination getPage(Integer siteId,Boolean memberMeun,int pageNo, int pageSize) {
		Finder f = Finder.create("from CustomForm bean where 1=1");
		if (memberMeun!=null) {
			f.append(" and bean.memberMeun=:memberMeun").setParam("memberMeun", memberMeun);
		}
		if (siteId!=null) {
			f.append(" and (bean.allSite=true or bean.site.id=:siteId)").setParam("siteId", siteId);
		}
		f.append(" order by bean.priority");
		return find(f, pageNo, pageSize);
	}


	@Override
	protected Class<CustomForm> getEntityClass() {
		// TODO Auto-generated method stub
		return CustomForm.class;
	}

	@Override
	public CustomForm findById(Integer id) {
		// TODO Auto-generated method stub
		CustomForm bean=get(id);
		return bean;
	}

	@Override
	public CustomForm deleteById(Integer id) {
		CustomForm bean = super.get(id);
		if (bean != null) {
			getSession().delete(bean);
		}
		return bean;
	}

	@Override
	public CustomForm save(CustomForm bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	public List<CustomForm> getList(Boolean memberMeun,Boolean enable,Integer siteId) {
		Finder f=Finder.create("from CustomForm bean where 1=1");
		if (memberMeun!=null) {
			f.append(" and bean.memberMeun=:memberMeun").setParam("memberMeun", memberMeun);
		}
		if (enable!=null) {
			f.append(" and bean.enable=:enable").setParam("enable", enable);
		}
		if (siteId!=null) {
			f.append(" and (bean.allSite=true or bean.site.id=:siteId)").setParam("siteId", siteId);
		}
		f.append(" order by bean.priority");
		
		return find(f);
	}

	
}
