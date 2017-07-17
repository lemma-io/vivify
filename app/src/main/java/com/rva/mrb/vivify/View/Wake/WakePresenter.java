package com.rva.mrb.vivify.View.Wake;

import com.rva.mrb.vivify.BasePresenter;
import com.rva.mrb.vivify.Model.Data.Alarm;

public interface WakePresenter extends BasePresenter<WakeView> {
    public Alarm getAlarmById(String alarmId);
}
