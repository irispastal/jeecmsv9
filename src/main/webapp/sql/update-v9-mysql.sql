alter table jc_api_user_login add column active_time timestamp   COMMENT '最后活跃时间';
alter table jc_user CHANGE is_disabled  statu tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0审核通过  1禁用  2待审核';
Insert into jc_site_attr  select site_id,"wxToken","myjcywangluoweixin" from jc_site;
update jo_config set cfg_value=465 where cfg_key="email_port";
CREATE TABLE jc_site_access_count_hour (
  access_count_hour_id int(11) NOT NULL AUTO_INCREMENT,
  hour_pv int(11) NOT NULL DEFAULT '0' COMMENT '小时PV',
  hour_ip int(11) NOT NULL DEFAULT '0' COMMENT '小时IP',
  hour_uv int(11) NOT NULL DEFAULT '0' COMMENT '小时访客数',
  access_date date NOT NULL,
  access_hour int(2) NOT NULL DEFAULT '0',
  site_id int(11) NOT NULL DEFAULT '0' COMMENT '站点ID',
  PRIMARY KEY (access_count_hour_id),
  KEY index_jc_access_count_hour (access_hour),
  KEY index_jc_access_count_hour_date (access_date),
  KEY index_jc_access_count_hour_site (site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='小时数据统计';

Insert into jc_site_attr  select site_id,"contentTotal",count(content_id) from jc_content group by site_id;
Insert into jc_site_attr  select site_id,"commentTotal",count(comment_id) from jc_comment group by site_id;
Insert into jc_site_attr  select site_id,"guestbookTotal",count(guestbook_id) from jc_guestbook group by site_id;
Insert into jc_site_attr  select 1,"memberTotal",count(user_id) from jc_user where is_admin=0;
create trigger triggerContentInsert after insert on jc_content for each row     update jc_site_attr set attr_value=attr_value+1 where attr_name="contentTotal" and site_id=new.site_id;
create trigger triggerContentDelete after delete on jc_content for each row    update jc_site_attr set attr_value=attr_value-1 where attr_name="contentTotal" and site_id=old.site_id;
create trigger triggerCommentInsert after insert on jc_comment for each row    update jc_site_attr set attr_value=attr_value+1 where attr_name="commentTotal" and site_id=new.site_id;
create trigger triggerCommentDelete after delete on jc_comment for each row    update jc_site_attr set attr_value=attr_value-1 where attr_name="commentTotal" and site_id=old.site_id;
create trigger triggerGuestbookInsert after insert on jc_guestbook for each row    update jc_site_attr set attr_value=attr_value+1 where attr_name="guestbookTotal" and site_id=new.site_id;
create trigger triggerGuestbookDelete after delete on jc_guestbook for each row    update jc_site_attr set attr_value=attr_value-1 where attr_name="guestbookTotal" and site_id=old.site_id;
create trigger triggerMemberInsert after insert on jc_user for each row    update jc_site_attr set attr_value=attr_value+1 where attr_name="memberTotal" and site_id=1 and new.is_admin=0;
create trigger triggerMemberDelete after delete on jc_user for each row    update jc_site_attr set attr_value=attr_value-1 where attr_name="memberTotal" and site_id=1 and old.is_admin=0;

alter table jc_topic add column initials varchar(150)   COMMENT '首字母拼音简写';
CREATE TABLE jc_oss (
  id int(11) NOT NULL AUTO_INCREMENT,
  app_id varchar(255)  DEFAULT '' COMMENT 'id',
  secret_id varchar(255) NOT NULL DEFAULT '' COMMENT 'secret_id',
  app_key varchar(255) NOT NULL DEFAULT '' COMMENT 'secret key',
  bucket_name varchar(255) DEFAULT NULL COMMENT 'bucket名',
  bucket_area varchar(255) DEFAULT '' COMMENT '地区码',
  oss_type tinyint(2) NOT NULL DEFAULT 1 COMMENT '存储类型(1腾讯云cos  2阿里云oss  3七牛云)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='oss云存储配置';
alter table jc_site add column oss_id int(11)   COMMENT '图片附件云存储oss';

alter table jc_oss add column end_point varchar(255) DEFAULT '' COMMENT 'end_point';
alter table jc_oss add column access_domain varchar(255) DEFAULT '' COMMENT '访问域名';

alter table jc_api_account add column is_admin tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认管理后台API账户';
alter table jc_api_account add column limit_single_device  tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否限制单设备同时登陆';


alter table jc_oss add column oss_name varchar(255) NOT NULL DEFAULT '' COMMENT '名称';

INSERT INTO jc_config_attr VALUES (1,'commentOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookNeedLogin','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookDayLimit',0);
INSERT INTO jc_config_attr VALUES (1,'commentDayLimit',0);
alter table jc_user_ext add column today_guestbook_total int(11) NOT NULL DEFAULT 0 COMMENT '今日留言数';
alter table jc_user_ext add column today_comment_total int(11) NOT NULL DEFAULT 0 COMMENT '今日评论数';

INSERT INTO jc_config_attr VALUES (1,'apiAccountMngPassword','5f4dcc3b5aa765d61d8327deb882cf99');
INSERT INTO jc_api_account VALUES (1, '1580387213331704', 'Sd6qkHm9o4LaVluYRX5pUFyNuiu2a8oi', 'S9u978Q31NGPGc5H', 0, 'X83yESM9iShLxfwS', 1, 0);
