package com.jdk2010.framework.test.rpc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRpcServer {
    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
