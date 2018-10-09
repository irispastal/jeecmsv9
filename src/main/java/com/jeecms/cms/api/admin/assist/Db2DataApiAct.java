package com.jeecms.cms.api.admin.assist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.annotation.SignValidate;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.cms.entity.back.CmsField;
import com.jeecms.cms.manager.assist.CmsDb2DataBackMng;
import com.jeecms.cms.manager.assist.CmsResourceMng;
import com.jeecms.common.file.FileWrap;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.util.Zipper;
import com.jeecms.common.util.Zipper.FileEntry;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.web.WebErrors;

@Controller
public class Db2DataApiAct {
	private static String SUFFIX = "sql";
	private static String BR = "\r\n";
	private static String SLASH="/";
	private static String BRANCH = ";";
	private static String DROP_TABLE=" DROP TABLE  ";
	private static String ALTER_TABLE=" ALTER TABLE ";
	private static String ALTER_COLUMN=" ALTER COLUMN ";
	private static String DROP_IDENTITY=" DROP  IDENTITY ";
	private static String ENFORCED="ENFORCED";
	private static String NOT_ENFORCED="NOT ENFORCED";
	private static String SET_IDENTITY_BEGIN=" SET GENERATED Always AS IDENTITY (START WITH ";
	private static String SET_IDENTITY_END=",INCREMENT BY 1,CACHE 10) ";
	private static String backup_table="";
	private static final String INVALID_PARAM = "template.invalidParams";
	private static final Logger log = LoggerFactory.getLogger(CmsResourceApiAct.class);
	
	@RequestMapping("/db2/data/list")
	public void list(HttpServletRequest request,HttpServletResponse response){
		List<String> list= dataBackMng.listTables();
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i));
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/listfields")
	public void listfiled(String tablename,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		if (StringUtils.isNotBlank(tablename)) {
			List<CmsField> list = dataBackMng.listFields(tablename);
			JSONArray jsonArray = new JSONArray();
			if (list!=null&&list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					jsonArray.put(i,list.get(i).convertToJson());
				}
			}
			body = jsonArray.toString();
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/backuppath")
	public void getBackUpPath(HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		if (StringUtils.isNotBlank(com.jeecms.cms.Constants.BACKUP_PATH)) {
			body = "{\"backuppath\":\""+com.jeecms.cms.Constants.BACKUP_PATH+"\"}";
		}
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/db2/data/revert")
	public void revert(String filename,String db,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, filename,db);
		if (!errors.hasErrors()) {
			String backFilePath = realPathResolver.get(com.jeecms.cms.Constants.BACKUP_PATH 
					+ SLASH +filename);
			File file=new File(backFilePath);
			if(!file.exists()){
				errors.addErrorString("error.db.hasnotselectfile");
			}
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_PARAM_ERROR;
			}else{
				try {
					String sql=readFile(backFilePath);
					//若db发生变化，需要处理jdbc
					dataBackMng.executeSQL(sql);
					result = true;
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				}catch(IOException e){
					message = Constants.API_MESSAGE_FILE_NOT_FOUNT;
					code = ResponseCode.API_CODE_FILE_NOT_FOUNT;
				}catch (Exception e) {
					message = Constants.API_MESSAGE_DB_REVERT_ERROR;
					code = ResponseCode.API_CODE_DB_REVERT_ERROR;
				}
			}
		}
		body = "{\"result\":"+result+"}";
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/backup")
	public void backup(String tableNames,String encoding,HttpServletRequest request,
			HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, tableNames,encoding);
		if (!errors.hasErrors()) {
			String[] tableNameArr = tableNames.split(",");
			String backpath = realPathResolver.get(com.jeecms.cms.Constants.BACKUP_PATH);
			File backDirectory = new File(backpath);
			if (!backDirectory.exists()) {
				backDirectory.mkdir();
			}
			DateUtils dateUtils = DateUtils.getDateInstance();
			String backFilePath = backpath + SLASH+ dateUtils.getNowString() + "."
					+ SUFFIX;
			File file=new File(backFilePath);
			Thread thread =new DateBackupTableThread(file,tableNameArr,encoding);
			thread.start();
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/files")
	public void files(HttpServletRequest request,HttpServletResponse response){
		List<FileWrap> list = resourceMng.listFile(com.jeecms.cms.Constants.BACKUP_PATH, false);
		JSONArray jsonArray = new JSONArray();
		if (list!=null&&list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i).convertToJson());
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/db2/data/delete")
	public void delete(String names,HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, names);
		if (!errors.hasErrors()) {
			String[] nameArr = names.split(",");
			errors = validateDelete(nameArr, request);
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = ResponseCode.API_CODE_PARAM_ERROR;
			}else{
				try {
					int count = resourceMng.delete(nameArr);
					log.info("delete Resource count: {}", count);
					for (String name : nameArr) {
						log.info("delete Resource name={}", name);
						cmsLogMng.operating(request, "resource.log.delete", "filename="
								+ name);
					}
					message = Constants.API_MESSAGE_SUCCESS;
					code = ResponseCode.API_CODE_CALL_SUCCESS;
				} catch (Exception e) {
					message = Constants.API_MESSAGE_DELETE_ERROR;
					code = ResponseCode.API_CODE_DELETE_ERROR;
				}
			}
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/origName")
	public void getOrigName(String name,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, name);
		if (!errors.hasErrors()) {
			body = "{\"origName\":\""+name.substring(com.jeecms.cms.Constants.BACKUP_PATH.length())+"\"}";
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@SignValidate
	@RequestMapping("/db2/data/rename")
	public void rename(String origName,String distName,
			HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, origName,distName);
		if (!errors.hasErrors()) {
			String orig = com.jeecms.cms.Constants.BACKUP_PATH + origName;
			String dist = com.jeecms.cms.Constants.BACKUP_PATH + distName;
			resourceMng.rename(orig, dist);
			log.info("name Resource from {} to {}", orig, dist);
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/db2/data/export")
	public void export(String names,HttpServletRequest request,HttpServletResponse response){
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, names);
		if (!errors.hasErrors()) {
			String[] nameArr = names.split(",");
			String backName="back";
			if(names!=null&&nameArr.length>0&&nameArr[0]!=null){
				backName=nameArr[0].substring(nameArr[0].indexOf(com.jeecms.cms.Constants.BACKUP_PATH)
						+com.jeecms.cms.Constants.BACKUP_PATH.length()+1);
			}
			List<FileEntry> fileEntrys = new ArrayList<FileEntry>();
			response.setContentType("application/x-download;charset=UTF-8");
			response.addHeader("Content-disposition", "filename="
					+ backName+".zip");
			for(String filename:nameArr){
				File file=new File(realPathResolver.get(filename));
				fileEntrys.add(new FileEntry("", "", file));
			}
			try {
				// 模板一般都在windows下编辑，所以默认编码为GBK
				Zipper.zip(response.getOutputStream(), fileEntrys, "GBK");
			} catch (IOException e) {
				log.error("export db error!", e);
			}
		}
	}
	
	@RequestMapping("/db2/data/progress")
	public void getBackupProgress(HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		if (StringUtils.isNotBlank(backup_table)) {
			body = "{\"tablename\":\""+backup_table+"\"}";
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private boolean validate(String[] names,HttpServletRequest request) {
		if(names!=null&&names.length>0){
			for(String name:names){
				//导出阻止非法获取其他目录文件
				if (!name.contains(com.jeecms.cms.Constants.BACKUP_PATH)
						||name.contains("../")||name.contains("..\\")) {
					return true;
				}
			}
		}else{
			return true;
		}
		return false;
	}
	
	
	private WebErrors validateDelete(String[] names,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(names, "names", true);
		if(names!=null&&names.length>0){
			for(String name:names){
				//导出阻止非法获取其他目录文件
				if (!name.contains("/WEB-INF/backup/")||name.contains("../")||name.contains("..\\")) {
					errors.addErrorString(INVALID_PARAM);
				}
			}
		}else{
			errors.addErrorString(INVALID_PARAM);
		}
		for (String id : names) {
			vldExist(id, errors);
		}
		return errors;
	}
	
	private boolean vldExist(String name, WebErrors errors) {
		if (errors.ifNull(name, "name", false)) {
			return true;
		}
		return false;
	}
	
	
	private class DateBackupTableThread extends Thread{
		private File file;
		private String[] tablenames;
		private String encoding;
		public DateBackupTableThread(File file, String[] tablenames,String encoding) {
			super();
			this.file = file;
			this.tablenames = tablenames;
			this.encoding=encoding;
		}
		public void run() {
			FileOutputStream out;
			OutputStreamWriter writer=null;
			try {
				out = new FileOutputStream(file);
				writer = new OutputStreamWriter(out, "utf8");
				writer.append(dataBackMng.disableORenbaleFK(false));
				writer.append(BR);
				try {
					for (int i=0;i<tablenames.length;i++) {
						backup_table=tablenames[i];
						backupTable(writer,tablenames[i],encoding);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				backup_table="";
				writer.append(dataBackMng.disableORenbaleFK(true));
				writer.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private   String backupTable(OutputStreamWriter writer,String tablename,String encoding) throws IOException, SQLException {
			writer.write(createOneTableSql(tablename,encoding));
			writer.flush();
			return tablename;
		}
		
		private String createOneTableSql(String tablename,String encoding)throws SQLException {
			StringBuffer buffer = new StringBuffer();
			String references=dataBackMng.getTableReferences(tablename);
			//删除表语句
			buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX +DROP_TABLE+ tablename + BRANCH + BR);
			boolean generatedByIdentity=false;
			String identityColumn=dataBackMng.getIdentityColumn(tablename);
			String tableDDL="";
			try {
				tableDDL=dataBackMng.createTableDDL(tablename,encoding);
				if(tableDDL.contains(ENFORCED)&&!tableDDL.contains(NOT_ENFORCED)){
					tableDDL=tableDDL.replace(ENFORCED, NOT_ENFORCED);
				}
				buffer.append(tableDDL + BR);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//存在自动增长列并且存在数据需要插入
			String dataInsertSqls=dataBackMng.createTableDataSQL(tablename);
			if(StringUtils.isNotBlank(tableDDL)&&StringUtils.isNotBlank(identityColumn)&&StringUtils.isNotBlank(dataInsertSqls)){
				generatedByIdentity=true;
				//自增长字段不能插入值，取消自动增长属性
				buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX +ALTER_TABLE+tablename+ALTER_COLUMN+identityColumn+DROP_IDENTITY+BRANCH+BR);
			}
			buffer.append(dataInsertSqls);
			//插入数据后恢复自动增长属性
			if(generatedByIdentity){
				//获取自动增长列最大值
				Integer max=dataBackMng.getMaxValueOfIdentityColumn(tablename);
				Integer identityStart=1;
				if(max!=null){
					identityStart+=max;
				}
				buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX +ALTER_TABLE+tablename+ALTER_COLUMN+identityColumn+SET_IDENTITY_BEGIN+identityStart+SET_IDENTITY_END+BRANCH+BR);
			}
			//从新创建约束
			buffer.append(references);
			return buffer.toString();
		}
	}
	
	private  String readFile(String filename) throws IOException {
	    File file =new File(filename);
	    if(filename==null || filename.equals(""))
	    {
	    	return Constants.API_MESSAGE_PARAM_REQUIRED;
	    }
	    long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(file));
	    int r = bufferedInputStream.read( bytes );
	    if (r != len){
	    	// throw new IOException("<@s.m 'db.filereaderror'/>");
	    }
	    bufferedInputStream.close();
	    return new String(bytes,"utf-8").replace(BR, "");
	}
	
	
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private CmsDb2DataBackMng dataBackMng;
	@Autowired
	private CmsResourceMng resourceMng;
	@Autowired
	private CmsLogMng cmsLogMng;
}
