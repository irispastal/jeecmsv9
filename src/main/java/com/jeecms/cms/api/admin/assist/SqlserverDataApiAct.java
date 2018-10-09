package com.jeecms.cms.api.admin.assist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import com.jeecms.cms.manager.assist.CmsResourceMng;
import com.jeecms.cms.manager.assist.CmsSqlserverDataBackMng;
import com.jeecms.common.file.FileWrap;
import com.jeecms.common.util.DateUtils;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.util.Zipper;
import com.jeecms.common.util.Zipper.FileEntry;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.web.WebErrors;
@Controller
public class SqlserverDataApiAct {
	private static String SUFFIX = "sql";
	private static String BR = "\r\n";
	private static String SLASH = "/";
	private static String dbXmlFileName = "/WEB-INF/config/jdbc.properties";
	private static String SPACE = " ";
	private static String BRANCH = ";";
	private static String INSERT_INTO = " INSERT INTO ";
	private static String VALUES = "VALUES";
	private static String LEFTBRACE = "(";
	private static String RIGHTBRACE = ")";
	private static String QUOTES = "'";
	private static String COMMA = ",";
	private static final String INVALID_PARAM = "template.invalidParams";
	private static String backup_table;
	private static final Logger log = LoggerFactory.getLogger(CmsResourceApiAct.class);
	
	@RequestMapping("/sqlserver/data/list")
	public void list(HttpServletRequest request,HttpServletResponse response){
		List<String> list = dataBackMng.listTabels();
		JSONArray jsonArray = new JSONArray();
		if (list!=null && list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				jsonArray.put(i,list.get(i));
			}
		}
		String body = jsonArray.toString();
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = Constants.API_MESSAGE_SUCCESS;
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/sqlserver/data/listfields")
	public void fields(String tablename,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message =Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, tablename);
		if (!errors.hasErrors()) {
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
	
	@RequestMapping("/sqlserver/data/listDataBases")
	public void listDataBases(HttpServletRequest request,HttpServletResponse response){
		List<String> list = dataBackMng.listDataBases();
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
	
	@RequestMapping("/sqlserver/data/defaultCatalog")
	public void defaultCatalog(HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		try {
			String defaultCatalog = dataBackMng.getDefaultCatalog();
			body = "{\"defaultCatalog\":\""+defaultCatalog+"\"}";
		} catch (SQLException e) {
			message = Constants.API_MESSAGE_SQL_ERROR;
			code = ResponseCode.API_CODE_SQL_ERROR;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/sqlserver/data/backuppath")
	public void backuppath(HttpServletRequest request,HttpServletResponse response){
		String body = "{\"backuppath\":\""+com.jeecms.cms.Constants.BACKUP_PATH+"\"}";
		String message =Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/sqlserver/data/revert")
	public void revert(String filename,String db,HttpServletRequest request,HttpServletResponse response){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, filename,db);
		if (!errors.hasErrors()) {
			String backpath = realPathResolver.get(com.jeecms.cms.Constants.BACKUP_PATH);
			String backFilePath = backpath + SLASH + filename;
			try {
				String sql = readFile(backFilePath);
				dataBackMng.executeSQL("use [" + db + "]" + BR);
				dataBackMng.executeSQL(sql);
				// 若db发生变化，需要处理jdbc
				String defaultCatalog = dataBackMng.getDefaultCatalog();
				if (!defaultCatalog.equals(db)) {
					String dbXmlPath = realPathResolver.get(dbXmlFileName);
					dbXml(dbXmlPath, defaultCatalog, db);
				}
				body = "{\"backFilePath\":\""+backFilePath+"\"}";
				message = Constants.API_MESSAGE_SUCCESS;
				code = ResponseCode.API_CODE_CALL_SUCCESS;
			} catch (IOException e) {
				message = Constants.API_MESSAGE_FILE_NOT_FOUNT;
				code = ResponseCode.API_CODE_FILE_NOT_FOUNT;
			} catch (Exception e) {
				message = Constants.API_MESSAGE_DB_REVERT_ERROR;
				code = ResponseCode.API_CODE_DB_REVERT_ERROR;
			}
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/sqlserver/data/backup")
	public void backup(String tableNames,HttpServletResponse response,HttpServletRequest request){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_SUCCESS;
		String code = ResponseCode.API_CODE_CALL_SUCCESS;
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
			String backFilePath = backpath + SLASH + dateUtils.getNowString() + "." + SUFFIX;
			File file = new File(backFilePath);
			Thread thread = new DateBackupTableThread(file, tableNameArr);
			thread.start();
			message = Constants.API_MESSAGE_SUCCESS;
			code = ResponseCode.API_CODE_CALL_SUCCESS;
		}
		ApiResponse apiResponse=new ApiResponse(request,body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	@RequestMapping("/sqlserver/data/progress")
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
	
	@RequestMapping("/sqlserver/data/files")
	public void listFiles(HttpServletRequest request,HttpServletResponse response){
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
	@RequestMapping("/sqlserver/data/delete")
	public void delete(String names,HttpServletRequest request,HttpServletResponse response){
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
						cmsLogMng.operating(request, "resource.log.delete", "filename=" + name);
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
	
	@RequestMapping("/sqlserver/data/origName")
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
	
	@RequestMapping("/sqlserver/data/rename")
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
	
	@RequestMapping("/sqlserver/data/export")
	public void export(String names,HttpServletRequest request,HttpServletResponse response){
		WebErrors errors = WebErrors.create(request);
		errors = ApiValidate.validateRequiredParams(request, errors, names);
		if (!errors.hasErrors()) {
			String[] nameArr = names.split(",");
			String backName = "back";
			if (names != null && nameArr.length > 0 && nameArr[0] != null) {
				backName = nameArr[0].substring(nameArr[0].indexOf(com.jeecms.cms.Constants.BACKUP_PATH) 
						+ com.jeecms.cms.Constants.BACKUP_PATH.length() + 1);
			}
			List<FileEntry> fileEntrys = new ArrayList<FileEntry>();
			response.setContentType("application/x-download;charset=UTF-8");
			response.addHeader("Content-disposition", "filename=" + backName + ".zip");
			for (String filename : nameArr) {
				File file = new File(realPathResolver.get(filename));
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
	
	private boolean validate(String[] names, HttpServletRequest request) {
		if (names != null && names.length > 0) {
			for (String name : names) {
				// 导出阻止非法获取其他目录文件
				if (!name.contains( com.jeecms.cms.Constants.BACKUP_PATH) 
						|| name.contains("../") || name.contains("..\\")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	private WebErrors validateDelete(String[] names, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(names, "names", true);
		if (names != null && names.length > 0) {
			for (String name : names) {
				// 导出阻止非法获取其他目录文件
				if (!name.contains("/WEB-INF/backup/") || name.contains("../") || name.contains("..\\")) {
					errors.addErrorString(INVALID_PARAM);
				}
			}
		} else {
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
	
	private class DateBackupTableThread extends Thread {
		private File file;
		private String[] tablenames;

		public DateBackupTableThread(File file, String[] tablenames) {
			this.file = file;
			this.tablenames = tablenames;
		}

		public void run() {
			OutputStreamWriter writer = null;
			try {
				FileOutputStream out = new FileOutputStream(file);
				writer = new OutputStreamWriter(out, "utf8");

				for (int i = 0; i < this.tablenames.length; i++) {
					backup_table = tablenames[i];
					nocheckConstraint(writer, this.tablenames[i]);
				}

				for (int i = 0; i < this.tablenames.length; i++) {
					backup_table = tablenames[i];
					backupTable(writer, this.tablenames[i]);
				}

				for (int i = 0; i < this.tablenames.length; i++) {
					backup_table = tablenames[i];
					checkConstraint(writer, this.tablenames[i]);
				}

				backup_table = "";
				writer.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String nocheckConstraint(OutputStreamWriter writer, String tablename) throws IOException {
			StringBuffer buffer = new StringBuffer();
			buffer.append("ALTER TABLE " + tablename + " NOCHECK CONSTRAINT ALL; " + SqlserverDataApiAct.BR);
			writer.write(buffer.toString());
			writer.flush();
			return tablename;
		}

		private String checkConstraint(OutputStreamWriter writer, String tablename) throws IOException {
			StringBuffer buffer = new StringBuffer();
			buffer.append("ALTER TABLE " + tablename + " CHECK CONSTRAINT ALL; " + SqlserverDataApiAct.BR);
			writer.write(buffer.toString());
			writer.flush();
			return tablename;
		}

		private String backupTable(OutputStreamWriter writer, String tablename) throws IOException {
			String sql = createOneTableSql(tablename);
			writer.write(sql);

			sql = createOneTableConstraintSql(sql, tablename);
			writer.write(sql);
			writer.flush();
			return tablename;
		}

		private String createOneTableSql(String tablename) {
			StringBuffer buffer = new StringBuffer();

			buffer.append(getNoCheckReference(tablename));

			buffer.append(dataBackMng.createTableDDL(tablename));

			List<Object[]> results = dataBackMng.createTableData(tablename);
			List<String> columns = dataBackMng.getColumns(tablename);
			if ((buffer.toString().contains(" IDENTITY")) && (results.size() > 0)) {
				buffer.append("SET IDENTITY_INSERT   " + tablename + " ON" + SqlserverDataApiAct.BR);
			}
			for (int i = 0; i < results.size(); i++) {
				Object[] oneResult = (Object[]) results.get(i);
				buffer.append(createOneInsertSql(tablename, columns, oneResult));
			}
			if ((buffer.toString().contains(" IDENTITY")) && (results.size() > 0)) {
				buffer.append("SET IDENTITY_INSERT  " + tablename + " OFF" + SqlserverDataApiAct.BR);
			}
			return buffer.toString();
		}

		private String createOneTableConstraintSql(String sql, String tablename) {
			StringBuffer buffer = new StringBuffer();

			buffer.append(dataBackMng.createConstraintDDL(sql, tablename));
			return buffer.toString();
		}

		private String createOneInsertSql(String tablename, List<String> columns, Object[] oneResult) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(com.jeecms.cms.Constants.ONESQL_PREFIX + SqlserverDataApiAct.INSERT_INTO + tablename);
			buffer.append(SqlserverDataApiAct.LEFTBRACE);
			for (int colIndex = 0; colIndex < columns.size() - 1; colIndex++) {
				buffer.append("[" + (String) columns.get(colIndex) + "]" + SqlserverDataApiAct.COMMA);
			}
			buffer.append("[" + (String) columns.get(columns.size() - 1) + "]" + SqlserverDataApiAct.RIGHTBRACE);
			buffer.append(SqlserverDataApiAct.SPACE + SqlserverDataApiAct.VALUES + SqlserverDataApiAct.LEFTBRACE);
			for (int j = 0; j < oneResult.length; j++) {
				if (oneResult[j] != null) {
					if ((oneResult[j] instanceof Date))
						buffer.append(SqlserverDataApiAct.QUOTES + oneResult[j] + SqlserverDataApiAct.QUOTES);
					else if ((oneResult[j] instanceof String))
						buffer.append(SqlserverDataApiAct.QUOTES +
								// 整合版
								StrUtils.replaceKeyString(oneResult[j].toString().replace("'", ""))
								+ SqlserverDataApiAct.QUOTES);
					else if ((oneResult[j] instanceof Boolean)) {
						if (((Boolean) oneResult[j]).booleanValue())
							buffer.append(1);
						else
							buffer.append(0);
					} else
						buffer.append(oneResult[j]);
				} else {
					buffer.append(oneResult[j]);
				}
				buffer.append(SqlserverDataApiAct.COMMA);
			}
			if (buffer.lastIndexOf(SqlserverDataApiAct.COMMA) != -1)
				buffer = buffer.deleteCharAt(buffer.lastIndexOf(SqlserverDataApiAct.COMMA));
			buffer.append(RIGHTBRACE + BRANCH + BR);
			return buffer.toString();
		}

		private String getNoCheckReference(String tablename) {
			Map<String, String> refers = dataBackMng.getBeReferForeignKeyFromTable(tablename);
			StringBuffer sqlBuffer = new StringBuffer();
			Iterator<String> keyIt = refers.keySet().iterator();

			if ((refers != null) && (!refers.isEmpty())) {
				while (keyIt.hasNext()) {
					String key = (String) keyIt.next();
					sqlBuffer.append(
							"ALTER TABLE [" + (String) refers.get(key) + "]" + " DROP   CONSTRAINT " + key + BR);
				}
			}

			return sqlBuffer.toString();
		}

	}
	
	@SuppressWarnings("deprecation")
	public void dbXml(String fileName, String oldDbHost, String dbHost) throws Exception {
		String s = FileUtils.readFileToString(new File(fileName));
		s = StringUtils.replace(s, oldDbHost, dbHost);
		FileUtils.writeStringToFile(new File(fileName), s);
	}
	
	private String readFile(String filename) throws IOException {
		File file = new File(filename);
		if (filename == null || filename.equals("")) {
			throw new NullPointerException("<@s.m 'db.fileerror'/>");
		}
		long len = file.length();
		byte[] bytes = new byte[(int) len];
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		int r = bufferedInputStream.read(bytes);
		if (r != len)
			// throw new IOException("<@s.m 'db.filereaderror'/>");
			bufferedInputStream.close();
		return new String(bytes, "utf-8");
	}
	
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private CmsSqlserverDataBackMng dataBackMng;
	@Autowired
	private CmsResourceMng resourceMng;
	@Autowired
	private CmsLogMng cmsLogMng;
}
