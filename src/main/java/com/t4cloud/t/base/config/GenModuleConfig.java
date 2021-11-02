package com.t4cloud.t.base.config;

import lombok.Data;

/**
 * ModuleConfig
 * <p>
 * 生成模块配置
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/4/4 19:23
 */
@Data
public class GenModuleConfig {

    // ----------------------------------------------- pom -----------------------------------------------

    private String groupId = "com.t4cloud";
    private String artifactId = "T4-Business";
    private String packageName;
    private String description = "T4Cloud适配自定义商业模块";

    // ----------------------------------------------- application -----------------------------------------------

    private Integer serverPort = 8090;
    private String nacosAddr = "127.0.0.1:8848";
    private String nacosGroup = "dev";
    private String nacosNamespace;

    private Integer redisDatabase = 0;
    private String redisHost = "127.0.0.1";
    private Integer redisPort = 6379;
    private String redisPassword = "''";

    private String mysqlHost = "127.0.0.1:3306";
    private String mysqlDateBase = "t4-cloud";
    private String mysqlUsername = "root";
    private String mysqlPassword = "root";

    private String rocketHost = "127.0.0.1:9876";

    private String jobHost = "http://127.0.0.1:8020";

    private String ossHost = "http://127.0.0.1:8010/T4Cloud-Support/file/view/";


}
