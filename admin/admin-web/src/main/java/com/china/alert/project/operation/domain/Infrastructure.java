package com.china.alert.project.operation.domain;

import com.china.alert.framework.aspectj.lang.annotation.Excel;
import com.china.alert.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Infrastructure extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 时间
     */
    @Excel(name = "时间")
    private String clock;

    /**
     * 统计数量
     */
    @Excel(name = "统计数值")
    private String value;

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("clock", getClock())
                .append("value", getValue())
                .toString();
    }
}
