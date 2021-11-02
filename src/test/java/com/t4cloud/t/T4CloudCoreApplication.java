package com.t4cloud.t;

import com.t4cloud.t.base.annotation.T4CloudApplicationStarter;
import com.t4cloud.t.base.boot.T4CloudApplication;
import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;

@Slf4j
@T4CloudApplicationStarter
public class T4CloudCoreApplication {

    public static void main(String[] args) throws UnknownHostException {
        T4CloudApplication.run(T4CloudCoreApplication.class);
    }

}
