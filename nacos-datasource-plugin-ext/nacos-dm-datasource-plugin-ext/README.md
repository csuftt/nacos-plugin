# Nacos数据库适配插件

## 一、注意事项

### 1.1、修改数据库配置文件

在application.properties文件中声明dameng的配置信息：

```java
spring.datasource.platform=dm
        db.url.0=jdbc:dm://127.0.0.1:5236/DMSERVER?schema=NACOS&compatibleMode=mysql&ignoreCase=true&ENCODING=utf-8
        db.user.0=SYSDBA
        db.password.0=SYSDBA
        db.pool.config.driverClassName=dm.jdbc.driver.DmDriver
```

### 1.2、修改dameng数据库配置文件

建议使用dameng自带的驱动,达梦安装目录下drivers/jdbc下的驱动，否则可能会出现驱动版本不兼容的问题 （读取超长CLOB，TEXT） 替换项目中的lib/DmJdbcDriver18.jar

```xml

<dependency>
    <groupId>com.dameng</groupId>
    <artifactId>DmJdbcDriver18</artifactId>
    <version>${jdbc.dm.version}</version>
    <scope>system</scope>
    <systemPath>${basedir}/lib/DmJdbcDriver18.jar</systemPath>
</dependency>
```

### 1.3、表结构初始化

在nacos数据库中执行schema/nacos-dm.sql文件，如果不是NACOS模式，需要修改schema/nacos-dm.sql文件中的模式名称
如果表结构没有及时适配，可以使用dameng自带的数据迁移工具进行转换（dameng安装目录/tool/dts.exe 版本8.4.2.98 低版本不识别mysql自增方言）， 或者手动修改表结构
## 三、Nacos 3.1 兼容性补充（2026-11）

本 fork 在官方 `3.1.x.2` 基础上补充了 Nacos 3.1.x 所需内容，已在 **Nacos 3.1.0 + DM8（2025-04-23）+ DmJdbcDriver18 8.1.3.x** 单机环境验证：

- 新增 `ConfigMigrateMapperByDaMeng`（`migrate_config`）：Nacos 3.1 启动阶段会查找该 Mapper，缺失时启动失败；
- 新增 `ConfigInfoGrayMapperByDaMeng`（`config_info_gray`）：Nacos 3.1 灰度配置/启动 dump 需要；
- `schema/nacos-dm.sql` 增加 `CONFIG_INFO_GRAY` 建表/约束/索引，并为 `HIS_CONFIG_INFO` 补 `PUBLISH_TYPE`、`GRAY_NAME`、`EXT_INFO` 三列；
- `NOW()` 映射由 `SYSDATE` 改为 `CURRENT_TIMESTAMP`：DM 的 `SYSDATE` 返回 UTC，会把 `gmt_modified` 写慢 8 小时；
- 主键回填改用 `LOWER_RETURN_PRIMARY_KEYS`（小写 `id`），与 Nacos 3.1 的 `keyProperty=id` 对齐。

### 部署注意

1. **Nacos 端口配置**：必须使用 Nacos 原生键，不要用 Spring 的 `server.port`：
   ```properties
   nacos.server.main.port=18848
   nacos.console.port=8090
   ```
   使用 `server.port=8848` 会导致 console 上下文与主上下文抢同一端口，console 启动失败后 `DumpService` 缓存订阅者随之失效，表现为"发布成功但立即读取不到，重启后才可读"。

2. **DM 兼容模式**：当前 capacity Mapper 仍继承 MySQL 实现，SQL 中带 `` `usage` ``/`` `max_size` `` 反引号。建议 DM 实例使用 `COMPATIBLE_MODE=4`（MySQL 兼容模式）；若必须使用原生模式，需要自行覆盖 `GroupCapacityMapperByDaMeng`/`TenantCapacityMapperByDaMeng` 的 SQL（对应上游 issue #177）。

3. **gRPC 端口**：Nacos 3.x 服务端 gRPC 端口为主端口 + 1000（如 18848 → 19848）；客户端若需自定义偏移，使用 JVM 参数 `-Dnacos.server.grpc.port.offset=<offset>`（该属性通过 `System.getProperty` 读取，不是普通 `Properties` 键）。

4. **JDBC URL 示例**：
   ```properties
   spring.sql.init.platform=dm
   spring.datasource.platform=dm
   db.pool.config.driverClassName=dm.jdbc.driver.DmDriver
   db.url.0=jdbc:dm://127.0.0.1:5238?schema=NACOS&compatibleMode=mysql&ignoreCase=true&ENCODING=utf-8
   ```

> 已验证：Nacos 3.1.0 启动、配置发布/查询/更新/删除、Nacos Client SDK publish/get/remove、控制台访问。灰度发布与集群模式尚未端到端验证。
