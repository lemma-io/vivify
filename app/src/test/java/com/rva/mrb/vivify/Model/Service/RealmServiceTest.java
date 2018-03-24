package com.rva.mrb.vivify.Model.Service;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Data.BaseTestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.rule.PowerMockRule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;



public class RealmServiceTest {

    Realm mockRealm;
    Alarm alarm;

    @Before
    public void setup() {
        RealmConfiguration testConfig = new RealmConfiguration.Builder()
                .inMemory()
                .name("test-realm")
                .build();

        mockRealm = Realm.getInstance(testConfig);

        mockRealm.c
    }

    @Test
    public void shouldBeAbleToGetDefaultInstance() {
        assertThat(Realm.getDefaultInstance(), is(mockRealm));
    }
}