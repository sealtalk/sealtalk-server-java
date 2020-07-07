package com.rcloud.server.sealtalk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rcloud.server.sealtalk.dao")
public class SealtalkServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SealtalkServerApplication.class, args);
    }

}
