package cn.hylexus.jt808.database.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class GpsRegisterInfo {
	/*
	   phone                varchar(16) not null,
	   imsi                 varchar(16) not null,
	   imsi_type            varchar(16),
	   make_brand           varchar(8),
	   province_id          int,
	   city_id              int,
	   outer_color          int,
	   license              varchar(16),
	   register_status      int comment '1:已注册,-1已解绑',
	   auth_token           varchar(32),
	   */
	private int ID;
	private String phone;
	private String imsi;
	private String imsi_type;
	private String make_brand;
	private int province_id;
	private int city_id;
	private int outer_color;
	private String license;
	private int register_status;
	private String auth_token;
	private Date create_time;
}
