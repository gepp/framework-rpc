package com.jdk2010.framework.test.rpc;


public interface HelloService {
    String hello(String name);

    String hello(Person person);
    
}
