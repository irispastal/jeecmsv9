alter table jc_api_user_login add active_time timestamp;
EXECUTE sp_addextendedproperty N'MS_Description', N'最后活跃时间', N'user', N'dbo', N'table', N'jc_api_user_login', N'COLUMN', N'active_time';
EXECUTE sp_rename 'jc_user.[is_disabled]' ,'statu';
alter table jc_user alter COLUMN statu tinyint NOT NULL;
Insert into jc_site_attr(site_id,attr_name,attr_value) select site_id ,set@a= 'wxToken','myjcywangluoweixin' from jc_site;
update jo_config set cfg_value=465 where cfg_key='email_port';
CREATE TABLE jc_site_access_count_hour (
  access_count_hour_id int IDENTITY(77,1) NOT NULL,
  hour_pv int NOT NULL DEFAULT 0,
  hour_ip int NOT NULL DEFAULT 0,
  hour_uv int NOT NULL DEFAULT 0,
  access_date date NOT NULL,
  access_hour int NOT NULL DEFAULT 0,
  site_id int NOT NULL DEFAULT 0,
); 
ALTER TABLE  jc_site_access_count_hour ADD CONSTRAINT pk_jc_site_access_count_hour primary key (access_count_hour_id);
CREATE INDEX index_jc_access_count_hour ON jc_site_access_count_hour(access_hour); 
CREATE INDEX index_jc_access_count_hour_date ON jc_site_access_count_hour(access_date); 
CREATE INDEX index_jc_access_count_hour_site ON jc_site_access_count_hour(site_id); 
EXECUTE sp_addextendedproperty N'MS_Description', N'小时数据统计', N'user', N'dbo', N'table', N'jc_site_access_count_hour', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'小时PV', N'user', N'dbo', N'table', N'jc_site_access_count_hour', N'COLUMN', N'hour_pv';
EXECUTE sp_addextendedproperty N'MS_Description', N'小时IP', N'user', N'dbo', N'table', N'jc_site_access_count_hour', N'COLUMN', N'hour_ip';
EXECUTE sp_addextendedproperty N'MS_Description', N'小时访客数', N'user', N'dbo', N'table', N'jc_site_access_count_hour', N'COLUMN', N'hour_uv';
EXECUTE sp_addextendedproperty N'MS_Description', N'站点ID', N'user', N'dbo', N'table', N'jc_site_access_count_hour', N'COLUMN', N'site_id';

Insert into jc_site_attr(site_id,attr_name,attr_value)  select site_id,'contentTotal',count(content_id) from jc_content group by site_id;
Insert into jc_site_attr(site_id,attr_name,attr_value)  select site_id,'commentTotal',count(comment_id) from jc_comment group by site_id;
Insert into jc_site_attr(site_id,attr_name,attr_value)  select site_id,'guestbookTotal',count(guestbook_id) from jc_guestbook group by site_id;
Insert into jc_site_attr(site_id,attr_name,attr_value)  select 1,'memberTotal',count(user_id) from jc_user where is_admin=0;
go
create trigger triggerContentInsert
on jc_content
after insert
as
update jc_site_attr set attr_value=attr_value+1 where attr_name='contentTotal' and site_id= (select site_id from inserted);
go
create trigger triggerContentDelete
on jc_content
after delete
as
update jc_site_attr set attr_value=attr_value-1 where attr_name='contentTotal' and site_id=(select site_id from deleted);
go
create trigger triggerCommentInsert
on jc_comment
after insert
as
update jc_site_attr set attr_value=attr_value+1 where attr_name='commentTotal' and site_id=(select site_id from inserted);
go
create trigger triggerCommentDelete
on jc_comment
after delete  
as
update jc_site_attr set attr_value=attr_value-1 where attr_name='commentTotal' and site_id=(select site_id from deleted);
go
create trigger triggerGuestbookInsert
on jc_guestbook
after insert 
as
update jc_site_attr set attr_value=attr_value+1 where attr_name='guestbookTotal' and site_id=(select site_id from inserted);
go
create trigger triggerGuestbookDelete
on jc_guestbook
after delete   
as
update jc_site_attr set attr_value=attr_value-1 where attr_name='guestbookTotal' and site_id=(select site_id from deleted);
go
create trigger triggerMemberInsert
on jc_user
after insert
as
update jc_site_attr set attr_value=attr_value+1 where attr_name='memberTotal' and site_id=1 and (select is_admin from inserted)=0;
go
create trigger triggerMemberDelete
on jc_user
after delete   
as
update jc_site_attr set attr_value=attr_value-1 where attr_name='memberTotal' and site_id=1 and (select is_admin from deleted)=0;
go
alter table jc_topic add initials varchar(150);
EXECUTE sp_addextendedproperty N'MS_Description', N'首字母拼音简写', N'user', N'dbo', N'table', N'jc_topic', N'COLUMN', N'initials';

CREATE TABLE jc_oss (
  id int IDENTITY(1,1) NOT NULL ,
  app_id varchar(255)  DEFAULT '',
  secret_id varchar(255) NOT NULL DEFAULT '',
  app_key varchar(255) NOT NULL DEFAULT '',
  bucket_name varchar(255) DEFAULT NULL,
  bucket_area varchar(255) DEFAULT '',
  oss_type tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
);
EXECUTE sp_addextendedproperty N'MS_Description', N'云储存', N'user', N'dbo', N'table', N'jc_oss', NULL, NULL;
EXECUTE sp_addextendedproperty N'MS_Description', N'id', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'app_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'secret key', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'secret_id';
EXECUTE sp_addextendedproperty N'MS_Description', N'bucket名', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'bucket_name';
EXECUTE sp_addextendedproperty N'MS_Description', N'地区码', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'bucket_area';
EXECUTE sp_addextendedproperty N'MS_Description', N'存储类型(1腾讯云cos  2阿里云oss  3七牛云)', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'oss_type';

alter table jc_site add oss_id int;
EXECUTE sp_addextendedproperty N'MS_Description', N'图片附件云存储oss', N'user', N'dbo', N'table', N'jc_site', N'COLUMN', N'oss_id';
alter table jc_oss add end_point varchar(255) NOT NULL DEFAULT '';
EXECUTE sp_addextendedproperty N'MS_Description', N'end_point', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'end_point';
alter table jc_oss add access_domain varchar(255) NOT NULL DEFAULT '';
EXECUTE sp_addextendedproperty N'MS_Description', N'访问域名', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'access_domain';
alter table jc_api_account add is_admin tinyint NOT NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'是否默认管理后台API账户', N'user', N'dbo', N'table', N'jc_api_account', N'COLUMN', N'is_admin';
alter table jc_api_account add limit_single_device  tinyint NOT NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'是否限制单设备同时登陆', N'user', N'dbo', N'table', N'jc_api_account', N'COLUMN', N'limit_single_device';
alter table jc_oss add oss_name varchar(255) NOT NULL DEFAULT '';
EXECUTE sp_addextendedproperty N'MS_Description', N'名称', N'user', N'dbo', N'table', N'jc_oss', N'COLUMN', N'oss_name';

INSERT INTO jc_config_attr VALUES (1,'commentOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookOpen','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookNeedLogin','true');
INSERT INTO jc_config_attr VALUES (1,'guestbookDayLimit',0);
INSERT INTO jc_config_attr VALUES (1,'commentDayLimit',0);

alter table jc_user_ext add today_guestbook_total int NOT NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'今日留言数', N'user', N'dbo', N'table', N'jc_user_ext', N'COLUMN', N'today_guestbook_total';
alter table jc_user_ext add today_comment_total int NOT NULL DEFAULT 0;
EXECUTE sp_addextendedproperty N'MS_Description', N'今日评论数', N'user', N'dbo', N'table', N'jc_user_ext', N'COLUMN', N'today_comment_total';

INSERT INTO jc_config_attr VALUES (1,'apiAccountMngPassword','5f4dcc3b5aa765d61d8327deb882cf99');



