
CREATE TABLE jc_custom_form(
	form_id number(11) NOT NULL,
	form_name varchar2(100) NOT NULL,
	priority number(11) default 10 NOT NULL,
	is_member_meun number(1) default 0 NOT NULL,
	create_time date NOT NULL,
	tpl_submit_url varchar2(100) NOT NULL,
	tpl_view_url varchar2(100) NOT NULL,
	start_time date default NULL,
	end_time date default NULL,
	all_site number(1) default 0  NOT NULL,
	enable number(1) default 0  NOT NULL,
	day_limit number(11) default 0  NOT NULL,
	site_id number(11) NOT NULL,
	user_id number(11) NOT NULL,
	workflow_id number(11) default NULL  
);
CREATE SEQUENCE S_JC_CUSTOM_FORM  start with 1;
COMMENT ON TABLE jc_custom_form is '自定义表单';	
COMMENT ON COLUMN jc_custom_form.form_name is '表单名';
COMMENT ON COLUMN jc_custom_form.priority is '排序';
COMMENT ON COLUMN jc_custom_form.is_member_meun is '是否会员菜单';
COMMENT ON COLUMN jc_custom_form.tpl_submit_url is '模板提交路径';
COMMENT ON COLUMN jc_custom_form.tpl_view_url is '模板列表路径';
COMMENT ON COLUMN jc_custom_form.start_time is '表单发起时间';
COMMENT ON COLUMN jc_custom_form.end_time is '表单结束时间';
COMMENT ON COLUMN jc_custom_form.all_site is '是否全站:0否 1是';
COMMENT ON COLUMN jc_custom_form.enable is '是否启用:0否 1是';
COMMENT ON COLUMN jc_custom_form.day_limit is '每日限制提交数';
ALTER TABLE jc_custom_form ADD CONSTRAINT pk_jc_custom_form primary key (form_id);
ALTER TABLE jc_custom_form ADD CONSTRAINT FK_JC_CUSTOM_FORM_SITE FOREIGN KEY (site_id) REFERENCES JC_SITE (site_id);
ALTER TABLE jc_custom_form ADD CONSTRAINT FK_JC_CUSTOM_FORM_USER FOREIGN KEY (user_id) REFERENCES JC_USER (user_id);
ALTER TABLE jc_custom_form ADD CONSTRAINT FK_JC_CUSTOM_FORM_WORKFLOW FOREIGN KEY (workflow_id) REFERENCES JC_WORKFLOW (workflow_id);
	
	
CREATE TABLE jc_custom_form_filed (
	filed_id number(11) NOT NULL,
	form_id number(11) NOT NULL,
	field varchar2(50) NOT NULL,
	label varchar2(100) NOT NULL,
	priority number(11) default 10 NOT NULL,
	def_value varchar2(255) default NULL,
	opt_value varchar2(255) default NULL,
	text_size varchar2(20) default NULL,
	description varchar2(255) default NULL,
	data_type number(11) default 1 NOT NULL,
	is_display number(1) default 1 NOT NULL,
	is_required number(1) default 0 NOT NULL
);
CREATE SEQUENCE S_JC_CUSTOM_FORM_FILED start with 1;
COMMENT ON TABLE jc_custom_form_filed is '自定义表单字段表';
COMMENT ON COLUMN jc_custom_form_filed.field is '字段';
COMMENT ON COLUMN jc_custom_form_filed.label is '字段名';
COMMENT ON COLUMN jc_custom_form_filed.priority is '排序';
COMMENT ON COLUMN jc_custom_form_filed.def_value is '默认值';
COMMENT ON COLUMN jc_custom_form_filed.opt_value is '可选项';
COMMENT ON COLUMN jc_custom_form_filed.text_size is '长度';
COMMENT ON COLUMN jc_custom_form_filed.description is '描述';
COMMENT ON COLUMN jc_custom_form_filed.data_type is '数据类型';
COMMENT ON COLUMN jc_custom_form_filed.is_display is '是否在记录列表中显示';	
COMMENT ON COLUMN jc_custom_form_filed.is_required is '是否必填项';
ALTER TABLE jc_custom_form_filed ADD CONSTRAINT pk_jc_custom_form_filed primary key (filed_id);
ALTER TABLE jc_custom_form_filed ADD CONSTRAINT FK_JC_CUSTOM_FORM_FILED_FORM FOREIGN KEY (form_id) REFERENCES JC_CUSTOM_FORM (form_id);
	
CREATE TABLE jc_custom_record (
	record_id number(11) NOT NULL,
	form_id number(11) NOT NULL,
	status number(11) default 0 NOT NULL,
	create_time date NOT NULL,
	site_id number(11) NOT NULL,
	user_id number(11) default NULL
);
CREATE SEQUENCE S_JC_CUSTOM_RECORD  start with 1;
COMMENT ON TABLE jc_custom_record is '自定义表单记录表';
COMMENT ON COLUMN jc_custom_record.status is '状态 0:未审核 1:审核中 2:已终审';
ALTER TABLE jc_custom_record ADD CONSTRAINT pk_jc_custom_record primary key (record_id);
ALTER TABLE jc_custom_record ADD CONSTRAINT FK_JC_CUSTOM_RECORD_FORM FOREIGN KEY (form_id) REFERENCES JC_CUSTOM_FORM (form_id);
ALTER TABLE jc_custom_record ADD CONSTRAINT FK_JC_CUSTOM_RECORD_SITE FOREIGN KEY (site_id) REFERENCES JC_SITE (site_id);
ALTER TABLE jc_custom_record ADD CONSTRAINT FK_JC_CUSTOM_RECORD_USER FOREIGN KEY (user_id) REFERENCES JC_USER (user_id);

CREATE TABLE jc_custom_record_attr (	
	record_id number(11) NOT NULL,
	filed_name varchar2(30) NOT NULL,
	filed_value varchar2(255) default NULL
);
COMMENT ON TABLE jc_custom_record_attr is '自定义表单记录属性表';	
COMMENT ON COLUMN jc_custom_record_attr.filed_name is '字段名';
COMMENT ON COLUMN jc_custom_record_attr.filed_value is '字段值';
ALTER TABLE jc_custom_record_attr ADD CONSTRAINT pk_jc_custom_record_attr primary key (record_id);

CREATE TABLE jc_custom_record_check (	
	record_id number(11) NOT NULL,
	check_step number(4) default 0 NOT NULL,
	check_opinion varchar2(255) default NULL,
	is_rejected number(1) default 0 NOT NULL,
	reviewer number(11) default NULL,
	check_date date default NULL
);
COMMENT ON TABLE jc_custom_record_check is '自定义表单记录审核表';	
COMMENT ON COLUMN jc_custom_record_check.check_step is '审核步数';
COMMENT ON COLUMN jc_custom_record_check.check_opinion is '审核意见';
COMMENT ON COLUMN jc_custom_record_check.is_rejected is '是否退回';
COMMENT ON COLUMN jc_custom_record_check.reviewer is '终审者';
COMMENT ON COLUMN jc_custom_record_check.check_date is '终审时间';
ALTER TABLE jc_custom_record_check ADD CONSTRAINT pk_jc_custom_record_check primary key (record_id);


