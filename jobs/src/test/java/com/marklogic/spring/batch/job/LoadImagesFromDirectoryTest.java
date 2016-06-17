package com.marklogic.spring.batch.job;

import com.marklogic.spring.batch.config.AbstractJobsJobTest;
import com.marklogic.spring.batch.config.LoadImagesFromDirectoryConfig;
import org.junit.Test;

/**
 * Created by sstafford on 6/17/2016.
 */
public class LoadImagesFromDirectoryTest extends AbstractJobsJobTest {

    @Test
    public void importBinariesTest() {
        runJob(LoadImagesFromDirectoryConfig.class,
                "--input_file_path", "binary/*.docx");

    }
}
