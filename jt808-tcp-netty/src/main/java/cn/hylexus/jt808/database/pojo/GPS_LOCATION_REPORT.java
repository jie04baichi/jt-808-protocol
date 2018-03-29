package cn.hylexus.jt808.database.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class GPS_LOCATION_REPORT{
	private int ID;
	private String phone;
	private int alarm_field;
	private int status_field;
	private int latitude;
	private int longitude;
	private int elevation;
	private int speed;
	private int direction;
	private Date loc_time;
	private Date create_time;
	
}
