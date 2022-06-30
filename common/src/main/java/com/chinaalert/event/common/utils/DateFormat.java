package com.chinaalert.event.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormat {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateTimeFormatter fmt1=DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static DateTimeFormatter fmt2=DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
	private static DateTimeFormatter fmt3=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	/**
	 * 返回格式为2020-03-24 15:23:36的时间字符串
	 * @param date
	 * @return
	 */
	public static String format(Date date){
		return sdf.format(date);
	}
	public static Date parse(String date) throws ParseException{
		return sdf.parse(date);
	}
	/**
	 * 解析yyyy-MM-dd'T'HH:mm:ss.SSS格式的日期
	 * @param date
	 * @return
	 */
	public static DateTime parseT(String date){
		if(date.length()==25){
			return fmt1.parseDateTime(date);
		}else if(date.length()==21){
			return fmt2.parseDateTime(date);
		}else if(date.length()==19){
			return fmt3.parseDateTime(date);
		}
		return new DateTime();
	}
}
