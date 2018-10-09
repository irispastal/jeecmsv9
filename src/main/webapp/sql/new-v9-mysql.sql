create table jc_sms(
id int(11) not null auto_increment,
name varchar(255) not null comment '消息服务名称',
access_key_id varchar(255) not null comment 'app id/accessKeyId',
access_key_secret varchar(255) not null comment 'app key/accessKey secret',
template_code varchar(255) not null comment '模板ID',
template_param varchar(255) comment '模板对应键',
interval_time int(11) comment '短信发送间隔时间 0无限制',
interval_unit int(1) comment '间隔时间单位 0秒 1分 2时',
effective_time int(11) comment '短信验证码有效时间 0无限制',
effective_unit int(1) comment '有效时间单位 0秒 1分 2时',
sign_name varchar(255) comment '短信签名(阿里)',
sms_up_extend_code varchar(255) comment '上行短信扩展码,无特殊需要此字段的用户请忽略此字段(阿里)',
out_id varchar(255) comment '外部流水扩展字段',
nation_code varchar(255) comment '区域码(腾讯)',
end_point varchar(255) comment 'SMS服务域名(百度)',
invoke_id varchar(255) comment '发送使用签名的调用ID(百度)',
sms_source tinyint(2) comment 'SMS服务平台1阿里 2腾讯 3百度',
is_code tinyint(1) comment '是否为验证码模板',
create_time datetime comment '创建时间',
random_num int(11) comment '验证码位数',
primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SMS短信服务配置';

create table jc_sms_record(
id int(11) not null auto_increment,
phone varchar(255) comment '电话号码',
send_time datetime comment '发送时间',
send_content varchar(255) comment '发送内容',
sms_id int(11) comment '短信服务id',
primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SMS短信服务记录';

ALTER TABLE jc_config CHANGE COLUMN email_validate validate_type  integer(2) NULL DEFAULT 0 COMMENT '验证类型：0:无  1：邮件验证  2：SMS验证';
ALTER TABLE jc_config ADD COLUMN day_count  integer(10) NULL DEFAULT 0 COMMENT '短信验证 每日验证次数限制';
ALTER TABLE jc_config ADD COLUMN smsid  bigint(20) NULL COMMENT '配置了的短信运营商';
ALTER TABLE jc_sms_record ADD COLUMN site_id  int(11) NULL;
ALTER TABLE jc_sms_record ADD COLUMN user_id  int(11) NULL DEFAULT 1;
ALTER TABLE jc_sms_record ADD COLUMN validate_type  int(2) NULL DEFAULT 0 COMMENT '验证类型  0：未知 1 : 注册验证 2 : 找回密码验证 ';

alter table jc_acquisition add column def_type_img tinyint(1) not null comment '是否默认类型图0：否1：是';
alter table jc_acquisition add column type_img_start varchar(255) comment '类型图开始';
alter table jc_acquisition add column type_img_end varchar(255) comment '类型图结束';
alter table jc_acquisition add column content_page_prefix varchar(255) comment '内容分页地址补全';
alter table jc_acquisition add column content_page_start varchar(255) comment '内容分页开始';
alter table jc_acquisition add column content_page_end varchar(255) comment '内容分页结束';
alter table jc_acquisition add column page_link_start varchar(255) comment '内容分页链接开始';
alter table jc_acquisition add column page_link_end varchar(255) comment '内容分页链接结束';

CREATE TABLE jc_acquisition_replace (
  replace_id int(11) NOT NULL auto_increment,
  acquisition_id int(11) NOT NULL,
  keyword varchar(255) comment '关键词',
  replace_word varchar(255) comment '替换词',
  PRIMARY KEY  (replace_id),
  KEY fk_jc_acquisition_replace (acquisition_id),
  CONSTRAINT fk_jc_acquisition_replace FOREIGN KEY (acquisition_id) REFERENCES jc_acquisition (acquisition_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集内容替换';

CREATE TABLE jc_acquisition_shield (
  shield_id int(11) NOT NULL auto_increment,
  acquisition_id int(11) NOT NULL,
  shield_start varchar(255) comment '屏蔽开始',
  shield_end varchar(255) comment '屏蔽结束',
  PRIMARY KEY  (shield_id),
  KEY fk_jc_acquisition_shield (acquisition_id),
  CONSTRAINT fk_jc_acquisition_shield FOREIGN KEY (acquisition_id) REFERENCES jc_acquisition (acquisition_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集内容屏蔽';


