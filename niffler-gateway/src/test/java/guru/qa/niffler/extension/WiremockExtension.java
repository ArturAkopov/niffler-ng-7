package guru.qa.niffler.extension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WiremockExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private final WireMockServer wireMockServer = new WireMockServer(
            new WireMockConfiguration()
                    .port(8093)
    );

    @Override
    public void beforeEach(@NonNull ExtensionContext context) throws Exception {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        context.getTestMethod().ifPresent(method -> {
            WiremockStubs[] annotations = method.getAnnotationsByType(WiremockStubs.class);
            for (WiremockStubs annotation : annotations) {
                stubFromConfig(annotation);
            }
        });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        wireMockServer.stop();
    }

    @Override
    public boolean supportsParameter(@NonNull ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(WireMockServer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return wireMockServer;
    }

    private void stubFromConfig(@NonNull WiremockStubs config) {
        try {
            String json = Files.readString(Paths.get("src/test/resources/wiremock/json", config.jsonFile()));

            MappingBuilder mappingBuilder = WireMock.get(WireMock.urlPathEqualTo(config.urlPath()));

            for (WiremockStubs.QueryParam param : config.queryParams()) {
                mappingBuilder.withQueryParam(param.name(), WireMock.equalTo(param.value()));
            }

            wireMockServer.stubFor(mappingBuilder.willReturn(WireMock.okJson(json)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + config.jsonFile(), e);
        }
    }
}