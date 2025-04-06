package guru.qa.niffler.tests.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import io.grpc.StatusRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.grpc.CurrencyValues.*;


public class CurrencyGrpcTests extends BaseGrpcTest{

    @NotNull
    private static Stream<Arguments> currencyTestData() {
        return Stream.of(
                Arguments.of(100.0, USD, RUB, 6666.67),
                Arguments.of(100.0, USD, KZT, 47619.05),
                Arguments.of(100.0, USD, EUR, 92.59),
                Arguments.of(100.0, USD, USD, 100),
                Arguments.of(100.0, RUB, USD, 1.5),
                Arguments.of(100.0, RUB, KZT, 714.29),
                Arguments.of(100.0, RUB, EUR, 1.39),
                Arguments.of(100.0, RUB, RUB, 100),
                Arguments.of(100.0, KZT, USD, 0.21),
                Arguments.of(100.0, KZT, KZT, 100),
                Arguments.of(100.0, KZT, EUR, 0.19),
                Arguments.of(100.0, KZT, RUB, 14.0),
                Arguments.of(100.0, EUR, USD, 108.0),
                Arguments.of(100.0, EUR, KZT, 51428.57),
                Arguments.of(100.0, EUR, RUB, 7200.0),
                Arguments.of(100.0, EUR, EUR, 100)
        );}

    @Test
    @DisplayName("Все типы валют должны быть получены")
    void allCurrencyShouldReturned(){
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    @ParameterizedTest
    @MethodSource("currencyTestData")
    @DisplayName("Проверка конвертации валюты")
    void checkCalculateRates(double amount, CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double calculatedAmountExpected){
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(amount)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(request);
        Assertions.assertEquals(calculatedAmountExpected,response.getCalculatedAmount());
    }

    @Test
    @DisplayName("Проверка невозможности конвертации неизвестной валюты")
    void checkInvalidCalculateRateForUnrecognizedCurrency(){
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(UNSPECIFIED)
                .setDesiredCurrency(RUB)
                .setAmount(100.0)
                .build();

        Assertions.assertThrows(StatusRuntimeException.class, () ->
                blockingStub.calculateRate(request),
                "Должно генерироваться исключение для неизвестной валюты");
    }
}
