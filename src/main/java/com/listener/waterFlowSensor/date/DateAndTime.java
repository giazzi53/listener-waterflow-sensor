//package com.listener.waterFlowSensor.date;
//
//import java.sql.Timestamp;
//import java.util.Date;
//
//public class DateAndTime {
//	
//	private Date date;
//	
//	private long time;
//	
//	private Timestamp timestamp;
//	
//	public DateAndTime() {
//		this.date = new Date();
//		this.time = this.date.getTime();
//		this.timestamp = new Timestamp(time);
//	}
//
//	public Date getDate() {
//		return date;
//	}
//
//	public void setDate(Date date) {
//		this.date = date;
//	}
//
//	public long getTime() {
//		return time;
//	}
//
//	public void setTime(long time) {
//		this.time = time;
//	}
//
//	public String getTimestamp() {
//		String formatTimestamp = this.timestamp.toString();
//		String newTimestamp = formatTimestamp.substring(0, 10) + " / " + formatTimestamp.substring(11, 19);
//		return newTimestamp;
//	}
//}