package com.yzl.framework.beam.common;

import com.yzl.framework.beam.exception.BeamMessage;

public class BeamMessageConstant {
    public static final int SUCCESS = 0;
    // service error status 503
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10001;
    public static final int SERVICE_REJECT_ERROR_CODE = 10002;
    public static final int SERVICE_TIMEOUT_ERROR_CODE = 10003;
    public static final int SERVICE_TASK_CANCEL_ERROR_CODE = 10004;
    // service error status 404
    public static final int SERVICE_UNFOUND_ERROR_CODE = 10101;
    // service error status 3XX
    public static final int SERVICE_REDIRECT_ERROR_CODE = 10300;
    // service error status other
    public static final int SERVICE_UNKNOW_ERROR_CODE = 10000;
    // framework error
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20001;
    public static final int FRAMEWORK_ENCODE_ERROR_CODE = 20002;
    public static final int FRAMEWORK_DECODE_ERROR_CODE = 20003;
    public static final int FRAMEWORK_INIT_ERROR_CODE = 20004;
    public static final int FRAMEWORK_EXPORT_ERROR_CODE = 20005;
    public static final int FRAMEWORK_SERVER_ERROR_CODE = 20006;
    public static final int FRAMEWORK_REFER_ERROR_CODE = 20007;
    public static final int FRAMEWORK_REGISTER_ERROR_CODE = 20008;
    // biz exception
    public static final int BIZ_DEFAULT_ERROR_CODE = 30001;

    /**
     * service error start
     */
    public static final BeamMessage SERVICE_DEFAULT_ERROR = new BeamMessage(503, SERVICE_DEFAULT_ERROR_CODE, "service error");
    public static final BeamMessage SERVICE_REJECT = new BeamMessage(503, SERVICE_REJECT_ERROR_CODE, "service reject");
    public static final BeamMessage SERVICE_UNFOUND = new BeamMessage(404, SERVICE_UNFOUND_ERROR_CODE, "service unfound");
    public static final BeamMessage SERVICE_TIMEOUT = new BeamMessage(503, SERVICE_TIMEOUT_ERROR_CODE, "service request timeout");
    public static final BeamMessage SERVICE_TASK_CANCEL = new BeamMessage(503, SERVICE_TASK_CANCEL_ERROR_CODE, "service task cancel");
    /* service error end */

    /**
     * framework error start
     */
    public static final BeamMessage FRAMEWORK_DEFAULT_ERROR = new BeamMessage(503, FRAMEWORK_DEFAULT_ERROR_CODE,
            "framework default error");

    public static final BeamMessage FRAMEWORK_ENCODE_ERROR =
            new BeamMessage(503, FRAMEWORK_ENCODE_ERROR_CODE, "framework encode error");
    public static final BeamMessage FRAMEWORK_DECODE_ERROR =
            new BeamMessage(503, FRAMEWORK_DECODE_ERROR_CODE, "framework decode error");
    public static final BeamMessage FRAMEWORK_INIT_ERROR = new BeamMessage(500, FRAMEWORK_INIT_ERROR_CODE, "framework init error");
    public static final BeamMessage FRAMEWORK_EXPORT_ERROR =
            new BeamMessage(503, FRAMEWORK_EXPORT_ERROR_CODE, "framework export error");
    public static final BeamMessage FRAMEWORK_REFER_ERROR = new BeamMessage(503, FRAMEWORK_REFER_ERROR_CODE, "framework refer error");

    /* framework error end  */

    /**
     * biz error start
     */
    public static final BeamMessage BIZ_DEFAULT_EXCEPTION = new BeamMessage(503, BIZ_DEFAULT_ERROR_CODE, "provider error");
    /* biz error end */


    private BeamMessageConstant() {
    }
}
