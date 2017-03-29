package com.litesuits.common.assist;

import android.test.AndroidTestCase;
import android.text.TextUtils;


/**
 * Created by yanxq on 17/3/29.
 */
public class ReflectTest extends AndroidTestCase{

    public void testReflect() throws Exception {
        StringBuilder builder = new StringBuilder();
        Object result = Reflect.on(builder)
                .call("append","Hello");
        assertTrue(result != null);
        assertEquals("Hello",builder.toString());

        result = Reflect.on(TextUtils.class)
                .find("isEmpty",CharSequence.class)
                .call(new Object[]{"Hi"});
        assertTrue(result != null);
        assertEquals("false", String.valueOf(result));


        result = Reflect.on("android.text.TextUtils")
                .find("isEmpty",CharSequence.class)
                .call(new Object[]{""});
        assertTrue(result != null);
        assertEquals("true",String.valueOf(result));
    }

    public void testGetField() throws Exception {
        Object result = Reflect.on(TextUtils.class)
                .get("CAP_MODE_CHARACTERS");
        assertTrue(result != null);
        assertEquals(4096,result);

        Object tmp = new Object();
        Reflect reflect = Reflect.on(tmp);
        result = Reflect.on(reflect)
                .get("mObj");
        assertEquals(result,tmp);
    }


}