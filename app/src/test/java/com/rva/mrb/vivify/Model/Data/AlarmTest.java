package com.rva.mrb.vivify.Model.Data;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;


public class AlarmTest {

    private Alarm alarm;
    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);

        alarm = new Alarm();
        alarm.setTime(calendar.getTime());
        alarm.setAlarmLabel("Test Alarm");
        alarm.setMediaType(MediaType.DEFAULT_TYPE);
    }

    @Test
    public void getDaysOfWeek_repeatAlarmOnWeekdays() throws Exception {
        boolean sundayCb = false;
        boolean mondayCb = true;
        boolean tuesdayCb = true;
        boolean wednesdayCb = true;
        boolean thursdayCb = true;
        boolean fridayCb = true;
        boolean saturdayCb = false;

        int weekDaysOnly = 0;

        if (sundayCb)
            weekDaysOnly = weekDaysOnly | Alarm.SUNDAY;
        if (mondayCb)
            weekDaysOnly = weekDaysOnly | Alarm.MONDAY;
        if (tuesdayCb)
            weekDaysOnly = weekDaysOnly | Alarm.TUESDAY;
        if (wednesdayCb)
            weekDaysOnly = weekDaysOnly | Alarm.WEDNESDAY;
        if (thursdayCb)
            weekDaysOnly = weekDaysOnly | Alarm.THURSDAY;
        if (fridayCb)
            weekDaysOnly = weekDaysOnly | Alarm.FRIDAY;
        if (saturdayCb)
            weekDaysOnly = weekDaysOnly | Alarm.SATURDAY;

        alarm.setDaysOfWeek(Integer.toBinaryString(weekDaysOnly));
        assertEquals(62, alarm.getDecDaysOfWeek());
    }

    @Test
    public void getDaysOfWeek_repeatAlarmOnWeekends() throws Exception {
        int WeekendsOnlyAlarm = 65;
        String setAlarmOnWeekendsOnly = "1000001";
        alarm.setDaysOfWeek(setAlarmOnWeekendsOnly);
        assertEquals(WeekendsOnlyAlarm, alarm.getDecDaysOfWeek());
    }

    @Test
    public void getNextDayEnabled_returnDaysUntilNextSetAlarmDay() {
        String setAlarmOnWeekdaysOnly = "0111110";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarm.getTime());
        calendar.add(Calendar.DATE, -1);

        alarm.setTime(calendar.getTime());
        alarm.setDaysOfWeek(setAlarmOnWeekdaysOnly);
        int nextAvailableDay;

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.FRIDAY)
            nextAvailableDay = 3;
        else if (dayOfWeek == Calendar.SATURDAY)
            nextAvailableDay = 2;
        else
            nextAvailableDay = 1;

        assertEquals(nextAvailableDay, alarm.getNextDayEnabled());
    }

    @Test
    public void updateTime_returnUpdatedAlarmDate() throws Exception {
        alarm.setDaysOfWeek("1111111");

        Calendar oldAlarm = Calendar.getInstance();
        oldAlarm.setTime(alarm.getTime());

        if (alarm.getCal().before(Calendar.getInstance())) {
            oldAlarm.add(Calendar.DATE, 1);
        }

        assertEquals(oldAlarm.getTime(), alarm.updateTime());
    }
}