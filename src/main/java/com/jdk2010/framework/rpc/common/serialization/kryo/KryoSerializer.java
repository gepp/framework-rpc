package com.jdk2010.framework.rpc.common.serialization.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer {

    public static byte[] serialize(Object obj) throws IOException {
        Output output = null;
        Kryo kryo = new Kryo();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output = new Output(baos);
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } finally {
            if (output != null) {
                output.close();
            }
            kryo = null;
        }
    }

    public static Object deserialize(byte[] bits) throws IOException {
        if (bits == null || bits.length == 0) {
            return null;
        }
        Input ois = null;
        Kryo kryo = new Kryo();
        Object session = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bits);
            ois = new Input(bais);
            session = (Object) kryo.readClassAndObject(ois);
        } finally {
            if (ois != null) {
                ois.close();
            }
            kryo = null;
        }
        return session;
    }

}
