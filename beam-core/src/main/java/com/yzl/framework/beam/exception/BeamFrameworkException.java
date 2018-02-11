package com.yzl.framework.beam.exception;

import com.yzl.framework.beam.common.BeamMessageConstant;

public class BeamFrameworkException extends BeamAbstractException {

    public BeamFrameworkException() {
        super(BeamMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public BeamFrameworkException(BeamMessage beamMessage) {
        super(beamMessage);
    }

    public BeamFrameworkException(String message) {
        super(message, BeamMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public BeamFrameworkException(String message, BeamMessage beamMessage) {
        super(message, beamMessage);
    }

    public BeamFrameworkException(String message, Throwable cause) {
        super(message, cause, BeamMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public BeamFrameworkException(String message, Throwable cause, BeamMessage beamMessage) {
        super(message, cause, beamMessage);
    }

    public BeamFrameworkException(Throwable cause) {
        super(cause, BeamMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public BeamFrameworkException(Throwable cause, BeamMessage beamMessage) {
        super(cause, beamMessage);
    }

}
