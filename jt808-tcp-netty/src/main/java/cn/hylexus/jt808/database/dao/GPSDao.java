package cn.hylexus.jt808.database.dao;

import java.util.List;
import java.util.Map;

import cn.hylexus.jt808.database.pojo.GPS;

public interface GPSDao {

	int save(GPS gps);
	GPS findByImei(String imei);
	GPS findByPhone(String phone);
	GPS findByCarId(int carId);
	List<GPS> findByCarIds(List<Integer> ids);
	List<GPS> findByStoreId(int storeId);
	int updateByCarId(Map<String, Object> params);
	int updateStatusByImei(Map<String, Object> params);
	int deleteByCarId(int carId);
	int findCount();
}
