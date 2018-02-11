package com.yzl.framework.beam.core;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.exception.BeamFrameworkException;

public class BinderSupporter {

    public static <T> T newInstance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new BeamFrameworkException(String.format("Can not create instance by class %s error.", clazz), e);
        }
    }

    public static <T> T generate(Class<T> clazz) {
        BinderDefine<?> binderDefine = BinderDefines.getInstance().getBinderDefine(clazz, BeamConstants.DEFAULT_BINDER_NAME);
        if (binderDefine == null) {
            return null;
        }
        return (T) newInstance(binderDefine.getBinderClass());
    }

    public static <T> T generate(Class<T> clazz, String name) {
        BinderDefine<?> binderDefine = BinderDefines.getInstance().getBinderDefine(clazz, name);
        if (binderDefine == null) {
            return null;
        }
        return (T) newInstance(binderDefine.getBinderClass());
    }

}
