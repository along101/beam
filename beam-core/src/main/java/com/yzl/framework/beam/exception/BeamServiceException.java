package com.yzl.framework.beam.exception;

import com.yzl.framework.beam.common.BeamMessageConstant;

public class BeamServiceException extends BeamAbstractException {

    public BeamServiceException() {
        super(BeamMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public BeamServiceException(BeamMessage beamMessage) {
        super(beamMessage);
    }

    public BeamServiceException(String message) {
        super(message, BeamMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public BeamServiceException(String message, BeamMessage beamMessage) {
        super(message, beamMessage);
    }

    public BeamServiceException(String message, Throwable cause) {
        super(message, cause, BeamMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public BeamServiceException(String message, Throwable cause, BeamMessage beamMessage) {
        super(message, cause, beamMessage);
    }

    public BeamServiceException(Throwable cause) {
        super(cause, BeamMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public BeamServiceException(Throwable cause, BeamMessage beamMessage) {
        super(cause, beamMessage);
    }

}
