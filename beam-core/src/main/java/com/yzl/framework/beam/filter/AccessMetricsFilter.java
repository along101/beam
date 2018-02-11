package com.yzl.framework.beam.filter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.yzl.framework.beam.common.Info;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.metric.MetricContext;
import com.yzl.framework.beam.metric.TagName;
import com.yzl.framework.beam.rpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@SpiBinder(name = "metrics")
@Slf4j
public class AccessMetricsFilter extends AbstractFilter {

    public static final String concurrenceName = Info.getInstance().getMetricPrefix() + ".concurrence";
    public static final String concurrenceHistogramName = Info.getInstance().getMetricPrefix() + ".concurrenceHistogram";
    public static final String timeName = Info.getInstance().getMetricPrefix() + ".time";
    public static final String countName = Info.getInstance().getMetricPrefix() + ".count";
    public static final String requestPayloadName = Info.getInstance().getMetricPrefix() + ".payload.request";
    public static final String responsePayloadName = Info.getInstance().getMetricPrefix() + ".payload.response";
    public static final String responseHistogram = Info.getInstance().getMetricPrefix() + ".payload.response.histrogram";

    private MetricRegistry metricRegistry = MetricContext.getMetricRegistry();

    @Override
    public boolean defaultEnable() {
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(Caller caller, Request request) {
        Counter concurrenceCounter = concurrenceCounter(caller, request);
        Histogram concurrenceHistogram = concurrenceHistogram(caller, request);
        concurrenceCounter.inc();
        concurrenceHistogram.update(concurrenceCounter.getCount());

        long t = System.currentTimeMillis();
        Response response = null;
        try {
            response = caller.call(request);
            return response;
        } finally {
            long requestTime = System.currentTimeMillis() - t;

            concurrenceCounter.dec();
            concurrenceHistogram.update(concurrenceCounter.getCount());
            time(caller, request, response, requestTime);
            count(caller, request, response);
            payload(caller, request, response);
        }
    }

    protected Counter concurrenceCounter(Caller caller, Request request) {
        TagName concurrenceTag = TagName.name(concurrenceName);
        tagRequest(concurrenceTag, caller, request);
        return metricRegistry.counter(concurrenceTag.toString());
    }

    protected Histogram concurrenceHistogram(Caller caller, Request request) {
        TagName histogramTag = TagName.name(concurrenceHistogramName);
        tagRequest(histogramTag, caller, request);
        return metricRegistry.histogram(histogramTag.toString());
    }

    protected void time(Caller caller, Request request, Response response, long requestTime) {
        TagName timeTag = TagName.name(timeName);
        tagRequest(timeTag, caller, request);
        tagResponse(timeTag, caller, response);
        Timer timer = metricRegistry.timer(timeTag.toString());
        timer.update(requestTime, TimeUnit.MILLISECONDS);
    }

    protected void count(Caller caller, Request request, Response response) {
        TagName countTag = TagName.name(countName);
        tagRequest(countTag, caller, request);
        tagResponse(countTag, caller, response);
        Counter counter = metricRegistry.counter(countTag.toString());
        counter.inc();
    }

    protected void payload(Caller caller, Request request, Response response) {
        Histogram requestPayloadHistogram = payloadHistogram(caller, request, response, requestPayloadName);
        Histogram responsePayloadHistogram = payloadHistogram(caller, request, response, responsePayloadName);
        Long requestPayload = getRequestPayloadSize(request);
        Long responsePayload = getResponsePayloadSize(response);
        //server端特殊处理，TODO 修改过滤器实现
        if (isServer(caller)) {
            if (requestPayload != null) {
                requestPayloadHistogram.update(requestPayload);
            }
            RpcContext.getContext().putAttribute(responseHistogram, responsePayloadHistogram);
        } else {
            if (requestPayload != null) {
                requestPayloadHistogram.update(requestPayload);
            }
            if (responsePayload != null) {
                responsePayloadHistogram.update(responsePayload);
            }
        }
    }

    protected Histogram payloadHistogram(Caller caller, Request request, Response response, String name) {
        TagName timeTag = TagName.name(name);
        tagRequest(timeTag, caller, request);
        tagResponse(timeTag, caller, response);
        return metricRegistry.histogram(timeTag.toString());
    }

    protected TagName tagRequest(TagName tagName, Caller caller, Request request) {
        return tagName.addTag("nodeType", getNodeType(caller))
                .addTag("interface", request.getInterfaceName())
                .addTag("method", request.getMethodName())
                .addTag("parameterTypes", getParameterTypes(request))
                .addTag("version", getInterfaceVersion(caller))
                .addTag("protocol", getProtocol(caller))
                .addTag("beamVersion", getBeamVersion(caller))
                .addTag("host_port", getHostPort(caller))
                .addTag("appId", getAppId())
                .addTag("retries", getRetries(request));
    }

    protected String getHostPort(Caller caller) {
        if (caller instanceof Provider) {
            URL url = ((Provider<?>) caller).getServiceUrl();
            return url.getHost() + "_" + url.getPort();
        }
        return null;
    }

    protected TagName tagResponse(TagName tagName, Caller caller, Response response) {
        return tagName.addTag("status", getStatusCode(caller, response));
    }
}
