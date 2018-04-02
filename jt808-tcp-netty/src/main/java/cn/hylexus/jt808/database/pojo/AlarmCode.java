package cn.hylexus.jt808.database.pojo;

public class AlarmCode {

	public static final String OFFALARM = "OFFALARM";
	public static final String COLLISIONALARM = "COLLISIONALARM";
	public static final String ROLLALARM = "ROLLALARM";
	public static final String SHAKEALARM = "SHAKEALARM";
	public static final String OFFLINEALARM = "OFFLINEALARM";
	public static final String ILLEGALALARM = "ILLEGALALARM";
	public static final String UNVOLTAGEALARM = "UNVOLTAGEALARM";
	public static final String OVERSPEEDALARM = "OVERSPEEDALARM";
	public static final String FENCESENTER = "FENCESENTER";
	public static final String FENCESEXIT = "FENCESEXIT";
	
	
	public static String getAlarmType(String code) {
		switch (code) {
		case "OFFALARM":
			return "断电报警";
		case "COLLISIONALARM":
			return "碰撞报警";
		case "ROLLALARM":
			return "侧翻报警";
		case "SHAKEALARM":
			return "震动报警";
		case "OFFLINEALARM":
			return "设备掉线";
		case "ILLEGALALARM ":
			return "非法行驶";
		case "UNVOLTAGEALARM":
			return "防盗电池欠压报警";
		case "OVERSPEEDALARM":
			return "超速报警";
		case "FENCESENTER":
			return "进围栏报警";
		case "FENCESEXIT":
			return "出围栏报警";
		}
		return "";
	}
}
