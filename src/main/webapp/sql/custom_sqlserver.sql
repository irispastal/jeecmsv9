CREATE TABLE jc_custom_form (
  form_id int IDENTITY(77,1) NOT NULL,
  form_name nvarchar(100) NOT NULL DEFAULT '',
  priority int NOT NULL default 10,
  is_member_meun tinyint NOT NULL default 0,
  create_time datetime NOT NULL,
  tpl_submit_url nvarchar(100),
  tpl_view_url nvarchar(100),
  start_time datetime default NULL,
  end_time datetime default NULL,
  all_site tinyint NOT NULL default 0,
  enable tinyint NOT NULL default 0,
  day_limit int NOT NULL default 0,
  site_id int NOT NULL,
  user_id int NOT NULL,
  workflow_id int default NULL
);
ALTER TABLE  jc_custom_form ADD CONSTRAINT pk_jc_custom_form primary key (form_id);
CREATE INDEX index_jc_custom_form_user ON jc_custom_form(user_id); 
CREATE INDEX index_jc_custom_form_site ON jc_custom_form(site_id); 
EXECUTE sp_addextendedproperty N'MS_Description', N'自定义表单', N'user', N'dbo', N'table', N'jc_custom_form', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'表单名', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'form_name';
EXECUTE sp_addextendedproperty N'MS_Description', N'排序', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'priority';
EXECUTE sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'create_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否会员菜单', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'is_member_meun';
EXECUTE sp_addextendedproperty N'MS_Description', N'模板提交路径', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'tpl_submit_url';
EXECUTE sp_addextendedproperty N'MS_Description', N'模板列表路径', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'tpl_view_url';
EXECUTE sp_addextendedproperty N'MS_Description', N'表单发起时间', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'start_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'表单结束时间', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'end_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否全站:0否 1是', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'all_site';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否启用:0否 1是', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'enable';
EXECUTE sp_addextendedproperty N'MS_Description', N'每日限制提交数', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'day_limit';
EXECUTE sp_addextendedproperty N'MS_Description', N'站点ID', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'site_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'用户ID', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'user_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'工作流id', N'user', N'dbo', N'table', N'jc_custom_form', N'COLUMN', N'workflow_id';


CREATE TABLE jc_custom_form_filed(
  filed_id int IDENTITY(77,1) NOT NULL,
  form_id int NOT NULL,
  field nvarchar(50) NOT NULL,
  label nvarchar(100) NOT NULL,
  priority int NOT NULL default 10,
  def_value nvarchar(255) default NULL,
  opt_value nvarchar(255) default NULL,
  text_size nvarchar(20) default NULL,
  description nvarchar(255) default NULL,
  data_type int NOT NULL default 1,
  is_display tinyint NOT NULL default 1,
  is_required tinyint NOT NULL default 0
);
ALTER TABLE  jc_custom_form_filed ADD CONSTRAINT pk_jc_custom_form_filed primary key (form_id);
CREATE INDEX index_jc_custom_form_filed_form ON jc_custom_form_filed(form_id); 
EXECUTE sp_addextendedproperty N'MS_Description', N'自定义表单字段表', N'user', N'dbo', N'table', N'jc_custom_form_filed', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'字段', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'field';
EXECUTE sp_addextendedproperty N'MS_Description', N'字段名', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'label';
EXECUTE sp_addextendedproperty N'MS_Description', N'排序', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'priority';
EXECUTE sp_addextendedproperty N'MS_Description', N'默认值', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'def_value';
EXECUTE sp_addextendedproperty N'MS_Description', N'可选项', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'opt_value';
EXECUTE sp_addextendedproperty N'MS_Description', N'长度', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'text_size';
EXECUTE sp_addextendedproperty N'MS_Description', N'描述', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'description';
EXECUTE sp_addextendedproperty N'MS_Description', N'数据类型', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'data_type';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否在记录列表中显示', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'is_display';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否必填项', N'user', N'dbo', N'table', N'jc_custom_form_filed', N'COLUMN', N'is_required';

CREATE TABLE jc_custom_record(
  record_id int IDENTITY(77,1) NOT NULL,
  form_id int NOT NULL,
  status int NOT NULL default 0,
  create_time datetime NOT NULL,
  site_id int NOT NULL,
  user_id int default NULL
);
ALTER TABLE  jc_custom_record ADD CONSTRAINT pk_jc_custom_record primary key (record_id);
CREATE INDEX index_jc_custom_record_form ON jc_custom_record(form_id);
CREATE INDEX index_jc_custom_record_site ON jc_custom_record(site_id);
CREATE INDEX index_jc_custom_record_user ON jc_custom_record(user_id);
EXECUTE sp_addextendedproperty N'MS_Description', N'自定义表单记录表', N'user', N'dbo', N'table', N'jc_custom_record', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'状态 0:未审核 1:审核中 2:已终审', N'user', N'dbo', N'table', N'jc_custom_record', N'COLUMN', N'status';
EXECUTE sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'jc_custom_record', N'COLUMN', N'create_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'站点ID', N'user', N'dbo', N'table', N'jc_custom_record', N'COLUMN', N'site_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'用户ID', N'user', N'dbo', N'table', N'jc_custom_record', N'COLUMN', N'user_id';

CREATE TABLE jc_custom_record_attr(
  record_id int NOT NULL,
  filed_name nvarchar(30) NOT NULL,
  filed_value nvarchar(255) default NULL
);
CREATE INDEX index_jc_custom_record_attr_record ON jc_custom_record_attr(record_id);
EXECUTE sp_addextendedproperty N'MS_Description', N'自定义表单记录属性表', N'user', N'dbo', N'table', N'jc_custom_record_attr', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'字段名', N'user', N'dbo', N'table', N'jc_custom_record_attr', N'COLUMN', N'filed_name';
EXECUTE sp_addextendedproperty N'MS_Description', N'字段值', N'user', N'dbo', N'table', N'jc_custom_record_attr', N'COLUMN', N'filed_value';

CREATE TABLE jc_custom_record_check(
  record_id int NOT NULL,
  check_step smallint NOT NULL default 0,
  check_opinion nvarchar(255) default NULL,
  is_rejected smallint NOT NULL default 0,
  reviewer int default NULL,
  check_date datetime default NULL
);
CREATE INDEX index_jc_custom_record_check_record ON jc_custom_record_check(record_id);
CREATE INDEX index_jc_custom_record_check_reviewer ON jc_custom_record_check(reviewer);
EXECUTE sp_addextendedproperty N'MS_Description', N'自定义表单记录审核表', N'user', N'dbo', N'table', N'jc_custom_record_check', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'审核步数', N'user', N'dbo', N'table', N'jc_custom_record_check', N'COLUMN', N'check_step';
EXECUTE sp_addextendedproperty N'MS_Description', N'审核意见', N'user', N'dbo', N'table', N'jc_custom_record_check', N'COLUMN', N'check_opinion';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否退回', N'user', N'dbo', N'table', N'jc_custom_record_check', N'COLUMN', N'is_rejected';
EXECUTE sp_addextendedproperty N'MS_Description', N'终审者', N'user', N'dbo', N'table', N'jc_custom_record_check', N'COLUMN', N'reviewer';
EXECUTE sp_addextendedproperty N'MS_Description', N'终审时间', N'user', N'dbo', N'table', N'jc_custom_record_check', N'COLUMN', N'check_date';