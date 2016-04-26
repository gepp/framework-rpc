package com.jdk2010.framework.rpc.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jdk2010.framework.rpc.common.protocol.RpcRequest;
import com.jdk2010.framework.rpc.common.protocol.RpcResponse;
import com.jdk2010.framework.zookeeper.client.ZookeeperClient;

@Service
public class ServiceProxy {
    
    private String serverAddress;
    
    @Autowired
    private ZookeeperClient zookeeperClient;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public ZookeeperClient getZookeeperClient() {
        return zookeeperClient;
    }

    public void setZookeeperClient(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                       
                         System.out.println("zookeeperClient:"+zookeeperClient);
                        RpcRequest request  = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        serverAddress=zookeeperClient.discovery();
                        System.out.println("serverAddress================"+serverAddress);
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        RpcClient client = new RpcClient(host, port);
                        RpcResponse response = client.send(request);

                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
    
}
