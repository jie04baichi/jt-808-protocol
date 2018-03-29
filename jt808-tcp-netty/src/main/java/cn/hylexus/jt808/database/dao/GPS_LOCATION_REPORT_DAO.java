package cn.hylexus.jt808.database.dao;

import cn.hylexus.jt808.database.pojo.GPS_LOCATION_REPORT;

public interface GPS_LOCATION_REPORT_DAO {
	int save(GPS_LOCATION_REPORT location);
	GPS_LOCATION_REPORT findByPhone(String phone);
}
