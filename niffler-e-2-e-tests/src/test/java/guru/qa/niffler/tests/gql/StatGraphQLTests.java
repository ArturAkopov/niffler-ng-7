package guru.qa.niffler.tests.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StatGraphQLTests extends BaseGraphQLTest {


    @User
    @ApiLogin
    @Test
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        final StatQuery.Stat result = data.stat;

        Assertions.assertEquals(0.0,
                result.total);
    }

    @User(spendings = {
            @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990,
                    currency = CurrencyValues.USD
            ),
            @Spending(category = "Pet a duck",
                    description = "Hobbies",
                    amount = 2000,
                    currency = CurrencyValues.KZT
            ),
            @Spending(category = "Сосиска",
                    description = "Съесть сосиску",
                    amount = 5000
            )
    }
    )
    @ApiLogin
    @Test
    void currencyOfTheCategoriesShouldBeConvertedToRublesTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        final StatQuery.Stat result = data.stat;

        Assertions.assertEquals(5337946.67, result.total);
        Assertions.assertEquals(CurrencyValues.RUB.name(), result.currency.rawValue);
    }
}