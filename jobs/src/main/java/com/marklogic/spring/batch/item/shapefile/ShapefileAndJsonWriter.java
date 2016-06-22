package com.marklogic.spring.batch.item.shapefile;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.*;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.item.AbstractDocumentWriter;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ShapefileAndJsonWriter extends AbstractDocumentWriter implements ItemWriter<ShapefileAndJson> {

    private DatabaseClient client;

    public ShapefileAndJsonWriter(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public void write(List<? extends ShapefileAndJson> items) throws Exception {
        BinaryDocumentManager binaryMgr = client.newBinaryDocumentManager();
        JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
        DocumentWriteSet binarySet = binaryMgr.newWriteSet();
        DocumentWriteSet jsonSet = jsonMgr.newWriteSet();
        for (ShapefileAndJson item : items) {
            DocumentDescriptor bd = binaryMgr.newDescriptor("/shapefile/" + item.file.getName());
            binarySet.add(bd, buildMetadata(), new FileHandle(item.file));

            logger.info(item.json);

            DocumentDescriptor jd = jsonMgr.newDescriptor("/shapefile/" + item.file.getName() + ".json");
            jsonSet.add(jd, buildMetadata(), new StringHandle(item.json).withFormat(Format.JSON));
        }

        binaryMgr.write(binarySet);
        jsonMgr.write(jsonSet);
    }
}
