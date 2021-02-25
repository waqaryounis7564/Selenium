package com.example.seleniumdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SeleniumdemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(SeleniumdemoApplication.class, args);
        GBbackup gBbackup=new GBbackup();
//        gBbackup.startBot();
        try {
            GB.crawl();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
