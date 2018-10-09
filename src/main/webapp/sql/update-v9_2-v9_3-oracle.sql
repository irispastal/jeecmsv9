
create table jc_sms(
	id number(11) not null,
	name varchar2(255) not null,
	access_key_id varchar2(255) not null,
	access_key_secret varchar2(255) not null,
	template_code varchar2(255) not null,
	template_param varchar2(255),
	interval_time number(11),
	interval_unit number(1),
	effective_time number(11),
	effective_unit number(1),
	sign_name varchar2(255),
	sms_up_extend_code varchar2(255),
	out_id varchar2(255),
	nation_code varchar2(255),
	end_point varchar2(255),
	invoke_id varchar2(255),
	sms_source number(2),
	is_code number(1),
	create_time date,
	random_num number(11)
);
CREATE SEQUENCE S_JC_SMS  start with 1;
COMMENT ON TABLE jc_sms is 'SMS短信服务配置';	
COMMENT ON COLUMN jc_sms.name is '消息服务名称';
COMMENT ON COLUMN jc_sms.access_key_id is 'app id/accessKeyId';
COMMENT ON COLUMN jc_sms.access_key_secret is 'app key/accessKey secret';
COMMENT ON COLUMN jc_sms.template_code is '模板ID';
COMMENT ON COLUMN jc_sms.template_param is '模板对应键';
COMMENT ON COLUMN jc_sms.interval_time is '短信发送间隔时间 0无限制';
COMMENT ON COLUMN jc_sms.interval_unit is '间隔时间单位 0秒 1分 2时';
COMMENT ON COLUMN jc_sms.effective_time is '短信验证码有效时间 0无限制';
COMMENT ON COLUMN jc_sms.effective_unit is '有效时间单位 0秒 1分 2时';
COMMENT ON COLUMN jc_sms.sign_name is '短信签名(阿里)';
COMMENT ON COLUMN jc_sms.sms_up_extend_code is '上行短信扩展码,无特殊需要此字段的用户请忽略此字段(阿里)';
COMMENT ON COLUMN jc_sms.out_id is '外部流水扩展字段';
COMMENT ON COLUMN jc_sms.nation_code is '区域码(腾讯)';
COMMENT ON COLUMN jc_sms.end_point is 'SMS服务域名(百度)';
COMMENT ON COLUMN jc_sms.invoke_id is '发送使用签名的调用ID(百度)';
COMMENT ON COLUMN jc_sms.sms_source is 'SMS服务平台1阿里 2腾讯 3百度';
COMMENT ON COLUMN jc_sms.is_code is '是否为验证码模板';
COMMENT ON COLUMN jc_sms.create_time is '创建时间';
COMMENT ON COLUMN jc_sms.random_num is '验证码位数';
ALTER TABLE jc_sms ADD CONSTRAINT pk_jc_sms primary key (id);

create table jc_sms_record(
	id number(11) not null,
	phone varchar2(255),
	send_time date,
	send_content varchar2(255),
	sms_id number(11)
);
CREATE SEQUENCE S_JC_SMS_RECORD  start with 1;
COMMENT ON TABLE jc_sms_record is 'SMS短信服务记录';	
COMMENT ON COLUMN jc_sms_record.phone is '电话号码';
COMMENT ON COLUMN jc_sms_record.send_time is '发送时间';
COMMENT ON COLUMN jc_sms_record.send_content is '发送内容';
COMMENT ON COLUMN jc_sms_record.sms_id is '短信服务id';
ALTER TABLE jc_sms_record ADD CONSTRAINT pk_jc_sms_record primary key (id);

alter table jc_config rename column email_validate to validate_type;
alter table jc_config modify (validate_type number(2));
ALTER TABLE jc_config ADD day_count  number(10);
COMMENT ON COLUMN jc_config.day_count is '短信验证 每日验证次数限制';
ALTER TABLE jc_config ADD smsid number(20);
COMMENT ON COLUMN jc_config.smsid is '配置了的短信运营商';
ALTER TABLE jc_sms_record ADD site_id  number(11);
COMMENT ON COLUMN jc_sms_record.site_id is '站点Id';
ALTER TABLE jc_sms_record ADD user_id  number(11);
COMMENT ON COLUMN jc_sms_record.user_id is '用户Id';
ALTER TABLE jc_sms_record ADD validate_type number(2);
COMMENT ON COLUMN jc_sms_record.validate_type is '验证类型  0：未知 1 : 注册验证 2 : 找回密码验证';
alter table jc_acquisition add def_type_img number(1);
COMMENT ON COLUMN jc_acquisition.def_type_img is '是否默认类型图0：否1：是';
alter table jc_acquisition add type_img_start varchar2(255);
COMMENT ON COLUMN jc_acquisition.type_img_start is '类型图开始';
alter table jc_acquisition add type_img_end varchar2(255);
COMMENT ON COLUMN jc_acquisition.type_img_end is '类型图结束';
alter table jc_acquisition add content_page_prefix varchar2(255);
COMMENT ON COLUMN jc_acquisition.content_page_prefix is '内容分页地址补全';
alter table jc_acquisition add content_page_start varchar2(255);
COMMENT ON COLUMN jc_acquisition.content_page_start is '内容分页开始';
alter table jc_acquisition add content_page_end varchar2(255);
COMMENT ON COLUMN jc_acquisition.content_page_end is '内容分页结束';
alter table jc_acquisition add page_link_start varchar2(255);
COMMENT ON COLUMN jc_acquisition.page_link_start is '内容分页链接开始';
alter table jc_acquisition add page_link_end varchar2(255);
COMMENT ON COLUMN jc_acquisition.page_link_end is '内容分页链接结束';

CREATE TABLE jc_acquisition_replace (
  replace_id number(11) NOT NULL,
  acquisition_id number(11) NOT NULL,
  keyword varchar2(255),
  replace_word varchar2(255)
);
CREATE SEQUENCE S_JC_ACQUISITION_REPLACE  start with 1;
COMMENT ON TABLE jc_acquisition_replace is '采集内容替换';
COMMENT ON COLUMN jc_acquisition_replace.keyword is '关键词';
COMMENT ON COLUMN jc_acquisition_replace.replace_word is '替换词';
ALTER TABLE jc_acquisition_replace ADD CONSTRAINT pk_jc_acquisition_replace primary key (replace_id);
ALTER TABLE jc_acquisition_replace ADD CONSTRAINT FK_JC_ACQUISITION_REPLACE FOREIGN KEY (acquisition_id) REFERENCES JC_ACQUISITION (acquisition_id);

CREATE TABLE jc_acquisition_shield (
  shield_id number(11) NOT NULL,
  acquisition_id number(11) NOT NULL,
  shield_start varchar2(255),
  shield_end varchar2(255)
);
CREATE SEQUENCE S_JC_ACQUISITION_SHIELD  start with 1;
COMMENT ON TABLE jc_acquisition_shield is '采集内容屏蔽';
COMMENT ON COLUMN jc_acquisition_shield.shield_start is '屏蔽开始';
COMMENT ON COLUMN jc_acquisition_shield.shield_end is '屏蔽结束';
ALTER TABLE jc_acquisition_shield ADD CONSTRAINT pk_jc_acquisition_shield primary key (shield_id);
ALTER TABLE jc_acquisition