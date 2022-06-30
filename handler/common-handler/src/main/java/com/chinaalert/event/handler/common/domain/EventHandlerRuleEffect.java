package com.chinaalert.event.handler.common.domain;

/**
 * 事件处理策略结果表达式，适用于压缩、分组、升降级
 * @author q
 *
 */
public class EventHandlerRuleEffect{
	/**
	 * 生效字段名称
	 */
	private String effectColumn;
	/**
	 * 生效字段数据类型
	 */
	private String effectDataType;
	/**
	 * 生效方式：counter计数，newest最新,fill填充，minus降级，plus升级
	 */
	private String effectType;
	/**
	 * 生效值，effectType为填充时则填充值
	 */
	private String effectValue;
	
	public String getEffectColumn() {
		return effectColumn;
	}
	public void setEffectColumn(String effectColumn) {
		this.effectColumn = effectColumn;
	}
	public String getEffectDataType() {
		return effectDataType;
	}
	public void setEffectDataType(String effectDataType) {
		this.effectDataType = effectDataType;
	}
	public String getEffectType() {
		return effectType;
	}
	public void setEffectType(String effectType) {
		this.effectType = effectType;
	}
	public String getEffectValue() {
		return effectValue;
	}
	public void setEffectValue(String effectValue) {
		this.effectValue = effectValue;
	}
	@Override
	public String toString() {
		return "EventHandlerRuleEffect [effectColumn=" + effectColumn + ", effectDataType=" + effectDataType
				+ ", effectType=" + effectType + ", effectValue=" + effectValue + "]";
	}
}
