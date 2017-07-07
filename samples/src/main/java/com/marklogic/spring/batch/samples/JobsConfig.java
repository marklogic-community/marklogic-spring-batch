package com.marklogic.spring.batch.samples;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DeleteDocumentsJobConfig.class,
        ImportDocumentsFromDirectoryJobConfig.class,
        CorbJobConfig.class,
        ExportContentFromMarkLogicJobConfig.class,
        ImportDocumentsAndExtractTextJobConfig.class,
        LoadImagesFromDirectoryJobConfig.class,
        YourJobConfig.class})
public class JobsConfig {
}
