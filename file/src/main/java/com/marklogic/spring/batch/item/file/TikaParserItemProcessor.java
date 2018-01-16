package com.marklogic.spring.batch.item.file;

import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.item.file.support.TikaParser;
import com.marklogic.spring.batch.item.processor.AbstractMarkLogicItemProcessor;
import com.marklogic.spring.batch.item.processor.support.UriGenerator;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class TikaParserItemProcessor extends AbstractMarkLogicItemProcessor<Resource> {

    public TikaParserItemProcessor() {
        super();
    }

    public TikaParserItemProcessor(UriGenerator uriGenerator) {
        super(uriGenerator);
    }

    @Override
    public AbstractWriteHandle getContentHandle(Resource item) throws Exception {
        String parsedContent = TikaParser.parseToXML(item.getInputStream());
        InputSource inputSource = new InputSource(new StringReader(parsedContent.toString()));
        InputSourceHandle handle = new InputSourceHandle(inputSource);
        return handle;
    }


}
