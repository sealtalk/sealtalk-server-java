package com.rcloud.server.sealtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.rcloud.server.sealtalk.dao")
public class SealtalkServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SealtalkServerApplication.class, args);
    }

}
