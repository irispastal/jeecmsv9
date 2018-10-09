package com.jeecms.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Repository;

import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.core.dao.CmsSiteDao;
import com.jeecms.core.entity.CmsSite;

@Repository
public class CmsSiteDaoImpl extends HibernateBaseDao<CmsSite, Integer>
		implements CmsSiteDao {

	public int siteCount(boolean cacheable) {
		String hql = "select count(*) from CmsSite bean";
		return ((Number) getSession().createQuery(hql).setCacheable(cacheable)
				.iterate().next()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsSite> getListByMaster(Boolean master) {
		String hql = "from CmsSite bean  where bean.master=:master order by bean.id asc";
		return getSession().createQuery(hql).setBoolean("master", master).list();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getList(boolean cacheable) {
		String hql = "from CmsSite bean order by bean.id asc";
		return getSession().createQuery(hql).setCacheable(cacheable).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsSite> getListByParent(Integer parentId) {
		String hql = "from CmsSite bean ";
		Finder f=Finder.create(hql);
		if(parentId!=null){
			f.append(" where bean.parent.id=:parentId").setParam("parentId", parentId);
		}
		return find(f);
	}
	
	public int getCountByProperty(String property){
		String hql = "select count(distinct "+property+") from CmsSite bean ";
		Query query = getSession().createQuery(hql);
		return ((Number) query.iterate().next()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsSite> getTopList(){
		String hql = "from CmsSite bean where bean.parent.id is null ";
		Finder f=Finder.create(hql);
		return find(f);
	}

	public CmsSite findByDomain(String domain) {
		return findUniqueByProperty("domain",domain);
	}
	
	public CmsSite findByAccessPath(String accessPath){
		return findUniqueByProperty("accessPath",accessPath);
	}

	public CmsSite findById(Integer id) {
		CmsSite entity = get(id);
		return entity;
	}

	public CmsSite save(CmsSite bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsSite deleteById(Integer id) {
		CmsSite entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public CmsSite getByDomain(String domain) {
		String hql = "from CmsSite bean where bean.domain=?";
		return findUniqueByProperty(hql, domain);
	}

	@Override
	protected Class<CmsSite> getEntityClass() {
		return CmsSite.class;
	}

	@Override
	public List<Map<String, Object>> getAttrListBySiteId(Integer siteId) {
		List<Map<String, Object>> attrList = null;
		StringBuffer sql = new StringBuffer("SELECT site_id as id ,\n" +
				"MAX(case attr_name when 'siteName' then attr_value END) as 'siteName',\n" +
				"MAX(case attr_name when 'userName' then attr_value END) as 'userName',\n" +
				"MAX(case attr_name when 'password' then attr_value END) as 'password',\n" +
				"MAX(case attr_name when 'tjSiteId' then attr_value END) as 'tjSiteId',\n" +
				"MAX(case attr_name when 'tjToken' then attr_value END) as 'tjToken'\n" +
				"FROM jc_site_attr where attr_name in ('siteName','userName','password','tjToken','tjSiteId') ");
		if (siteId!=null) {
			sql.append(" and site_id = '" + siteId + "'");
		} 
		sql.append(" GROUP BY site_id ORDER BY site_id ");
		try {
			attrList = queryForListWithSql(
					sql.toString(),
					new ColumnMapRowMapper());
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attrList;
	}

	@Override
	public Integer deleteAttrListBySiteId(Integer siteId) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer("delete FROM jc_site_attr where attr_name in ('siteName','userName','password','tjToken','tjSiteId') ");
		if (siteId!=null) {
			sql.append(" and site_id = '" + siteId + "'");
		} 
		Integer count = null;
		try {
			count = deleteWithSql(sql.toString());
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
}