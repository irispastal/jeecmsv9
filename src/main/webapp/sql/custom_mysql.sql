
CREATE TABLE `jc_custom_form` (
  `form_id` int(11) NOT NULL auto_increment,
  `form_name` varchar(100) NOT NULL COMMENT '表单名',
  `priority` int(11) NOT NULL default '10' COMMENT '排序',
  `is_member_meun` tinyint(1) NOT NULL default '0' COMMENT '是否会员菜单',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tpl_submit_url` varchar(100) COMMENT '模板提交路径',
  `tpl_view_url` varchar(100) COMMENT '模板列表路径',
  `start_time` datetime default NULL COMMENT '表单发起时间',
  `end_time` datetime default NULL COMMENT '表单结束时间',
  `all_site` tinyint(1) NOT NULL default '0' COMMENT '是否全站:0否 1是',
  `enable` tinyint(1) NOT NULL default '0' COMMENT '是否启用:0否 1是',
  `day_limit` int(11) NOT NULL default '0' COMMENT '每日限制提交数',
  `site_id` int(11) NOT NULL COMMENT '站点ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `workflow_id` int(11) default NULL COMMENT '工作流id',
  PRIMARY KEY  (`form_id`),
  KEY `fk_jc_custom_form_site` (`site_id`),
  KEY `fk_jc_custom_form_user` (`user_id`),
  CONSTRAINT `fk_jc_custom_form_site` FOREIGN KEY (`site_id`) REFERENCES `jc_site` (`site_id`),
  CONSTRAINT `fk_jc_custom_form_user` FOREIGN KEY (`user_id`) REFERENCES `jc_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义表单';


CREATE TABLE `jc_custom_form_filed` (
  `filed_id` int(11) NOT NULL auto_increment,
  `form_id` int(11) NOT NULL,
  `field` varchar(50) NOT NULL COMMENT '字段',
  `label` varchar(100) NOT NULL COMMENT '字段名',
  `priority` int(11) NOT NULL default '10' COMMENT '排序',
  `def_value` varchar(255) default NULL COMMENT '默认值',
  `opt_value` varchar(255) default NULL COMMENT '可选项',
  `text_size` varchar(20) default NULL COMMENT '长度',
  `description` varchar(255) default NULL COMMENT '描述',
  `data_type` int(11) NOT NULL default '1' COMMENT '数据类型',
  `is_display` tinyint(1) NOT NULL default '1' COMMENT '是否在记录列表中显示',
  `is_required` tinyint(1) NOT NULL default '0' COMMENT '是否必填项',
  PRIMARY KEY  (`filed_id`),
  KEY `fk_jc_filed_form` (`form_id`),
  CONSTRAINT `fk_jc_filed_form` FOREIGN KEY (`form_id`) REFERENCES `jc_custom_form` (`form_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义表单字段表';


CREATE TABLE `jc_custom_record` (
  `record_id` int(11) NOT NULL auto_increment,
  `form_id` int(11) NOT NULL,
  `status` int(11) NOT NULL default '0' COMMENT '状态 0:未审核 1:审核中 2:已终审',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `site_id` int(11) NOT NULL COMMENT '站点ID',
  `user_id` int(11) default NULL COMMENT '用户ID',
  PRIMARY KEY  (`record_id`),
  KEY `fk_jc_custom_record_form` (`form_id`),
  KEY `fk_jc_custom_record_site` (`site_id`),
  KEY `fk_jc_custom_record_user` (`user_id`),
  CONSTRAINT `fk_jc_custom_record_form` FOREIGN KEY (`form_id`) REFERENCES `jc_custom_form` (`form_id`),
  CONSTRAINT `fk_jc_custom_record_site` FOREIGN KEY (`site_id`) REFERENCES `jc_site` (`site_id`),
  CONSTRAINT `fk_jc_custom_record_user` FOREIGN KEY (`user_id`) REFERENCES `jc_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义表单记录表';


CREATE TABLE `jc_custom_record_attr` (
  `record_id` int(11) NOT NULL,
  `filed_name` varchar(30) NOT NULL COMMENT '字段名',
  `filed_value` varchar(255) default NULL COMMENT '字段值',
  KEY `fk_jc_attr_record` (`record_id`),
  CONSTRAINT `fk_jc_attr_record` FOREIGN KEY (`record_id`) REFERENCES `jc_custom_record` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义表单记录属性表';


CREATE TABLE `jc_custom_record_check` (
  `record_id` int(11) NOT NULL,
  `check_step` tinyint(4) NOT NULL default '0' COMMENT '审核步数',
  `check_opinion` varchar(255) default NULL COMMENT '审核意见',
  `is_rejected` tinyint(1) NOT NULL default '0' COMMENT '是否退回',
  `reviewer` int(11) default NULL COMMENT '终审者',
  `check_date` datetime default NULL COMMENT '终审时间',
  PRIMARY KEY  (`record_id`),
  KEY `fk_jc_custom_record_check` (`reviewer`),
  CONSTRAINT `fk_jc_custom_record` FOREIGN KEY (`record_id`) REFERENCES `jc_custom_record` (`record_id`),
  CONSTRAINT `fk_jc_custom_record_check_user` FOREIGN KEY (`reviewer`) REFERENCES `jc_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义表单记录审核表';