package guru.qa.niffler.tests.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;


@WebTest
public class SpendingWebTests {

    @DisplayName("Название категории должно быть изменено в таблице")
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @DisabledByIssue("5")
    @Test
    void categoryDescriptionShouldBeChangedFromTable(@NotNull UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .editSpendingClick(user.testData().spends().getFirst().description())
                .editDescription(newDescription)
                .saveChange()
                .checkAlertMessage("Spending is edited successfully")
                .checkThatTableContainsSpending(newDescription);
    }

    @DisplayName("Должна быть добавлена новая трата")
    @User
    @ApiLogin
    @Test
    void shouldAddNewSpending() {
        String category = "Duck";
        int amount = 100;
        LocalDate currentDate = LocalDate.now();
        String description = RandomDataUtils.randomSentence(1);

        Selenide.open(EditSpendingPage.EDIT_SPEND_PAGE_URL, EditSpendingPage.class)
                .editCategory(category)
                .editAmount(amount)
                .editDate(currentDate)
                .editDescription(description)
                .saveChange()
                .checkAlertMessage("New spending is successfully created")
                .getSpendingTable()
                .checkTableContains(description);
    }

    @DisplayName("Проверка компонента статистики")
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @ScreenShotTest(value = "image/expected-stat.png", rewriteExpected = true)
    void checkStatComponentTest(BufferedImage expected) throws IOException {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkBubbles(new Bubble(Color.yellow, "Обучение 79990 ₽"));
    }

    @DisplayName("Проверка компонента статистики после редактирования траты")
    @User(
            spendings = @Spending(
                    category = "Pet a duck",
                    description = "Hobbies",
                    amount = 1000
            )
    )
    @ApiLogin
    @ScreenShotTest(value = "image/expected-edit-stat.png")
    void checkStatComponentAfterEditSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        String newAmount = "2000";
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .editSpendingClick(user.testData().spends().getFirst().description())
                .editAmount(Double.parseDouble(newAmount))
                .saveChange()
                .getStatComponent()
                .checkCellCategoryAndAmountInStatisticsBlock(user.testData().spends().getFirst().category().name(),
                        newAmount)
                .checkStatisticImage(expected);
    }

    @DisplayName("Проверка компонента статистики после удаления траты")
    @User(
            spendings = {
                    @Spending(
                            category = "Hitting a duck",
                            description = "Bad deeds",
                            amount = 1000),
                    @Spending(
                            category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000)
            }
    )
    @ApiLogin
    @ScreenShotTest(value = "image/expected-delete-stat.png",
            rewriteExpected = true)
    void checkStatComponentAfterDeleteSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getSpendingTable()
                .deleteSpending(user.testData().spends().getFirst().category().name());
        new MainPage()
                .getStatComponent()
                .checkStatisticImage(expected);
    }

    @DisplayName("Проверка компонента статистики после редактирования траты")
    @User(
            spendings = @Spending(
                    category = "Pet a duck",
                    description = "Hobbies",
                    amount = 2000
            )
    )
    @ApiLogin
    @ScreenShotTest(value = "image/expected-archived-stat.png")
    void checkStatComponentAfterArchivedCategoryTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.PROFILE_PAGE_URL, ProfilePage.class)
                .archivedCategory(user.testData().spends().getFirst().category().name())
                .getHeader()
                .toMainPage()
                .getStatComponent()
                .checkCellCategoryAndAmountInStatisticsBlock("Archived",
                        String.format("%.0f", user.testData().spends().getFirst().amount()))
                .checkStatisticImage(expected)
                .checkBubbles(new Bubble(Color.yellow, "Archived 2000 ₽"));
    }

    @DisplayName("Проверяем, что состояние диаграммы трат имеет Bubbles в любом порядке")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    ),
                    @Spending(category = "Сосиска",
                            description = "Съесть сосиску",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @Test
    void checkBubblesInAnyOderTest() {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkBubblesInAnyOrder(
                        new Bubble(Color.yellow, "Обучение 79990 ₽"),
                        new Bubble(Color.green, "Сосиска 5000 ₽"),
                        new Bubble(Color.orange, "Pet a duck 2000 ₽")
                );
    }

    @DisplayName("Проверка содержания в состоянии диаграммы цветов и текстов для искомых трат")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    ),
                    @Spending(category = "Сосиска",
                            description = "Съесть сосиску",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @Test
    void checkStatComponentContainsBubblesTest() {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkBubblesContains(
                        new Bubble(Color.yellow, "Обучение 79990 ₽"),
                        new Bubble(Color.orange, "Pet a duck 2000 ₽")
                );
    }

    @DisplayName("Проверка наличия в таблице статистики трат искомых")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    )
            }

    )
    @ApiLogin
    @Test
    void checkSpendExistTest(UserJson user) {
        SpendJson[] expectedSpends = user.testData().spends().toArray(SpendJson[]::new);
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getSpendingTable()
                .checkSpendingTable(expectedSpends);
    }
}

