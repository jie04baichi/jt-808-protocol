package cn.hylexus.jt808.database.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class GPS {

	private int id;
	
	private int carId;
	
	private String imei;
	
	private String imsi_type;
			
	private Date bindingAt;

	private Date createdAt;

	private String deviceType;
	
	private int storeId;
	
	private int status;
	
}
