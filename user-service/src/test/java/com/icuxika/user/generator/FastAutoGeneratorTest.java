package com.icuxika.user.generator;

import com.alibaba.druid.DbType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static com.alibaba.druid.sql.parser.SQLParserUtils.getTables;

/**
 * MyBatis-Plus 代码生成器 <a href="https://baomidou.com/pages/981406/#%E6%95%B0%E6%8D%AE%E5%BA%93%E9%85%8D%E7%BD%AE-datasourceconfig">mybatis-plus-generator</a>
 */
public class FastAutoGeneratorTest {

    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://127.0.0.1:3306/driftwood-cloud?serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true", "root", "ALLURE_love921");

    private static List<String> tables(String tables) {
        if ("all".equals(tables)) {
            return getTables(tables, DbType.mysql);
        }
        return Arrays.stream(tables.split(",")).toList();
    }

    public static void main(String[] args) {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称")).outputDir("D://mybatis-plus-generated-files"))
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(tables(scanner.apply("请输入表名，多个英文逗号分隔，所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .mapperBuilder().mapperAnnotation(Mapper.class)
                        .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl")
                        .build())
                // 模板配置
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
