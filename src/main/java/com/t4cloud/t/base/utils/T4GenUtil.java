package com.t4cloud.t.base.utils;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.t4cloud.t.base.config.GenCodeConfig;
import com.t4cloud.t.base.config.GenModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

/**
 * T4-CLOUD项目专用代码生成器
 *
 * <p>
 *
 * @author TeaR
 * @return --------------------
 * @date 2020/2/8 0:00
 */
@Slf4j
public class T4GenUtil {

    /**
     * 生成代码
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/6 16:37
     */
    public static void gen(GenCodeConfig genConfig) {
        Properties props = getProperties();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        GlobalConfig gc = new GlobalConfig();
        String outputDir = System.getProperty("user.dir") + "/_gen";
        String author = props.getProperty("t4cloud.author");
        Boolean cloud = Boolean.parseBoolean(props.getProperty("t4cloud.cloud"));

        //补全参数
        if (StrUtil.isBlank(genConfig.getServerName())) {
            genConfig.setServerName("T4Cloud-" + StrUtil.upperFirst(genConfig.getModuleName()));
        }

        //全局配置部分
        gc.setOutputDir(outputDir);
        gc.setAuthor(author);
        gc.setFileOverride(true);
        gc.setOpen(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        gc.setSwagger2(true);
        gc.setDateType(DateType.ONLY_DATE);
        mpg.setGlobalConfig(gc);

        //数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDriverName(props.getProperty("spring.datasource.driver-class-name"));
        dsc.setUrl(props.getProperty("spring.datasource.url"));
        dsc.setUsername(props.getProperty("spring.datasource.username"));
        dsc.setPassword(props.getProperty("spring.datasource.password"));
//        dsc.setDbQuery(new MySqlQuery() {
//            /**
//             * 重写父类预留查询自定义字段<br>
//             * 这里查询的 SQL 对应父类 tableFieldsSql 的查询字段，默认不能满足你的需求请重写它<br>
//             * 模板中调用：  table.fields 获取所有字段信息，
//             * 然后循环字段获取 field.customMap 从 MAP 中获取注入字段如下  NULL 或者 PRIVILEGES
//             */
//            @Override
//            public String[] fieldCustom() {
//                return new String[]{"NULL", "PRIVILEGES"};
//            }
//        });
        mpg.setDataSource(dsc);

        StrategyConfig strategy = new StrategyConfig();
        //命名转换
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);

        //表名前缀和需要生成的表名
        strategy.setTablePrefix(genConfig.getTablePrefix());
        strategy.setInclude(genConfig.getTableName());

        //父类增强器
        strategy.setSuperEntityClass("com.t4cloud.t.base.entity.BaseEntity");
        strategy.setSuperEntityColumns("id", "tenant_id", "create_by", "create_time", "update_by", "update_time", "parent_id");
        strategy.setSuperServiceClass("com.t4cloud.t.base.service.T4Service");
        strategy.setSuperServiceImplClass("com.t4cloud.t.base.service.impl.T4ServiceImpl");
        strategy.setSuperControllerClass("com.t4cloud.t.base.controller.T4Controller");

        strategy.setEntityBuilderModel(false);
        strategy.setEntityLombokModel(true);
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);

        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(genConfig.getModuleName());
        pc.setParent("com.t4cloud.t");
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setXml("mapper");
        mpg.setPackageInfo(pc);

        // 自定义配置
        final Map<String, Object> map = new HashMap(16);
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // 区分是否开启cloud模式
                map.put("cloud", cloud);
                // 定义saas模式下租户ID字段
                //TODO 是否开启逻辑删除 （暂未开发）
                map.put("deleteLogic", false);
                map.put("tree", genConfig.getTree());
                this.setMap(map);
            }
        };
        List<FileOutConfig> focList = new ArrayList();

        //权限sql生成
        focList.add(new FileOutConfig("/templates/sql/menu.sql.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                map.put("moduleName", genConfig.getModuleName());
                //权限表ID
                map.put("menuId", IdWorker.getId());
                map.put("addMenuId", IdWorker.getId());
                map.put("editMenuId", IdWorker.getId());
                map.put("removeMenuId", IdWorker.getId());
                map.put("viewMenuId", IdWorker.getId());
                map.put("exportMenuId", IdWorker.getId());
                map.put("importMenuId", IdWorker.getId());
                //角色授权表ID
                map.put("roleMenuId", IdWorker.getId());
                map.put("roleAddMenuId", IdWorker.getId());
                map.put("roleEditMenuId", IdWorker.getId());
                map.put("roleRemoveMenuId", IdWorker.getId());
                map.put("roleViewMenuId", IdWorker.getId());
                map.put("roleExportMenuId", IdWorker.getId());
                map.put("roleImportMenuId", IdWorker.getId());
                return outputDir + "//sql/" + tableInfo.getEntityName() + ".menu.sql";
            }
        });

        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return outputDir + "/" + pc.getParent().replace(".", "/") + "/mapper/xml/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        //是否生成feign client和DTO
        if (genConfig.getFeignClient() && StrUtil.isNotBlank(genConfig.getServerName())) {
            focList.add(new FileOutConfig("/templates/entityDTO.java.vm") {
                public String outputFile(TableInfo tableInfo) {
                    return outputDir + "/com/t4cloud/t/feign/dto/" + tableInfo.getEntityName() + "DTO" + ".java";
                }
            });
            focList.add(new FileOutConfig("/templates/feignClient.java.vm") {
                public String outputFile(TableInfo tableInfo) {
                    map.put("serverName", genConfig.getServerName());
                    map.put("classNamePrefix", StrUtil.upperFirst(genConfig.getModuleName()));
                    return outputDir + "/com/t4cloud/t/feign/client/" + StrUtil.upperFirst(genConfig.getModuleName()) + tableInfo.getEntityName() + "Client" + ".java";
                }
            });
        }

        //页面生成
        focList.add(new FileOutConfig("/templates/vue/list.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                map.put("serverName", genConfig.getServerName());
                return outputDir + "//vue/views/" + genConfig.getModuleName() + "/" + tableInfo.getEntityName() + "List.vue";
            }
        });
        focList.add(new FileOutConfig("/templates/vue/modal" + (genConfig.getDrawer() ? "_Drawer" : "") + ".vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                map.put("serverName", genConfig.getServerName());
                return outputDir + "//vue/views/" + genConfig.getModuleName() + "/modals/" + tableInfo.getEntityName() + "Modal.vue";
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);


        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/entity.java");
        templateConfig.setService("templates/service.java");
        templateConfig.setController("templates/controller.java");

        //有自定义配置，此处不需要再配置
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);
        mpg.setTemplateEngine(new VelocityTemplateEngine());

        //启动生成器
        mpg.execute();

        log.debug("========================== T4CLOUD 代码生成完毕 ==========================");
        log.debug("========================== com 移动到模块的java目录下(如果有feign相关，请移动到FeignServer的模块下) ==========================");
        log.debug("========================== sql 为菜单对应的数据，需导入数据库 ==========================");
        log.debug("========================== vue 为T4Cloud-WEB配套代码，复制views覆盖即可 ==========================");
    }

    /**
     * 读取配置的参数
     *
     * <p>
     *
     * @return java.util.Properties
     * --------------------
     * @author TeaR
     * @date 2020/2/6 16:37
     */
    private static Properties getProperties() {
        Resource resource = new ClassPathResource("/templates/code.properties");
        Properties props = new Properties();

        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        return props;
    }

    /**
     * 模块生成器，用来生成一个新的模块
     *
     * @param config 模块配置文件
     *               <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/4/4 19:33
     */
    public static void gen(GenModuleConfig config) {

        final String outputDir = System.getProperty("user.dir") + "/" + config.getArtifactId() + "/";

        //自动填充PackageName
        if (StrUtil.isBlank(config.getPackageName())) {
            if (config.getArtifactId().contains("-")) {
                config.setPackageName(config.getArtifactId().split("-")[1].toLowerCase());
            } else {
                config.setPackageName(config.getArtifactId().toLowerCase());
            }
        }

        TemplateEngine engine = TemplateUtil.createEngine(new cn.hutool.extra.template.TemplateConfig("templates", cn.hutool.extra.template.TemplateConfig.ResourceMode.CLASSPATH));

        //生成pom文件
        try {
            Dict dict = Dict.create();
            dict.set("groupId", config.getGroupId());
            dict.set("artifactId", config.getArtifactId());
            dict.set("description", config.getDescription());
            genFile(engine, "templates/module/pom.vm", outputDir + "pom.xml", dict);
            log.debug("========================== pom 生成完毕 ==========================");
        } catch (Exception e) {
            log.error("========================== pom 生成失败 ==========================");
        }

        //生成 dockerfile 文件
        try {
            Dict dict = Dict.create();
            dict.set("artifactId", config.getArtifactId());
            dict.set("serverPort", config.getServerPort());
            genFile(engine, "templates/module/dockerfile.vm", outputDir + "dockerfile", dict);
            log.debug("========================== dockerfile 生成完毕 ==========================");
        } catch (Exception e) {
            log.error("========================== dockerfile 生成失败 ==========================");
        }

        //生成 resources
        try {
            String path = "src/main/resources/";

            // 1 application
            genFile(engine, "templates/module/application.vm", outputDir + path + "application.yml", Dict.create());


            Dict dict = Dict.create();
            dict.set("packageName", config.getPackageName());
            dict.set("serverPort", config.getServerPort());
            dict.set("nacosAddr", config.getNacosAddr());
            dict.set("nacosGroup", config.getNacosGroup());
            dict.set("nacosNamespace", config.getNacosNamespace());

            dict.set("redisDatabase", config.getRedisDatabase());
            dict.set("redisHost", config.getRedisHost());
            dict.set("redisPort", config.getRedisPort());
            dict.set("redisPassword", config.getRedisPassword());

            dict.set("mysqlHost", config.getMysqlHost());
            dict.set("mysqlDateBase", config.getMysqlDateBase());
            dict.set("mysqlUsername", config.getMysqlUsername());
            dict.set("mysqlPassword", config.getMysqlPassword());


            dict.set("rocketHost", config.getRocketHost());
            dict.set("jobHost", config.getJobHost());
            dict.set("ossHost", config.getOssHost());

            // 2 application-dev
            genFile(engine, "templates/module/application-env.vm", outputDir + path + "application-dev.yml", dict);
            // 2 application-prod
            genFile(engine, "templates/module/application-env.vm", outputDir + path + "application-prod.yml", dict);
            // 2 application-test
            genFile(engine, "templates/module/application-env.vm", outputDir + path + "application-test.yml", dict);


            log.debug("========================== resources 生成完毕 ==========================");
        } catch (Exception e) {
            log.error("========================== resources 生成失败 ==========================");
        }

        //生成Java文件
        try {

            String path = "src/main/java/com/t4cloud/t/" + config.getPackageName() + "/";

            // 1.shiro
            Dict shiro = Dict.create();
            shiro.set("packageName", config.getPackageName());
            genFile(engine, "templates/module/java/ShiroConfig.vm", outputDir + path + "authc/ShiroConfig.java", shiro);

            //Application
            Dict application = Dict.create();
            application.set("packageName", config.getPackageName());
            application.set("artifactId", config.getArtifactId());
            application.set("appName", StrUtil.upperFirst(config.getPackageName()));
            genFile(engine, "templates/module/java/T4CloudApplication.vm", outputDir + path + "T4Cloud" + StrUtil.upperFirst(config.getPackageName()) + "Application.java", application);


            log.debug("========================== JAVA资源 生成完毕 ==========================");
        } catch (Exception e) {
            log.error("========================== JAVA资源 生成失败 ==========================");
        }


    }

    /**
     * 生成文件方法
     *
     * @param tempPath 模板路径
     * @param filePath 输出路径
     * @param param    模板参数
     *                 <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/4/4 20:33
     */
    private static void genFile(TemplateEngine engine, String tempPath, String filePath, Dict param) {
        Template appTemplate = engine.getTemplate(tempPath);
        String result = appTemplate.render(param);
        FileWriter writer = new FileWriter(filePath);
        writer.write(result);
    }

}