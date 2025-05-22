package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.ex.SpendNotFoundException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SpendServiceTest {

    @Test
    void getSpendsForUserShouldThrowExceptionInCaseThatIdIsIncorrectFormat(
            @Mock SpendRepository spendRepository,
            @Mock CategoryService categoryService
    ) {
        final String incorrectID = "incorrectID";
        final String correctUsername = "duck";

        SpendService spendService = new SpendService(spendRepository, categoryService);

        SpendNotFoundException ex = Assertions.assertThrows(
                SpendNotFoundException.class,
                () -> spendService.getSpendForUser(incorrectID, correctUsername)
        );
        Assertions.assertEquals("Can`t find spend by given id: " + incorrectID, ex.getMessage());
    }

    @Test
    void getSpendsForUserShouldThrowExceptionInCaseThatSpendNotFoundInDb(
            @Mock SpendRepository spendRepository,
            @Mock CategoryService categoryService
    ) {
        final UUID correctID = UUID.randomUUID();
        final String correctUsername = "duck";

        Mockito
                .when(spendRepository.findByIdAndUsername(
                                eq(correctID),
                                eq(correctUsername)
                        )
                )
                .thenReturn(Optional.empty());

        SpendService spendService = new SpendService(spendRepository, categoryService);

        SpendNotFoundException ex = Assertions.assertThrows(
                SpendNotFoundException.class,
                () -> spendService.getSpendForUser(correctID.toString(), correctUsername)
        );
        Assertions.assertEquals("Can`t find spend by given id: " + correctID, ex.getMessage());
    }

    @Test
    void getSpendsForUserShouldReturnCorrectJsonObject(
            @Mock SpendRepository spendRepository,
            @Mock CategoryService categoryService
    ) {
        final UUID correctID = UUID.randomUUID();
        final String correctUsername = "duck";
        final String spendDescription = "Unit-test spend description";
        final String categoryDescription = "Unit-test category description";
        final SpendEntity spendEntity = new SpendEntity();
        final CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setId(UUID.randomUUID());
        categoryEntity.setUsername(correctUsername);
        categoryEntity.setName(categoryDescription);
        categoryEntity.setArchived(false);

        spendEntity.setId(correctID);
        spendEntity.setUsername(correctUsername);
        spendEntity.setCurrency(CurrencyValues.RUB);
        spendEntity.setAmount(15.15);
        spendEntity.setDescription(spendDescription);
        spendEntity.setSpendDate(new Date(0));
        spendEntity.setCategory(categoryEntity);

        Mockito
                .when(spendRepository.findByIdAndUsername(
                                eq(correctID),
                                eq(correctUsername)
                        )
                )
                .thenReturn(Optional.of(
                        spendEntity
                ));

        SpendService spendService = new SpendService(spendRepository, categoryService);
        final SpendJson result = spendService.getSpendForUser(correctID.toString(), correctUsername);

        Mockito.verify(spendRepository, Mockito.times(1))
                .findByIdAndUsername(eq(correctID), eq(correctUsername));

        Assertions.assertEquals(spendDescription, result.description());
    }
}