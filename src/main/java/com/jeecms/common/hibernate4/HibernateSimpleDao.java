package com.jeecms.common.hibernate4;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;

import com.ibm.db2.jcc.a.b;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.MyBeanUtils;

/**
 * hibernate DAO基类
 * 
 * 提供hql分页查询，不带泛型，与具体实体类无关。
 */
public abstract class HibernateSimpleDao {
	/**
	 * 日志，可用于子类
	 */
	protected Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * HIBERNATE 的 order 属性
	 */
	protected static final String ORDER_ENTRIES = "orderEntries";
	
	private Integer count;//执行executeUpdate影响的行数

	/**
	 * 通过HQL查询对象列表
	 * 
	 * @param hql
	 *            hql语句
	 * @param values
	 *            数量可变的参数
	 */
	@SuppressWarnings("unchecked")
	protected List find(String hql, Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 通过HQL查询唯一对象
	 */
	protected Object findUnique(String hql, Object... values) {
		return createQuery(hql, values).uniqueResult();
	}

	/**
	 * 通过Finder获得分页数据
	 * 
	 * @param finder
	 *            Finder对象
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页条数
	 * @return
	 */
	protected Pagination find(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	

	protected Pagination find(Finder finder,String totalHql, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder,totalHql);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		for(int i=0;i<list.size();i++){
			Object[]o=(Object[]) list.get(i);
		}
		p.setList(list);
		return p;
	}
	
	protected int countQueryResult(Finder finder,String hql) {
		Query query = getSession().createQuery(hql);
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		Iterator iterator= query.iterate();
		if(iterator.hasNext()){
			return ((Number) query.iterate().next()).intValue();
		}else{
			return 0;
		}
		
	}
	
	protected Pagination findBigData(Finder finder, int pageNo, int pageSize) {
		int totalCount = pageNo*pageSize;
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	
	protected Pagination findBigDataPage(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return p;
	}
	
	
	
	protected Pagination findByGroup(Finder finder,String selectSql, int pageNo, int pageSize) {
		return findByTotalCount(finder, pageNo, pageSize,  countQueryResultByGroup(finder,selectSql));
	}

	/**
	 * 通过Finder获得列表数据
	 * 
	 * @param finder
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List find(Finder finder) {
		Query query = finder.createQuery(getSession());
		List list = query.list();
		return list;
	}

	/**
	 * 根据查询函数与参数列表创建Query对象,后续可进行更多处理,辅助函数.
	 */
	protected Query createQuery(String queryString, Object... values) {
		Assert.hasText(queryString);
		Query queryObject = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject;
	}

	/**
	 * 通过Criteria获得分页数据
	 * 
	 * @param crit
	 * @param pageNo
	 * @param pageSize
	 * @param projection
	 * @param orders
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Pagination findByCriteria(Criteria crit, int pageNo, int pageSize) {
		CriteriaImpl impl = (CriteriaImpl) crit;
		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();
		List<CriteriaImpl.OrderEntry> orderEntries;
		try {
			orderEntries = (List) MyBeanUtils
					.getFieldValue(impl, ORDER_ENTRIES);
			MyBeanUtils.setFieldValue(impl, ORDER_ENTRIES, new ArrayList());
		} catch (Exception e) {
			throw new RuntimeException(
					"cannot read/write 'orderEntries' from CriteriaImpl", e);
		}

		int totalCount = ((Number) crit.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		crit.setProjection(projection);
		if (projection == null) {
			crit.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			crit.setResultTransformer(transformer);
		}
		try {
			MyBeanUtils.setFieldValue(impl, ORDER_ENTRIES, orderEntries);
		} catch (Exception e) {
			throw new RuntimeException(
					"set 'orderEntries' to CriteriaImpl faild", e);
		}
		crit.setFirstResult(p.getFirstResult());
		crit.setMaxResults(p.getPageSize());
		p.setList(crit.list());
		return p;
	}

	/**
	 * 获得Finder的记录总数
	 * 
	 * @param finder
	 * @return
	 */
	protected int countQueryResult(Finder finder) {
		Query query = getSession().createQuery(finder.getRowCountHql());
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return ((Number) query.iterate().next()).intValue();
	}
	
	protected int countQueryResultByGroup(Finder finder,String selectSql) {
		Query query = getSession().createQuery(finder.getRowCountTotalHql(selectSql));
		setParamsToQuery(finder, query);
		return ((Number) query.iterate().next()).intValue();
	}
	
	protected Pagination findByTotalCount(Finder finder, int pageNo, int pageSize,int totalCount) {
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	
	private Query setParamsToQuery(Finder finder,Query query){
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return query;
	}
	/**
	 * 保存一个实体对象
	 * 
	 * @param obj
	 * @return
	 */
	public Object save(Object obj) throws SQLIntegrityConstraintViolationException {
		try {
			Serializable se = getSession().save(obj);
			if (se == null) {
				return null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * hibernate处理SQL的实现，此方法是当SQL中含有参数时可以使用
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param args
	 * @param rse
	 * @return Object
	 * @throws org.springframework.dao.DataAccessException
	 * @throws java.sql.SQLException
	 */
	public Object queryWithSql(final String sql, final Object args[], final ResultSetExtractor<?> rse)
			throws org.springframework.dao.DataAccessException, java.sql.SQLException {
		Session session = getSession();
		Object obj = null;
		ResultSet resultSet = session.doReturningWork(new ReturningWork<ResultSet>() {
			@Override
			public ResultSet execute(Connection connection) throws java.sql.SQLException {
				ResultSet rs = null;
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				if (args.length > 0) {
					for (int i = 0; i < args.length; i++) {
						Object arg = args[i];
						if (arg instanceof SqlParameterValue) {
							SqlParameterValue paramValue = (SqlParameterValue) arg;
							StatementCreatorUtils.setParameterValue(preparedStatement, i + 1, paramValue,
									paramValue.getValue());
						} else {
							StatementCreatorUtils.setParameterValue(preparedStatement, i + 1, -2147483648, arg);
						}
					}
				}
				if (preparedStatement != null) {
					rs = preparedStatement.executeQuery();
				}
				return rs;
			}
		});
		obj = rse.extractData(resultSet);
		return obj;
	}

	/**
	 * 处理SQL的分页方法
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param args
	 * @return List<Map<String, Object>>
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> queryPageForListWithSql(String sql, Object[] args, int pageNo, int pageSize)
			throws DataAccessException, SQLException {
		int rn = (pageNo - 1) * pageSize+1;
		String RN = String.valueOf(rn);
		sql = "SELECT * FROM (SELECT A.*,ROWNUM RN FROM (" + sql + " AND ROWNUM <= " + pageNo * pageSize
				+ " ) A ) R WHERE R.RN >= " + RN;
		// 判断有没有参数
		if (args.length > 0) {
			return queryForListWithSql(sql, args, ((RowMapper) (new ColumnMapRowMapper())));
		} else {
			return queryForListWithSql(sql, ((RowMapper) (new ColumnMapRowMapper())));
		}

	}

	/**
	 * 不需要分页是直接调用此方法但需要传递个((RowMapper) (new ColumnMapRowMapper()))对象，前提是sql中有参数。
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return List<Map<String, Object>>
	 * @throws org.springframework.dao.DataAccessException
	 * @throws java.sql.SQLException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> queryForListWithSql(String sql, Object args[], RowMapper rowMapper)
			throws org.springframework.dao.DataAccessException, java.sql.SQLException {
		log.info("HibernateSimpleDao----queryForListWithSql:" + sql);
		return (List<Map<String, Object>>) queryWithSql(sql, args, new RowMapperResultSetExtractor((rowMapper)));
	}

	/**
	 * 不需要分页是直接调用此方法但需要传递个((RowMapper) (new
	 * ColumnMapRowMapper()))对象,sql中没有参数可以调用此方法。
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param rowMapper
	 * @return List<Map<String, Object>>
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> queryForListWithSql(String sql, RowMapper rowMapper)
			throws DataAccessException, SQLException {
		log.info("HibernateSimpleDao----queryForListWithSql:" + sql);
		return (List<Map<String, Object>>) queryWithSql(sql, new RowMapperResultSetExtractor((rowMapper)));
	}

	/**
	 * hibernate处理SQL的实现，此方法是处理不带参数的SQL实现
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param rse
	 * @return Object
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public Object queryWithSql(final String sql, final ResultSetExtractor<?> rse)
			throws DataAccessException, SQLException {
		Session session = getSession();
		Object obj = null;
		ResultSet resultSet = session.doReturningWork(new ReturningWork<ResultSet>() {
			@Override
			public ResultSet execute(Connection connection) throws SQLException {
				ResultSet rs = null;
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				rs = preparedStatement.executeQuery();
				return rs;
			}

		});
		obj = rse.extractData(resultSet);
		return obj;
	}
	/**
	 * hibernate处理SQL的实现，此方法是处理不带参数的SQL实现
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param rse
	 * @return Object
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public Integer deleteWithSql(final String sql)
			throws DataAccessException, SQLException {
		Session session = getSession();
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				count = preparedStatement.executeUpdate();
				log.info("HibernateSimpleDao---deleteWithSql:"+ count);
			}

		});
		return count;
	}

	/**
	 * 处理sql返回记录的总条数
	 * 
	 * @author wangyinsheng
	 * @param sql
	 * @param args
	 * @return int
	 */
	public int queryForIntWithSql(String sql, Object args[]) {
		log.info("HibernateSimpleDao---queryForIntWithSql:" + sql);
		Number number = (Number) queryForObjectWithSql(sql, args, Integer.class);
		return number != null ? number.intValue() : 0;
	}

	public Object queryForObjectWithSql(String sql, Object[] args, Class requiredType) {
		return queryForObjectWithSql(sql, args, new SingleColumnRowMapper(requiredType));
	}

	@SuppressWarnings("unchecked")
	public Object queryForObjectWithSql(String sql, Object[] args, RowMapper rowMapper) {
		List results = null;
		// 判断有没有参数
		try {
			if (args.length > 0) {
				results = (List) queryWithSql(sql, args, new RowMapperResultSetExtractor(rowMapper, 1));
			} else {
				results = (List) queryWithSql(sql, new RowMapperResultSetExtractor(rowMapper, 1));
			}

		} catch (DataAccessException | SQLException e) {
			e.printStackTrace();
		}

		return DataAccessUtils.requiredSingleResult(results);
	}

	/**
	 * 处理分页列表数据返回Pagination对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param args
	 * @param sql
	 * @return
	 */
	public Pagination queryPageDate(int pageNo, int pageSize, Object[] args, String sql) {
		List<Map<String, Object>> list = new ArrayList<>();
		String countSql ="SELECT COUNT(1) "+ sql.substring(sql.indexOf("FROM"));
		Pagination p = null;
		int pageCount = 0;
		try {
			// 得到分页列表数据
			list = queryPageForListWithSql(sql, args, pageNo, pageSize);
			// 得到总记录数
			pageCount = queryForIntWithSql(countSql, args);
			// 初始化分页对象
			p = new Pagination(pageNo, pageSize, pageCount);
			// 如果没有数据是返回一个空集合
			if (pageCount < 1) {
				p.setList(new ArrayList<>());
				return p;
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 将数据注入分页对象
		p.setList(list);
		return p;
	}


	protected SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
