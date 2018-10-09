create table jc_sms(
id int IDENTITY(77,1) not null,
name nvarchar(255) not null,
access_key_id nvarchar(255) not null,
access_key_secret nvarchar(255) not null,
template_code nvarchar(255) not null,
template_param nvarchar(255),
interval_time int,
interval_unit int,
effective_time int,
effective_unit int,
sign_name nvarchar(255),
sms_up_extend_code nvarchar(255),
out_id nvarchar(255),
nation_code nvarchar(255),
end_point nvarchar(255),
invoke_id nvarchar(255),
sms_source tinyint,
is_code tinyint,
create_time datetime,
random_num int
);
ALTER TABLE  jc_sms ADD CONSTRAINT pk_jc_sms primary key (id);
EXECUTE sp_addextendedproperty N'MS_Description', N'SMS短信服务配置', N'user', N'dbo', N'table', N'jc_sms', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'消息服务名称', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'name';
EXECUTE sp_addextendedproperty N'MS_Description', N'app id/accessKeyId', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'access_key_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'app key/accessKey secret', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'access_key_secret';
EXECUTE sp_addextendedproperty N'MS_Description', N'模板ID', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'template_code';
EXECUTE sp_addextendedproperty N'MS_Description', N'模板对应键', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'template_param';
EXECUTE sp_addextendedproperty N'MS_Description', N'短信发送间隔时间 0无限制', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'interval_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'间隔时间单位 0秒 1分 2时', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'interval_unit';
EXECUTE sp_addextendedproperty N'MS_Description', N'短信验证码有效时间 0无限制', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'effective_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'有效时间单位 0秒 1分 2时', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'effective_unit';
EXECUTE sp_addextendedproperty N'MS_Description', N'短信签名(阿里)', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'sign_name';
EXECUTE sp_addextendedproperty N'MS_Description', N'上行短信扩展码,无特殊需要此字段的用户请忽略此字段(阿里)', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'sms_up_extend_code';
EXECUTE sp_addextendedproperty N'MS_Description', N'外部流水扩展字段', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'out_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'区域码(腾讯)', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'nation_code';
EXECUTE sp_addextendedproperty N'MS_Description', N'SMS服务域名(百度)', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'end_point';
EXECUTE sp_addextendedproperty N'MS_Description', N'发送使用签名的调用ID(百度)', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'invoke_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'SMS服务平台1阿里 2腾讯 3百度', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'sms_source';
EXECUTE sp_addextendedproperty N'MS_Description', N'是否为验证码模板', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'is_code';
EXECUTE sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'create_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'验证码位数', N'user', N'dbo', N'table', N'jc_sms', N'COLUMN', N'random_num';

create table jc_sms_record(
id int IDENTITY(77,1) not null,
phone nvarchar(255),
send_time datetime,
send_content nvarchar(255),
sms_id int
);
ALTER TABLE jc_sms_record ADD CONSTRAINT pk_jc_sms_record primary key(id);
EXECUTE sp_addextendedproperty N'MS_Description', N'SMS短信服务记录', N'user', N'dbo', N'table', N'jc_sms_record', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'电话号码', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'phone';
EXECUTE sp_addextendedproperty N'MS_Description', N'发送时间', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'send_time';
EXECUTE sp_addextendedproperty N'MS_Description', N'发送内容', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'send_content';
EXECUTE sp_addextendedproperty N'MS_Description', N'短信服务id', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'sms_id';

EXECUTE sp_rename 'jc_config.[email_validate]' ,'validate_type';
alter table jc_config alter COLUMN validate_type integer NOT NULL;

ALTER TABLE jc_config ADD day_count int NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'短信验证 每日验证次数限制', N'user', N'dbo', N'table', N'jc_config', N'COLUMN', N'day_count';
ALTER TABLE jc_config ADD smsid  bigint NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'配置了的短信运营商', N'user', N'dbo', N'table', N'jc_config', N'COLUMN', N'smsid';
ALTER TABLE jc_sms_record ADD site_id  int NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'站点Id', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'site_id';
ALTER TABLE jc_sms_record ADD user_id  int NULL DEFAULT 1;
EXECUTE sp_addextendedproperty N'MS_Description', N'用户Id', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'user_id';
ALTER TABLE jc_sms_record ADD validate_type  int NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'验证类型  0：未知 1 : 注册验证 2 : 找回密码验证', N'user', N'dbo', N'table', N'jc_sms_record', N'COLUMN', N'validate_type';
alter table jc_acquisition add def_type_img tinyint not null default 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'是否默认类型图0：否1：是', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'def_type_img';
alter table jc_acquisition add type_img_start nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'类型图开始', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'type_img_start';
alter table jc_acquisition add type_img_end nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'类型图结束', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'type_img_end';
alter table jc_acquisition add content_page_prefix nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'内容分页地址补全', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'content_page_prefix';
alter table jc_acquisition add content_page_start nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'内容分页开始', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'content_page_start';
alter table jc_acquisition add content_page_end nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'内容分页结束', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'content_page_end';
alter table jc_acquisition add page_link_start nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'内容分页链接开始', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'page_link_start';
alter table jc_acquisition add page_link_end nvarchar(255);
EXECUTE sp_addextendedproperty N'MS_Description', N'内容分页链接结束', N'user', N'dbo', N'table', N'jc_acquisition', N'COLUMN', N'page_link_end';

CREATE TABLE jc_acquisition_replace(
  replace_id int IDENTITY(77,1) NOT NULL,
  acquisition_id int NOT NULL,
  keyword nvarchar(255),
  replace_word nvarchar(255)
);
ALTER TABLE  jc_acquisition_replace ADD CONSTRAINT pk_jc_acquisition_replace primary key (replace_id);
CREATE INDEX index_jc_acquisition_replace_acquisition ON jc_acquisition_replace(acquisition_id);
EXECUTE sp_addextendedproperty N'MS_Description', N'采集内容替换', N'user', N'dbo', N'table', N'jc_acquisition_replace', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'关键词', N'user', N'dbo', N'table', N'jc_acquisition_replace', N'COLUMN', N'keyword';
EXECUTE sp_addextendedproperty N'MS_Description', N'替换词', N'user', N'dbo', N'table', N'jc_acquisition_replace', N'COLUMN', N'replace_word';


CREATE TABLE jc_acquisition_shield (
  shield_id int IDENTITY(77,1) NOT NULL,
  acquisition_id int NOT NULL,
  shield_start varchar(255),
  shield_end varchar(255)
);
ALTER TABLE  jc_acquisition_shield ADD CONSTRAINT pk_jc_acquisition_shield primary key (shield_id);
CREATE INDEX index_jc_acquisition_shield_acquisition ON jc_acquisition_shield(acquisition_id);
EXECUTE sp_addextendedproperty N'MS_Description', N'采集内容屏蔽', N'user', N'dbo', N'table', N'jc_acquisition_shield', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'屏蔽开始', N'user', N'dbo', N'table', N'jc_acquisition_shield', N'COLUMN', N'shield_start';
EXECUTE sp_addextendedproperty N'MS_Description', N'屏蔽结束', N'user', N'dbo', N'table', N'jc_acquisition_shield', N'COLUMN', N'shield_end';
