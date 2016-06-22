package com.marklogic.spring.batch.config;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.item.DirectoryReader;
import com.marklogic.spring.batch.item.shapefile.ImportShapefilesConfig;
import com.marklogic.spring.batch.item.shapefile.ShapefileAndJson;
import com.marklogic.spring.batch.item.shapefile.ShapefileAndJsonWriter;
import com.marklogic.spring.batch.item.shapefile.ShapefileProcessor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.List;

public class ImportShapefilesTest extends AbstractJobsJobTest {

    @Test
    public void test() {
        runJob(ImportShapefilesConfig.class,
                "--input_file_path", "build/shapefiles");
    }

}
