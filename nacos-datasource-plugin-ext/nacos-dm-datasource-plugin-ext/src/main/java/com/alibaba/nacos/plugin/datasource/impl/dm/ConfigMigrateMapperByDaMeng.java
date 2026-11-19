/*
 * Copyright 1999-2024 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.plugin.datasource.impl.dm;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigMigrateMapper;

/**
 * The Dameng implementation of ConfigMigrateMapper.
 *
 * <p>Nacos 3.x registers this mapper by the logical table name {@code migrate_config}. The interface
 * provides default SQL implementations, so only the data source and table name need to be set.
 *
 * @author csuftt
 */
public class ConfigMigrateMapperByDaMeng extends BaseConfigInfoMapper implements ConfigMigrateMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.DM;
    }

    @Override
    public String getTableName() {
        return "migrate_config";
    }
}
