alter table jc_api_user_login add active_time timestamp default sysdate;
COMMENT ON COLUMN jc_api_user_login.active_time is '最后活跃时间';

alter table jc_user rename column is_disabled to statu;
alter table jc_user modify(statu number default 0);
COMMENT ON COLUMN jc_user.statu is '状态 0审核通过  1禁用  2待审核';

insert into jc_site_attr select site_id,'wxToken','myjcywangluoweixin' from jc_site;

update jo_config set cfg_value=465 where cfg_key='email_port';

CREATE TABLE jc_site_access_count_hour (
  access_count_hour_id number(11) NOT NULL,
  hour_pv number(11) DEFAULT '0' NOT NULL,
  hour_ip number(11) DEFAULT '0' NOT NULL,
  hour_uv number(11) DEFAULT '0' NOT NULL,
  access_date timestamp NOT NULL,
  access_hour number(2) DEFAULT '0' NOT NULL,
  site_id number(11) DEFAULT '0' NOT NULL
);
CREATE SEQUENCE S_JC_SITE_ACCESS_COUNT_HOUR start with 1;
COMMENT ON TABLE jc_site_access_count_hour is '小时数据统计';
COMMENT ON COLUMN jc_site_access_count_hour.hour_pv is '小时PV';
COMMENT ON COLUMN jc_site_access_count_hour.hour_ip is '小时IP';
COMMENT ON COLUMN jc_site_access_count_hour.hour_uv is '小时访客数';
COMMENT ON COLUMN jc_site_access_count_hour.site_id is '站点ID';
ALTER TABLE jc_site_access_count_hour ADD CONSTRAINT pk_jc_site_access_count_hour primary key (access_count_hour_id);

Insert into jc_site_attr  select site_id,'contentTotal',count(content_id) from jc_content group by site_id;
Insert into jc_site_attr  select site_id,'commentTotal',count(comment_id) from jc_comment group by site_id;
Insert into jc_site_attr  select site_id,'guestbookTotal',count(guestbook_id) from jc_guestbook group by site_id;

create trigger triggerContentInsert
after insert on jc_content
for each row   
begin
update jc_site_attr set attr_value=attr_value+1 where attr_name='contentTotal' and site_id=:new.site_id;
end;
/
create trigger triggerContentDelete
after delete on jc_content
for each row   
begin
update jc_site_attr set attr_value=attr_value-1 where attr_name='contentTotal' and site_id=:old.site_id;
end;
/
create trigger triggerCommentInsert
after insert on jc_comment
for each row   
begin
update jc_site_attr set attr_value=attr_value+1 where attr_name='commentTotal' and site_id=:new.site_id;
end;
/
create trigger triggerCommentDelete
after delete on jc_comment
for each row   
begin
update jc_site_attr set attr_value=attr_value-1 where attr_name='commentTotal' and site_id=:old.site_id;
end;
/
create trigger triggerGuestbookInsert
after insert on jc_guestbook
for each row   
begin
update jc_site_attr set attr_value=attr_value+1 where attr_name='guestbookTotal' and site_id=:new.site_id;
end;
/
create trigger triggerGuestbookDelete
after delete on jc_guestbook
for each row   
begin
update jc_site_attr set attr_value=attr_value-1 where attr_name='guestbookTotal' and site_id=:old.site_id;
end;
/
create trigger triggerMemberInsert
after insert on jc_user
for each row   
begin
update jc_site_attr set attr_value=attr_value+1 where attr_name='memberTotal' and site_id=1 and :new.is_admin=0;
end;
/
create trigger triggerMemberDelete
after delete on jc_user
for each row   
begin
update jc_site_attr set attr_value=attr_value-1 where attr_name='memberTotal' and site_id=1 and :old.is_admin=0;
end;
/
alter table jc_topic add initials varchar2(150);
COMMENT ON COLUMN jc_topic.initials is '首字母拼音简写';

CREATE TABLE jc_oss (
  id number(11) NOT NULL,
  app_id varchar2(255)  DEFAULT '',
  secret_id varchar2(255) DEFAULT '' NOT NULL,
  app_key varchar2(255) DEFAULT '' NOT NULL,
  bucket_name varchar2(255) DEFAULT NULL,
  bucket_area varchar2(255) DEFAULT '',
  oss_type number(2) DEFAULT 1 NOT NULL
);
CREATE SEQUENCE S_JC_OSS start with 1;
COMMENT ON TABLE jc_oss is 'oss云存储配置';
COMMENT ON COLUMN jc_oss.app_id is 'id';
COMMENT ON COLUMN jc_oss.secret_id is 'secret_id';
COMMENT ON COLUMN jc_oss.app_key is 'secret key';
COMMENT ON COLUMN jc_oss.bucket_name is 'bucket名';
COMMENT ON COLUMN jc_oss.bucket_area is '地区码';
COMMENT ON COLUMN jc_oss.oss_type is '存储类型(1腾讯云cos  2阿里云oss  3七牛云)';
ALTER TABLE jc_oss ADD CONSTRAINT pk_jc_oss primary key (id);

alter table jc_site add oss_id number(11);
COMMENT ON COLUMN jc_site.oss_id is '图片附件云存储oss';

alter table jc_oss add end_point varchar2(255) DEFAULT '';
COMMENT ON COLUMN jc_oss.end_point is 'end_point';

alter table jc_oss add access_domain varchar2(255) DEFAULT '';
COMMENT ON COLUMN jc_oss.access_domain is '访问域名';

alter table jc_api_account add is_admin number(1) DEFAULT 0 NOT NULL;
COMMENT ON COLUMN jc_api_account.is_admin is '是否默认管理后台API账户';

alter table jc_api_account add limit_single_device number(1) DEFAULT 0 NOT NULL;
COMMENT ON COLUMN jc_api_account.limit_single_device is '是否限制单设备同时登陆';

alter table jc_oss add oss_name varchar2(255) DEFAULT '' NOT NULL;
COMMENT ON COLUMN jc_oss.oss_name is '名称';

INSERT INTO jc_config_attr VALUES (1,'commentOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookNeedLogin','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookDayLimit',0);
INSERT INTO jc_config_attr VALUES (1,'commentDayLimit',0);

alter table jc_user_ext add today_guestbook_total number(11) DEFAULT 0 NOT NULL;
COMMENT ON COLUMN jc_user_ext.today_guestbook_total is '今日留言数';

alter table jc_user_ext add today_comment_total number(11) DEFAULT 0 NOT NULL;
COMMENT ON COLUMN jc_user_ext.today_comment_total is '今日评论数';

INSERT INTO jc_config_attr VALUES (1,'apiAccountMngPassword','5f4dcc3b5aa765d61d8327deb882cf99');
INSERT INTO jc_api_account VALUES (1, '1580387213331704', 'Sd6qkHm9o4LaVluYRX5pUFyNuiu2a8oi', 'S9u978Q31NGPGc5H', 0, 'X83yESM9iShLxfwS', 1, 0);
