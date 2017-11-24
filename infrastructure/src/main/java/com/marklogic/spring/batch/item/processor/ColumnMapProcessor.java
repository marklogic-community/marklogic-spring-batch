package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;
import com.marklogic.spring.batch.item.processor.support.UriGenerator;

import java.util.Map;
import java.util.UUID;

public class ColumnMapProcessor extends AbstractMarkLogicItemProcessor<Map<String, Object>> {

    private ColumnMapSerializer columnMapSerializer;
    private String rootLocalName = "CHANGEME";

    public ColumnMapProcessor(ColumnMapSerializer columnMapSerializer) {
        super();
        this.columnMapSerializer = columnMapSerializer;
        setUriGenerator(
                new UriGenerator() {
                    @Override
                    public String generateUri(Object o) {
                        String uuid = UUID.randomUUID().toString();
                        return "/" + rootLocalName.replaceAll("[^A-Za-z0-9\\_\\-]", "") + "/" + uuid + ".xml";
                    }
                });
    }

    public ColumnMapProcessor(ColumnMapSerializer columnMapSerializer, UriGenerator uriGenerator) {
        super(uriGenerator);
        this.columnMapSerializer = columnMapSerializer;
    }

    @Override
    public AbstractWriteHandle getContentHandle(Map<String, Object> item) throws Exception {
        String rootElement = item.containsKey(rootLocalName) ? item.get(rootLocalName).toString() : rootLocalName;
        return new StringHandle(columnMapSerializer.serializeColumnMap(item, rootElement));
    }

    public void setRootLocalName(String rootName) {
        this.rootLocalName = rootName;
    }

}
