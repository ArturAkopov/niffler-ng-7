package guru.qa.niffler.tests.kafka;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.KafkaService;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@KafkaTest
public class KafkaTests {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);

    @Test
    void userShouldBeProducedToKafka() throws IOException, InterruptedException {

        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        UserJson userFromKafka = KafkaService.getUserJson(username);
        Assertions.assertEquals(
                username,
                userFromKafka.username()
        );
    }
}
