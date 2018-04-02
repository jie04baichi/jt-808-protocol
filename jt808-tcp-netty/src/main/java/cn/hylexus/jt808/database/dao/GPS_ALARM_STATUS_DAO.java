package cn.hylexus.jt808.database.dao;

import cn.hylexus.jt808.database.pojo.GPS_ALARM_STATUS;

public interface GPS_ALARM_STATUS_DAO {
	int save(GPS_ALARM_STATUS alarm);
	GPS_ALARM_STATUS findByPhone(String phone);
}
