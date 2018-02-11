package com.yzl.framework.beam.cluster.loadbalance;


import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin loadbalance.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-13
 */
@SpiBinder(name = "roundrobin")
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    protected Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = getRefers();

        int index = getNextPositive();
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> refer = refers.get((i + index) % refers.size());
            if (refer.isAvailable()) {
                return refer;
            }
        }
        return null;
    }

    @Override
    protected List<Refer<T>> doSelectToHolder(Request request) {
        List<Refer<T>> refers = getRefers();
        List<Refer<T>> refersHolder = new ArrayList<>();
        int index = getNextPositive();
        for (int i = 0, count = 0; i < refers.size() && count < MAX_REFER_COUNT; i++) {
            Refer<T> refer = refers.get((i + index) % refers.size());
            if (refer.isAvailable()) {
                refersHolder.add(refer);
                count++;
            }
        }
        return refersHolder;
    }

    // get positive int
    private int getNextPositive() {
        return MathUtil.getPositive(idx.incrementAndGet());
    }

    @Override
    public String getAlgorithm() {
        return "roundrobin";
    }
}
