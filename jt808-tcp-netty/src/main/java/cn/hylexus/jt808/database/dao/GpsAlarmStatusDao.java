package cn.hylexus.jt808.database.dao;

import cn.hylexus.jt808.database.pojo.GpsAlarmStatus;

public interface GpsAlarmStatusDao {
	int save(GpsAlarmStatus alarm);
	GpsAlarmStatus findByPhone(String phone);
}
