package cn.hylexus.jt808.database.pojo;

import java.util.Date;

import lombok.Data;
@Data
public class GpsAlarmStatus {
	/*
	   ID                   int not null,
	   phone                varchar(16),
	   alarm_code           varchar(16),
	   latitude             int,
	   longitude            int,
	   poi                  text,
	   alarm_time           date,
	   */
	private int ID;
	//private String phone;
	private int car_id;
	private int store_id;
	private String alarm_code;
	private int latitude;
	private int longitude;
	private String poi;
	private Date alarm_time;
}
