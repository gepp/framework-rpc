package com.jdk2010.framework.test.rpc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdk2010.framework.rpc.consumer.ServiceProxy;

public class TestClient {

    @Autowired
    private ServiceProxy serviceProxy;
   

    public void helloTest1() {
        System.out.println(serviceProxy);
        HelloService helloService = serviceProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result + "---------");
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("spring-client.xml");
        TestClient client = new TestClient();
        client.helloTest1();
    }
}
