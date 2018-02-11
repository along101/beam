package com.yzl.framework.beam.cluster.loadbalance;


import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {
    public static final int MAX_REFER_COUNT = 10;

    volatile private AtomicReference<List<Refer<T>>> refers = new AtomicReference<>();

    @Override
    public void onRefresh(List<Refer<T>> refers) {
        // 只能引用替换，不能进行refers update
        this.refers.set(refers);
    }

    @Override
    public Refer select(Request request) {
        List<Refer<T>> refers = this.refers.get();

        Refer<T> refer = null;
        if (refers.size() > 1) {
            refer = doSelect(request);

        } else if (refers.size() == 1) {
            refer = refers.get(0).isAvailable() ? refers.get(0) : null;
        }

        if (refer != null) {
            return refer;
        }
        throw new BeamServiceException(this.getClass().getSimpleName() + " No available refers for call ");
    }

    @Override
    public List<Refer<T>> selectToHolder(Request request) {
        List<Refer<T>> refers = this.refers.get();

        List<Refer<T>> refersHolder = Collections.emptyList();
        if (refers == null) {
            throw new BeamServiceException(this.getClass().getSimpleName()
                    + " No available refers for call : refers_size= 0 ");
        }
        if (refers.size() > 1) {
            refersHolder = doSelectToHolder(request);

        } else if (refers.size() == 1 && refers.get(0).isAvailable()) {
            refersHolder = Collections.singletonList(refers.get(0));
        }
        if (refersHolder.isEmpty()) {
            throw new BeamServiceException(this.getClass().getSimpleName()
                    + " No available refers for call : refers_size=" + refers.size());
        }
        return refersHolder;
    }

    protected List<Refer<T>> getRefers() {
        return refers.get();
    }

    protected abstract Refer<T> doSelect(Request request);

    protected abstract List<Refer<T>> doSelectToHolder(Request request);
}
