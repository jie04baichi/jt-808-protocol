package cn.hylexus.jt808.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public final static String salt = "sdfgsgks456456jdfn7&%^@ksa3541654161sdfc^%&%^";

	/**
	 * 27 md5���ܲ���������128λ��bit����mac 28 ��128bit Macת����16���ƴ��� 29
	 * 
	 * @param strSrc
	 *            30
	 * @param key
	 *            31
	 * @return 32
	 */
	public static String MD5Encode(String strSrc) {
		return MD5Encode(strSrc, "");
	}

	/**
	 * 27 md5���ܲ���������128λ��bit����mac 28 ��128bit Macת����16���ƴ��� 29
	 * 
	 * @param strSrc
	 *            30
	 * @param key
	 *            31
	 * @return 32
	 */
	public static String MD5Encode(String strSrc, String key) {

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes("UTF8"));
			StringBuilder result = new StringBuilder(32);
			byte[] temp;
			temp = md5.digest(key.getBytes("UTF8"));
			for (int i = 0; i < temp.length; i++) {
				result.append(Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	public static void main(String[] args) throws Exception {
		String aaa = "wtjn168";
		String mac128byte = MD5Encode(aaa, "");
		System.out.println("md5���ܽ��32 bit------------->:" + mac128byte);
	}

}
