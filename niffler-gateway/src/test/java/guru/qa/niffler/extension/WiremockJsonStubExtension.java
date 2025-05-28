package guru.qa.niffler.extension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class WiremockJsonStubExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private WireMockServer wireMockServer;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WiremockStubs annotation = context.getRequiredTestMethod()
                .getAnnotation(WiremockStubs.class);

        wireMockServer = new WireMockServer(annotation.port());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        for (String configPath : annotation.configPath()) {
            configureStubFromJson(configPath);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(WireMockServer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {
        return wireMockServer;
    }

    private void configureStubFromJson(String configPath) throws IOException {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("wiremock/json/" + configPath)) {

            if (is == null) {
                throw new IllegalArgumentException("File not found in resources: wiremock/json/" + configPath);
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            StubMapping stubMapping = StubMapping.buildFrom(
                    String.valueOf(Json.read(
                            jsonContent,
                            StubMapping.class
                    ))
            );

            wireMockServer.addStubMapping(stubMapping);
        }
    }
}