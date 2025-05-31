package guru.qa.niffler.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;


import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyControllerTest {

    private static final int WIREMOCK_PORT = 8092;
    private final WireMockServer wm = new WireMockServer(
            WireMockConfiguration
                    .wireMockConfig()
                    .port(WIREMOCK_PORT)
                    .withRootDirectory("src/test/resources/wiremock/")
                    .extensions(new Jetty12GrpcExtensionFactory())
    );
    private final WireMockGrpcService mockCurrencyService = new WireMockGrpcService(
            new WireMock(WIREMOCK_PORT),
            "guru.qa.grpc.niffler.NifflerCurrencyService"
    );

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        wm.start();
        System.out.println("WireMock GRPC запущен на порту: " + wm.port());
    }

    @AfterEach
    void afterEach() {
        wm.stop();
    }

    @Test
    void getAllCurrencies() throws Exception {

        final List<Currency> currencies = List.of(
                Currency.newBuilder()
                        .setCurrency(CurrencyValues.RUB)
                        .setCurrencyRate(1.0)
                        .build(),
                Currency.newBuilder()
                        .setCurrency(CurrencyValues.USD)
                        .setCurrencyRate(90.5)
                        .build(),
                Currency.newBuilder()
                        .setCurrency(CurrencyValues.EUR)
                        .setCurrencyRate(100.2)
                        .build(),
                Currency.newBuilder()
                        .setCurrency(CurrencyValues.KZT)
                        .setCurrencyRate(500.75)
                        .build()
        );

        final CurrencyResponse response = CurrencyResponse.newBuilder()
                .addAllAllCurrencies(currencies)
                .build();

        mockCurrencyService.stubFor(
                WireMockGrpc.method("GetAllCurrencies")
                        .willReturn(WireMockGrpc.message(CurrencyResponse.newBuilder()
                                .addAllAllCurrencies(currencies)
                                .build())
                        ));

        mockMvc.perform(get("/api/currencies/all")
                        .with(jwt().jwt(c -> c.claim("sub", "duck"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].currency",
                        containsInAnyOrder("RUB", "USD", "EUR", "KZT")));
    }
}