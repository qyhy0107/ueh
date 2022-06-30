package com.chinaalert.event.common.domain;

/**
 * 组件类型
 * @author hy
 */
public enum ProbeType {
	PROBE((byte) 1),DISPATCHER((byte)5),HANDLER((byte)6),DELIVERY((byte)7),UNKNOW((byte)10);

	private byte type;

	ProbeType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static ProbeType get(byte type) {
		for (ProbeType value : values()) {
			if (value.type == type) {
				return value;
			}
		}
		return null;
	}
}