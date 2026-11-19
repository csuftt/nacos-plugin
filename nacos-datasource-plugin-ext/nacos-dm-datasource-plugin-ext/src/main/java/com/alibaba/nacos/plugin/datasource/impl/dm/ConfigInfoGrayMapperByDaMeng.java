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
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoGrayMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Collections;

/**
 * The Dameng implementation of ConfigInfoGrayMapper.
 *
 * <p>Nacos 3.x introduced gray configuration. The DM module of this repository did not provide the
 * gray mapper before 3.1.x, which caused startup to fail with
 * {@code Failed to find the table, tableName: config_info_gray}.
 *
 * @author csuftt
 */
public class ConfigInfoGrayMapperByDaMeng extends BaseConfigInfoMapper implements ConfigInfoGrayMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.DM;
    }

    @Override
    public String getTableName() {
        return "config_info_gray";
    }

    @Override
    public MapperResult findChangeConfig(MapperContext context) {
        return ConfigInfoGrayMapper.super.findChangeConfig(context);
    }

    @Override
    public MapperResult findAllConfigInfoGrayForDumpAllFetchRows(MapperContext context) {
        String sql = "SELECT id, data_id, group_id, tenant_id, gray_name, gray_rule, app_name, content, md5, gmt_modified "
                + "FROM config_info_gray ORDER BY id LIMIT "
                + context.getStartRow() + "," + context.getPageSize();
        return new MapperResult(sql, Collections.emptyList());
    }
}
