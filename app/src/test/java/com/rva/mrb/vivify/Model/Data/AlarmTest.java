package com.rva.mrb.vivify.Model.Data;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;


public class AlarmTest {

    private final static String REPEAT_WEEKDAYS_ONLY_BIN = "0111110";
    private final static String REPEAT_WEEKENDS_ONLY_BIN = "1000001";
    private final static String REPEAT_EVERYDAY_BIN = "1111111";

    private final static int REPEAT_WEEKDAYS_ONLY_DEC = 62;
    private final static int REPEAT_WEEKENDS_ONLY_DEC = 65;

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
        assertEquals(REPEAT_WEEKDAYS_ONLY_DEC, alarm.getDecDaysOfWeek());
    }

    @Test
    public void getDaysOfWeek_repeatAlarmOnWeekends() throws Exception {
        alarm.setDaysOfWeek(REPEAT_WEEKENDS_ONLY_BIN);
        assertEquals(REPEAT_WEEKENDS_ONLY_DEC, alarm.getDecDaysOfWeek());
    }

    @Test
    public void getNextDayEnabled_returnDaysUntilNextSetAlarmDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarm.getTime());
        calendar.add(Calendar.DATE, -1);

        alarm.setTime(calendar.getTime());
        alarm.setDaysOfWeek(REPEAT_WEEKDAYS_ONLY_BIN);
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
        alarm.setDaysOfWeek(REPEAT_EVERYDAY_BIN);

        Calendar oldAlarm = Calendar.getInstance();
        oldAlarm.setTime(alarm.getTime());

        if (alarm.getCalendar().before(Calendar.getInstance())) {
            oldAlarm.add(Calendar.DATE, 1);
        }

        assertEquals(oldAlarm.getTime(), alarm.updateTime());
    }
}