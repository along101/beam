package com.yzl.framework.beam.exception;

import com.yzl.framework.beam.common.BeamMessageConstant;
import org.apache.commons.lang3.StringUtils;

public abstract class BeamAbstractException extends RuntimeException {

    protected BeamMessage beamMessage = BeamMessageConstant.FRAMEWORK_DEFAULT_ERROR;

    public BeamAbstractException() {
        super();
    }

    public BeamAbstractException(BeamMessage beamMessage) {
        super();
        this.beamMessage = beamMessage;
    }

    public BeamAbstractException(String message) {
        super(message);
    }

    public BeamAbstractException(String message, BeamMessage beamMessage) {
        super(message);
        this.beamMessage = beamMessage;
    }

    public BeamAbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeamAbstractException(String message, Throwable cause, BeamMessage beamMessage) {
        super(message, cause);
        this.beamMessage = beamMessage;
    }

    public BeamAbstractException(Throwable cause) {
        super(cause);
    }

    public BeamAbstractException(Throwable cause, BeamMessage beamMessage) {
        super(cause);
        this.beamMessage = beamMessage;
    }


    @Override
    public String getMessage() {
        String message = getOriginMessage();
        return "error_message: " + message + ", status: " + beamMessage.getStatus() + ", error_code: " + beamMessage.getErrorCode();
    }

    public String getOriginMessage() {
        if (StringUtils.isBlank(super.getMessage())) {
            return beamMessage.getMessage();
        }
        return super.getMessage();
    }

    public int getStatus() {
        return beamMessage != null ? beamMessage.getStatus() : 0;
    }

    public int getErrorCode() {
        return beamMessage != null ? beamMessage.getErrorCode() : 0;
    }

    public BeamMessage getBeamMessage() {
        return beamMessage;
    }
}
