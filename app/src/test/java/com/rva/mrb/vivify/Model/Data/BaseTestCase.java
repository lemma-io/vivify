package com.rva.mrb.vivify.Model.Data;

import org.junit.Before;
import org.mockito.MockitoAnnotations;


public class BaseTestCase {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
