package com.youmin.imsystem.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class MybatisGenerator {
    public static void main(String[] args) {

        AutoGenerator autoGenerator = new AutoGenerator();


        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);

        assembleDev(dataSourceConfig);
        autoGenerator.setDataSource(dataSourceConfig);


        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOpen(false);

        globalConfig.setOutputDir(System.getProperty("user.dir") + "/instant-messaging-system-server/src/main/java");



        globalConfig.setServiceImplName("%sDao");
        autoGenerator.setGlobalConfig(globalConfig);

        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.youmin.imsystem.common.user");
        packageConfig.setEntity("domain.entity");
        packageConfig.setMapper("mapper");
        packageConfig.setController("controller");
        packageConfig.setServiceImpl("dao");
        autoGenerator.setPackageInfo(packageConfig);


        StrategyConfig strategyConfig = new StrategyConfig();

        strategyConfig.setEntityLombokModel(true);

        strategyConfig.setNaming(NamingStrategy.underline_to_camel);

        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);

        strategyConfig.setEntityTableFieldAnnotationEnable(true);

        strategyConfig.setInclude(
                "user"
        );

        List<TableFill> list = new ArrayList<TableFill>();
        TableFill tableFill1 = new TableFill("create_time", FieldFill.INSERT);
        TableFill tableFill2 = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        list.add(tableFill1);
        list.add(tableFill2);


        autoGenerator.setStrategy(strategyConfig);


        autoGenerator.execute();

    }

    public static void assembleDev(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");
        dataSourceConfig.setUrl("jdbc:mysql://127.0.0.1:3307/IMSystem?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC");
    }
}
