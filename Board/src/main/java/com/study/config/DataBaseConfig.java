package com.study.config;


import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration // 이 클래스가 스프링 설정 파일임을 나타냄
@PropertySource("classpath:/application.properties") // application.properties 파일에서 설정 값을 로드
public class DataBaseConfig {

    // Spring의 ApplicationContext를 주입받아 사용
    @Autowired
    private ApplicationContext context;

    /*
     * HikariCP 설정을 읽어오는 메서드
     * - application.properties에서 "spring.datasource.hikari"로 시작하는 설정 값을 읽어 HikariConfig에 매핑
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /**
     * DataSource Bean 생성
     * - HikariConfig를 기반으로 데이터 소스를 생성
     * - DataSource는 데이터베이스 연결 풀을 관리하며, 애플리케이션에서 데이터베이스와 연결할 때 사용
     */
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    /**
     * SqlSessionFactory Bean 생성
     * - MyBatis와 데이터베이스의 연결을 관리
     * - SqlSessionFactory는 MyBatis Mapper XML 파일과 데이터베이스 설정을 사용해 MyBatis 세션을 생성
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        
        // 위에서 생성된 DataSource를 MyBatis에 설정
        factoryBean.setDataSource(dataSource());
        
        // 주석 처리된 부분: MyBatis Mapper XML 파일의 위치를 설정 Spring Boot는 XML을 선호하지 않음
        // 예를 들어, "classpath:/mappers/**/*Mapper.xml" 경로에 있는 모든 XML 파일을 MyBatis에서 읽도록 설정
        // factoryBean.setMapperLocations(context.getResources("classpath:/mappers/**/*Mapper.xml"));
        
        return factoryBean.getObject();
    }

    /**
     * SqlSessionTemplate Bean 생성
     * - SqlSessionTemplate은 MyBatis의 SqlSession을 구현한 것으로, 스레드에 안전한 방식으로 MyBatis를 사용할 수 있도록 제공
     * - SqlSessionTemplate은 Mapper 인터페이스와 함께 사용됨
     */
    @Bean
    public SqlSessionTemplate sqlSession() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

}
