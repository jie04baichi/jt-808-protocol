package cn.hylexus.jt808.database.dao;

import java.util.List;
import java.util.Map;

import cn.hylexus.jt808.database.pojo.GPS;

public interface GPSDao {

	GPS findByImei(String imei);
}
