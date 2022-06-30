package com.chinaalert.event.transmit.domain.vo;

import java.util.List;

/**
 * date 2022/1/13 15:23
 * @version V1.0
 */
public class CandidateVo {
    //通知人
    private String candidate;
    //通知方式
    private List<String> notification_types;

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public List<String> getNotification_types() {
        return notification_types;
    }

    public void setNotification_types(List<String> notification_types) {
        this.notification_types = notification_types;
    }
}
