package com.yzl.framework.beam.exception;

import com.yzl.framework.beam.common.BeamMessageConstant;

public class BeamBizException extends BeamAbstractException {

    public BeamBizException() {
        super(BeamMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public BeamBizException(BeamMessage beamMessage) {
        super(beamMessage);
    }

    public BeamBizException(String message) {
        super(message, BeamMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public BeamBizException(String message, BeamMessage beamMessage) {
        super(message, beamMessage);
    }

    public BeamBizException(String message, Throwable cause) {
        super(message, cause, BeamMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public BeamBizException(String message, Throwable cause, BeamMessage beamMessage) {
        super(message, cause, beamMessage);
    }

    public BeamBizException(Throwable cause) {
        super(cause, BeamMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public BeamBizException(Throwable cause, BeamMessage beamMessage) {
        super(cause, beamMessage);
    }
}
