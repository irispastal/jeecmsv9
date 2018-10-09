package com.jeecms.cms.manager.main.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jeecms.cms.dao.main.CustomRecordDao;
import com.jeecms.cms.entity.main.CustomForm;
import com.jeecms.cms.entity.main.CustomRecord;
import com.jeecms.cms.entity.main.CustomRecordCheck;
import com.jeecms.cms.manager.main.CustomFormMng;
import com.jeecms.cms.manager.main.CustomRecordCheckMng;
import com.jeecms.cms.manager.main.CustomRecordMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsWorkflow;
import com.jeecms.core.manager.CmsWorkflowMng;
import com.jeecms.core.web.util.CmsUtils;
@Service
@Transactional
public class CustomRecordMngImpl implements CustomRecordMng {

	@Transactional(readOnly=true)
	public List<CustomRecord> getList(Integer formId) {
		return dao.getList(formId);
	}

	@Override
	public CustomRecord save(CustomRecord bean) {
		return dao.save(bean);
	}

	@Override
	public CustomRecord deleteById(Integer id) {
		return dao.deleteById(id);
	}

	@Override
	public CustomRecord[] deleteByIds(Integer[] ids) {
		CustomRecord[] beans = new CustomRecord[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public CustomRecord[] check(Integer[] ids, CmsUser user) {
		CustomRecord[] beans = new CustomRecord[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = check(ids[i], user);
		}
		return beans;
	}
	
	public CustomRecord check(Integer id, CmsUser user) {
		CustomRecord record = findById(id);
		CustomRecordCheck check = record.getCustomRecordCheck();
		CmsWorkflow workflow;
		//是否需要检查状态
		workflow = record.getForm().getWorkflow();
		//终审不能审核
		if(record.getStatus().equals(CustomRecordCheck.CHECKED)){
			return record;
		}
		int workflowstep = workflowMng.check(workflow, record.getUser(), user, CustomRecord.DATA_CONTENT, record.getId(), null);
		if (workflowstep == -1) {
			record.setStatus(CustomRecordCheck.CHECKED);
			// 终审，清除退回意见
			check.setCheckOpinion(null);
			check.setRejected(false);
			check.setCheckStep((byte)workflowstep);
			//终审，设置审核者
			check.setReviewer(user);
			check.setCheckDate(Calendar.getInstance().getTime());
		} else if (workflowstep > 0) {
			record.setStatus(CustomRecordCheck.CHECKING);
			check.setCheckOpinion(null);
			check.setRejected(false);
			check.setCheckStep((byte)workflowstep);
		}
		return record;
	}
	
	@Transactional(readOnly=true)
	public CustomRecord findById(Integer id) {
		return dao.findById(id);
	}

	@Override
	public CustomRecord save(CustomRecord bean, Integer formId, HttpServletRequest request) {
		bean.setAttr(RequestUtils.getRequestMap(request, "attr_"));
		CustomForm form= customFormMng.findById(formId);
		bean.setForm(form);
		CmsSite site= CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		bean.setCreateTime(new Date());
		bean.setSite(site);
		Byte userStep;		
		userStep = user.getCheckStep(site.getId());
		CmsWorkflow workflow = bean.getForm().getWorkflow();
		// 流程处理
		if (workflow != null) {
			bean.setStatus(CustomRecordCheck.CHECKING);
		} else {
			bean.setStatus(CustomRecordCheck.CHECKED);
		}
		if (user!=null) {
			bean.setUser(user);
		}
		save(bean);
		CustomRecordCheck check=new CustomRecordCheck();
		check.init();
		check.setCheckStep(userStep);	
		if (workflow != null) {
			int step = workflowMng.check(workflow, user, user, CustomRecord.DATA_CONTENT, bean.getId(), null);
			if (step >= 0) {
				bean.setStatus(CustomRecordCheck.CHECKING);
				check.setCheckStep((byte)step);
			} else {
				bean.setStatus(CustomRecordCheck.CHECKED);
			}
		}
		recordCheckMng.save(check, bean);
		return bean;
	}

	@Transactional(readOnly=true)
	public Pagination getPage(Integer formId, Integer status,Integer userId, int pageNo, int pageSize) {
		return dao.getPage(formId, status, userId, pageNo, pageSize);
	}

	@Transactional(readOnly=true)
	public long getDaySubNum(Integer formId, Integer userId) {
		Date date=new Date();
		Date startTime=DateUtils.getStartDate(date);
		Date endTime= DateUtils.getFinallyDate(date);
		
		return dao.getSubNum(formId, userId,startTime,endTime);
	}

	@Autowired
	private CustomRecordDao dao;
	@Autowired
	private CustomFormMng customFormMng;
	@Autowired
	private CmsWorkflowMng workflowMng;
	@Autowired
	private CustomRecordCheckMng recordCheckMng;
}
