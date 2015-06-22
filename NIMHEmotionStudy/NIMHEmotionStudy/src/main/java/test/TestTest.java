package test;

import android.content.Context;
import android.test.*;
import android.test.mock.MockContext;

import junit.framework.Assert;

import java.lang.Exception;

public class TestTest extends AndroidTestCase {
    Context context;

            public void setUp() throws Exception {
                super.setUp();

                context = new MockContext();

                setContext(context);
            }


            public void testTest() {
                Assert.assertEquals(true, true);
            }

}