import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@RunWith(value = Arquillian.class)
public class ArquillianTest {

    @ArquillianResource(MainServlet.class)
    URL contextPath;

    @Deployment(testable = false)
    public static WebArchive init() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.jboss.weld.servlet:weld-servlet")
                .withTransitivity()
                .asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "jsfilter.war")
                .setWebXML("web.xml")
                .addAsWebInfResource("beans.xml", "beans.xml")
                .addAsManifestResource("context.xml", "context.xml")
                .addAsManifestResource("org.jboss.weld.environment.Container", "services/org.jboss.weld.environment.Container")
                .addClass(MainServlet.class)
                .addAsLibraries(libs);

        System.out.println(archive.toString(true));
        return archive;
    }

    @Test
    public void tryTest() throws IOException {
        InputStream stream = contextPath.openStream();
        String resp = IOUtils.toString(stream);

        Assert.assertEquals(resp, "main called");
    }
}
