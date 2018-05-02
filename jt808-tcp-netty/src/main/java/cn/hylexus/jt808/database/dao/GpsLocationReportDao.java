package cn.hylexus.jt808.database.dao;

import cn.hylexus.jt808.database.pojo.GpsLocationReport;

public interface GpsLocationReportDao {
	int save(GpsLocationReport location);
	GpsLocationReport findByPhone(String phone);
}
