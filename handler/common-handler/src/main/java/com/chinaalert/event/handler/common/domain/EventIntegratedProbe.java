package com.chinaalert.event.handler.common.domain;

import com.chinaalert.event.common.domain.BaseBean;

public class EventIntegratedProbe extends BaseBean{
	/**
	 * 事件源名称
	 */
	private String name;
	/**
	 * 事件源类型，目前包括ZABBIX，SYSLOG，SNMPTRAP
	 */
	private String sourceType;
	/**
	 * ProbeKey可以由前端生成或者后端生成
	 */
	private String probeKey;
	/**
	 * 字段映射，格式参考PlatformProbeColumnMapping.java
	 */
	private String columnMapping;
	/**
	 * 级别映射，格式参考PlatformProbeSeverityMapping.java
	 */
	private String severityMapping;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getProbeKey() {
		return probeKey;
	}
	public void setProbeKey(String probeKey) {
		this.probeKey = probeKey;
	}
	public String getColumnMapping() {
		return columnMapping;
	}
	public void setColumnMapping(String columnMapping) {
		this.columnMapping = columnMapping;
	}
	public String getSeverityMapping() {
		return severityMapping;
	}
	public void setSeverityMapping(String severityMapping) {
		this.severityMapping = severityMapping;
	}
}
