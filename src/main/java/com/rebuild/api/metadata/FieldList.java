/*
Copyright (c) REBUILD <https://getrebuild.com/> and/or its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.api.metadata;

import cn.devezhao.persist4j.Entity;
import cn.devezhao.persist4j.Field;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.api.ApiContext;
import com.rebuild.api.ApiInvokeException;
import com.rebuild.api.BaseApi;
import com.rebuild.core.configuration.general.MultiSelectManager;
import com.rebuild.core.configuration.general.PickListManager;
import com.rebuild.core.metadata.MetadataHelper;
import com.rebuild.core.metadata.easymeta.DisplayType;
import com.rebuild.core.metadata.easymeta.EasyField;
import com.rebuild.core.metadata.easymeta.EasyMetaFactory;

/**
 * 获取字段列表
 *
 * @author devezhao
 * @since 2020/5/14
 */
public class FieldList extends BaseApi {

    @Override
    protected String getApiName() {
        return "metadata/fields";
    }

    @Override
    public JSON execute(ApiContext context) throws ApiInvokeException {
        String entity = context.getParameterNotBlank("entity");
        if (!MetadataHelper.containsEntity(entity)) {
            throw new ApiInvokeException("Unknown entity : " + entity);
        }

        Entity thatEntity = MetadataHelper.getEntity(entity);
        JSONArray array = new JSONArray();
        for (Field field : thatEntity.getFields()) {
            if (MetadataHelper.isSystemField(field) || !field.isQueryable()) {
                continue;
            }
            array.add(buildField(field));
        }
        return formatSuccess(array);
    }

    private JSONObject buildField(Field field) {
        final EasyField easyField = EasyMetaFactory.valueOf(field);
        final DisplayType dt = easyField.getDisplayType();

        JSONObject o = (JSONObject) easyField.toJSON();
        o.put("repeatable", field.isRepeatable());
        o.put("queryable", easyField.isQueryable());

        if (dt == DisplayType.MULTISELECT) {
            o.put("options", MultiSelectManager.instance.getSelectList(field));
        }
        if (dt == DisplayType.PICKLIST) {
            o.put("options", PickListManager.instance.getPickList(field));
        }
        return o;
    }
}
