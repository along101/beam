package com.yzl.framework.beam.cluster.loadbalance;

import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpiBinder(name = "random")
public class RandomLoadBalance<T> extends AbstractLoadBalance<T> {

    @Override
    protected Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = getRefers();

        int idx = ThreadLocalRandom.current().nextInt();
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> ref = refers.get((i + idx) % refers.size());
            if (ref.isAvailable()) {
                return ref;
            }
        }
        return null;
    }

    @Override
    protected List<Refer<T>> doSelectToHolder(Request request) {
        List<Refer<T>> refers = getRefers();
        List<Refer<T>> refersHolder = new ArrayList<>();
        int idx = ThreadLocalRandom.current().nextInt();
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> refer = refers.get((i + idx) % refers.size());
            if (refer.isAvailable()) {
                refersHolder.add(refer);
            }
        }
        return refersHolder;
    }

    @Override
    public String getAlgorithm() {
        return "random";
    }
}
