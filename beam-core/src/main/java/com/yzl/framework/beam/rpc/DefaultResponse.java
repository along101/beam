package com.yzl.framework.beam.rpc;

import com.yzl.framework.beam.exception.BeamServiceException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultResponse implements Response, Serializable {
    private static final long serialVersionUID = 4281186647291615871L;

    private int code = -1;
    private Object value;
    private Exception exception;
    private long requestId;
    private long processTime;
    private int timeout;

    private Map<String, String> attachments = new HashMap<>();

    public DefaultResponse() {
    }

    public DefaultResponse(long requestId) {
        this.requestId = requestId;
    }

    public Object getValue() {
        if (exception != null) {
            throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new BeamServiceException(
                    exception.getMessage(), exception);
        }
        return value;
    }

    @Override
    public void setAttachment(String key, String value) {
        if (this.attachments == null) {
            this.attachments = new HashMap<>();
        }
        this.attachments.put(key, value);
    }

}
