package com.jdk2010.framework.test.rpc;

import org.springframework.stereotype.Service;

import com.jdk2010.framework.rpc.provider.annotation.Contract;

@Contract(HelloService.class)
@Service("contract1")
public class ContractTest1 {

}
