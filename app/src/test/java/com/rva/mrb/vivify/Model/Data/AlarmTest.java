package com.rva.mrb.vivify.Model.Data;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;


public class AlarmTest {

    private Alarm alarm;
    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        alarm = new Alarm();
        alarm.setTime(calendar.getTime());
        alarm.setAlarmLabel("Test Alarm");
        alarm.setMediaType(MediaType.DEFAULT_TYPE);
    }

    @Test
    public void getAlarmLabel() throws Exception {
    }

    @Test
    public void getmWakeTime() throws Exception {
    }

    @Test
    public void getDaysOfWeek() throws Exception {
    }

    @Test
    public void setDaysOfWeek() throws Exception {
    }

}