package cn.hylexus.jt808.database.dao;

import cn.hylexus.jt808.database.pojo.GpsRegisterInfo;

public interface GpsRegisterInfoDao {

	int save(GpsRegisterInfo registerInfo);
	GpsRegisterInfo findByImsi(String imsi);
	GpsRegisterInfo findByPhone(String phone);
	int deleteByphone(String phone);
}
