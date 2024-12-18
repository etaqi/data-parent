package com.tdp.data.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 提供文件上传的web项目
 * @author admin
 */
@SpringBootApplication
@EnableScheduling
public class DataWebApplication {
  public static void main(String[] args) {
    SpringApplication.run(DataWebApplication.class, args);
  }
}
