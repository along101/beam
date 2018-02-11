package com.yzl.framework.beam.protocol;

import com.yzl.framework.beam.rpc.Protocol;

public interface ProtocolFactory {

    Protocol newInstance();

}
