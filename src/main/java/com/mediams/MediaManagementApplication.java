package com.mediams;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 媒体素材管理系统启动类
 * 
 * @author mediams
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@MapperScan("com.mediams.mapper")
public class MediaManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaManagementApplication.class, args);
        System.out.println("========================================");
        System.out.println("   媒体素材管理系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}