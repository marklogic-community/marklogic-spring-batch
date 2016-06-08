package com.marklogic.spring.batch.job;

import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class CorbJobTest extends AbstractJobTest {

    File urisModule;
    File transformModule;

    @Before
    public void installModules() {
        ExtensionLibrariesManager libMgr = getClient().newServerConfigManager().newExtensionLibrariesManager();
        try {
            urisModule = getApplicationContext().getResource("ml-modules/ext/corb/uris.xqy").getFile();
            transformModule = getApplicationContext().getResource("ml-modules/ext/corb/process.xqy").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileHandle urisHandle = new FileHandle(urisModule).withFormat(Format.TEXT);
        FileHandle transformHandle = new FileHandle(transformModule).withFormat(Format.TEXT);
        libMgr.write("/ext/corb/uris.xqy", urisHandle);
        libMgr.write("/ext/corb/process.xqy", transformHandle);
    }

    @Test
    public void testJob() {
        runJobWithMarkLogicJobRepository(CorbConfig.class,
                "--urisModule", "/ext/corb/uris.xqy",
                "--transformModule", "/ext/corb/process.xqy");
    }
}
