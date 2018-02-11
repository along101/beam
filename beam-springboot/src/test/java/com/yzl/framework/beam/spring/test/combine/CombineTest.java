package com.yzl.framework.beam.spring.test.combine;

import com.yzl.framework.beam.annotation.BeamRouteClient;
import com.yzl.framework.beam.annotation.BeamService;
import com.yzl.framework.beam.proto.Simple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CombineApplication.class)
public class CombineTest {

    @BeamRouteClient
    private Simple simple;

    @BeamService
    private Simple simpleService;

    @Autowired
    private SimpleImplCombine simpleImpl;

    @Test
    public void testCombine() {
        System.out.println(simple.getClass());
        System.out.println(simpleService.getClass());
        System.out.println(simpleImpl.getClass());
    }
}
