package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;

import java.util.Map;

public class ColumnMapProcessor extends AbstractMarkLogicItemProcessor<Map<String, Object>> {

    private ColumnMapSerializer columnMapSerializer;
    private String rootLocalName = "CHANGEME";

    public ColumnMapProcessor(ColumnMapSerializer columnMapSerializer) {
        this.columnMapSerializer = columnMapSerializer;
        setType(rootLocalName);
    }

    @Override
    public AbstractWriteHandle getContentHandle(Map<String, Object> item) throws Exception {
        return new StringHandle(columnMapSerializer.serializeColumnMap(item, getType()));
    }

    public void setRootLocalName(String rootName) {
        setType(rootName);
    }

}
