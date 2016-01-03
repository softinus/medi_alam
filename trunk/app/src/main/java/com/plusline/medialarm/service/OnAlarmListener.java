package com.plusline.medialarm.service;

import com.plusline.medialarm.type.Alarm;

/**
 * 알람 리스너
 */
public interface OnAlarmListener {
    void onChime(Alarm alarm);
}
