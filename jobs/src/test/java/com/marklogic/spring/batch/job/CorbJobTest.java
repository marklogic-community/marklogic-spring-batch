package com.marklogic.spring.batch.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import org.junit.After;
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
        DatabaseClient client = getClient();
        ExtensionLibrariesManager libMgr = client.newServerConfigManager().newExtensionLibrariesManager();
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

        XMLDocumentManager xmlMgr = client.newXMLDocumentManager();
        StringHandle handle = new StringHandle("<hello />");
        xmlMgr.write("/doc.xml", handle);
    }

    @Test
    public void runCorbJobTest  () {
        runJobWithMarkLogicJobRepository(CorbConfig.class,
                "--urisModule", "/ext/corb/uris.xqy",
                "--transformModule", "/ext/corb/process.xqy");
        thenVerifyCorbWorked();
    }

    private void thenVerifyCorbWorked() {
        DatabaseClient client = getClient();
        XMLDocumentManager xmlMgr = client.newXMLDocumentManager();
        StringHandle handle = xmlMgr.read("/doc.xml", new StringHandle());
        Fragment fragment = new Fragment(handle.get());
        fragment.assertElementExists("/goodbye");
    }

    @After
    public void uninstallModules() {
        DatabaseClient client = getClient();
        ExtensionLibrariesManager libMgr = client.newServerConfigManager().newExtensionLibrariesManager();
        libMgr.delete("/ext/corb/uris.xqy");
        libMgr.delete("/ext/corb/process.xqy");
    }
}
