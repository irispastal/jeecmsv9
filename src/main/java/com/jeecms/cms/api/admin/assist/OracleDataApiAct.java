package com.jeecms.cms.api.admin.assist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
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
import com.jeecms.cms.manager.assist.CmsOracleDataBackMng;
import com.jeecms.cms.manager.assist.CmsResourceMng;
import com.jeecms.common.file.FileWrap;
import com.jeecms.common.util.DateFormatUtils;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.util.Zipper;
import com.jeecms.common.util.Zipper.FileEntry;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.web.WebErrors;

@Controller
public class OracleDataApiAct {
	private static String SUFFIX = "sql";
	private static String BR = "\r\n";
	private static String SLASH="/";
	private static String SPACE = " ";
	private static String BRANCH = ";";
	private static String INSERT_INTO = "INSERT INTO ";
	private static String VALUES = "VALUES";
	private static String LEFTBRACE = "(";
	private static String RIGHTBRACE = ")";
	private static final String DROP_TABLE=" DROP TABLE ";
	private static final String ALTER_TABLE=" ALTER TABLE  ";
	private static final String DROP_CONSTRAINT=" DROP CONSTRAINT  ";
	private static final String TO_DATE="to_date";
	private static final String FORMAT_STRING="yyyy-mm-dd hh24:mi:ss";
	private static String QUOTES = "'";
	private static String COMMA = ",";
	private static String CLOB="CLOB";
	private static String EQUALS=":=";
	private static String DECLARE="declare";
	private static String BEGIN="begin";
	private static String END="end";
	private String backup_table="start";
	private static final String INVALID_PARAM = "template.invalidParams";
	private static final Logger log = LoggerFactory.getLogger(CmsResourceApiAct.class);
	
	@RequestMapping("/oracle/data/list")
	public void list(HttpServletResponse response,HttpServletRequest request){
		List<String> list = dataBackMng.listTabels();
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
	
	@RequestMapping("/oracle/data/listfields")
	public void filed(String tablename,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, tablename);
		if (!errors.hasErrors()) {
			List<CmsField> list = dataBackMng.listFields(tablename);
			JSONArray jsonArray = new JSONArray();
			if (list!=null && list.size()>0) {
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
	
	@SignValidate
	@RequestMapping("/oracle/data/backuppath")
	public void backuppath(HttpServletRequest request,HttpServletResponse response){
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
	@RequestMapping("/oracle/data/revert")
	public void revert(String filename,String db,HttpServletRequest request,
			HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		boolean result = false;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, filename,db);
		if (!errors.hasErrors()) {
			String backpath = realPathResolver.get(com.jeecms.cms.Constants.BACKUP_PATH);
			String backFilePath = backpath + SLASH +filename;
			try {
				String sql=readFile(backFilePath);
				dataBackMng.executeSQL(sql,com.jeecms.cms.Constants.ONESQL_PREFIX);
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
		body = "{\"result\":"+result+"}";
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/oracle/data/backup")
	public void backup(String tableNames,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, tableNames);
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
			Thread thread =new DateBackupTableThread(file,tableNameArr);
			thread.start();
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/oracle/data/progress")
	public void progress(HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		if (StringUtils.isNotBlank(backup_table)) {
			body = "{\"tablename\":\""+backup_table+"\"}";
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/oracle/data/files")
	public void files(HttpServletRequest request,HttpServletResponse response){
		List<FileWrap> list = resourceMng.listFile(com.jeecms.cms.Constants.BACKUP_PATH, false);
		JSONArray jsonArray = new JSONArray();
		if (list!=null && list.size()>0) {
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
	@RequestMapping("/oracle/data/delete")
	public void delete(String names,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, names);
		if (!errors.hasErrors()) {
			try {
				String[] nameArr = names.split(",");
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
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/oracle/data/origName")
	public void origName(String name,HttpServletRequest request,HttpServletResponse response){
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
	
	@RequestMapping("/oracle/data/rename")
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
	
	@RequestMapping("/oracle/data/export")
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
	
	private boolean validate(String[] names,HttpServletRequest request) {
		if(names!=null&&names.length>0){
			for(String name:names){
				//导出阻止非法获取其他目录文件
				if (!name.contains(com.jeecms.cms.Constants.BACKUP_PATH)||name.contains("../")||name.contains("..\\")) {
					return true;
				}
			}
		}else{
			return true;
		}
		return false;
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
	    if (r != len)
	    //  throw new IOException("<@s.m 'db.filereaderror'/>");
	    bufferedInputStream.close();
	    return new String(bytes,"utf-8");
	}
	
	private class DateBackupTableThread extends Thread{
		private File file;
		private String[] tablenames;
		public DateBackupTableThread(File file, String[] tablenames) {
			super();
			this.file = file;
			this.tablenames = tablenames;
		}
		public void run() {
			FileOutputStream out;
			OutputStreamWriter writer=null;
			try {
				out = new FileOutputStream(file);
				writer = new OutputStreamWriter(out, "utf8");
				//删除外键sql
				for (int i=0;i<tablenames.length;i++) {
					backupDropConstraint(writer,tablenames[i]);
				}
				//备份表结构
				for (int i=0;i<tablenames.length;i++) {
					backup_table=tablenames[i];
					backupTable(writer,tablenames[i]);
					//生成建表脚本中包含约束创建，先删除方便插入数据
					backupDropConstraint(writer,tablenames[i]);
				}
				//备份数据
				for (int i=0;i<tablenames.length;i++) {
					backup_table=tablenames[i];
					backupData(writer,tablenames[i]);
				}
				//重新建立外键约束
				for (int i=0;i<tablenames.length;i++) {
					backupCreateConstraint(writer,tablenames[i]);
				}
				backup_table="finish";
				//writer.append(createSequenceSql());
				writer.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private   String backupTable(OutputStreamWriter writer,String tablename) throws IOException {
			writer.write(createOneTableSql(tablename));
			writer.flush();
			return tablename;
		}
		
		private   String backupCreateConstraint(OutputStreamWriter writer,String tablename) throws IOException {
			writer.write(createCreateConstraintsSql(tablename));
			writer.flush();
			return tablename;
		}
		
		private   String backupDropConstraint(OutputStreamWriter writer,String tablename) throws IOException {
			writer.write(createDropConstraintSql(tablename));
			writer.flush();
			return tablename;
		}
		
		private   String backupData(OutputStreamWriter writer,String tablename) throws IOException {
			writer.write(insertDatasSql(tablename));
			writer.flush();
			return tablename;
		}
		

		private String createOneTableSql(String tablename) {
			StringBuffer buffer = new StringBuffer();
			//创建表
			buffer.append(BR);
			buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX+DROP_TABLE+tablename+BR);
			buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX+dataBackMng.createTableDDL(tablename).trim());
			/*
			//创建索引
			List<String>indexSqls=dataBackMng.createIndexDDL(tablename);
			for(String indexSql:indexSqls){
				buffer.append(Constants.ONESQL_PREFIX+indexSql);
			}
			*/
			return buffer.toString();
		}

		private String createDropConstraintSql(String tablename) {
			StringBuffer buffer = new StringBuffer();
			//删除外键约束，表创建完成后重新创建
			List<String>constraints=dataBackMng.getFkConstraints(tablename);
			for(String constraint:constraints){
				buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX+ALTER_TABLE+tablename+DROP_CONSTRAINT+constraint+BR);
			}
			return buffer.toString();
		}
		
		private String createCreateConstraintsSql(String tablename) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(BR);
			//表创建完成后重新创建外键约束
			List<String>constraints=dataBackMng.getFkConstraints(tablename);
			for(String constraint:constraints){
				buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX+dataBackMng.createFKconstraintDDL(constraint));
			}
			return buffer.toString();
		}
		
		private String insertDatasSql(String tablename) {
			StringBuffer buffer = new StringBuffer();
			Object[][] oneResult;
			buffer.append(BR);
			//插入数据
			//数组结构 [数据][是否clob类型]
			List<Object[][]> results = dataBackMng.createTableData(tablename);
			List<String>columns=dataBackMng.getColumns(tablename);
			for (int rowIndex = 0; rowIndex < results.size(); rowIndex++) {
				// one insert sql
				oneResult = results.get(rowIndex);
				buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX+createOneInsertSql(tablename,columns,oneResult,rowIndex));
			}
			return buffer.toString();
		}

		private String createOneInsertSql( String tablename,List<String>columns,Object[][] oneResult,int rowIndex) {
			StringBuffer buffer = new StringBuffer();
			String clobDeclareName;
			//声明clob变量
			for (int j = 0; j < oneResult.length; j++) {
				if (oneResult[j] != null&&oneResult[j][0] instanceof String&&(Boolean)oneResult[j][1]) {
					buffer.append(DECLARE+SPACE);
					clobDeclareName=CLOB+"_"+rowIndex+"_"+j;
					buffer.append(clobDeclareName+SPACE+CLOB+EQUALS+QUOTES+ StrUtils.replaceString((String) oneResult[j][0])+ QUOTES+BRANCH);
				}
			}
			buffer.append(SPACE+BEGIN);
			buffer.append(SPACE+INSERT_INTO +  tablename);
			/*列信息可以取消
			buffer.append(LEFTBRACE);
			for(int colIndex=0;colIndex<columns.size()-1;colIndex++){
				buffer.append(columns.get(colIndex)+COMMA);
			}
			buffer.append(columns.get(columns.size()-1)+RIGHTBRACE);
			*/
			buffer.append(SPACE + VALUES + LEFTBRACE);
			for (int j = 0; j < oneResult.length; j++) {
				if (oneResult[j] != null) {
					if (oneResult[j][0] instanceof Date) {
						buffer.append(TO_DATE+LEFTBRACE+QUOTES + DateFormatUtils.formatDateTime((Date)oneResult[j][0]) + QUOTES+COMMA+QUOTES+FORMAT_STRING+QUOTES+RIGHTBRACE);
					} else if (oneResult[j][0] instanceof String) {
						if((Boolean)oneResult[j][1]){
							//clob column
							clobDeclareName=CLOB+"_"+rowIndex+"_"+j;
							buffer.append(clobDeclareName);
						}else{
							buffer.append(QUOTES
									+ StrUtils.replaceKeyString((String) oneResult[j][0])
									+ QUOTES);
						}
					} else if (oneResult[j][0] instanceof Boolean) {
						if ((Boolean) oneResult[j][0]) {
							buffer.append(1);
						} else {
							buffer.append(0);
						}
					} else {
						buffer.append(oneResult[j][0]);
					}
				} else {
					buffer.append(oneResult[j][0]);
				}
				buffer.append(COMMA);
			}
			if(buffer.lastIndexOf(COMMA)!=-1)
			buffer = buffer.deleteCharAt(buffer.lastIndexOf(COMMA));
			buffer.append(RIGHTBRACE+BRANCH);
			//buffer.append(BRANCH);
			buffer.append(SPACE+"commit;");
			buffer.append(SPACE+END+BRANCH+BR);
			return buffer.toString();
		}
	}
	
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private CmsOracleDataBackMng dataBackMng;
	@Autowired
	private CmsResourceMng resourceMng;
	@Autowired
	private CmsLogMng cmsLogMng;
}
