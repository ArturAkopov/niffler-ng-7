package guru.qa.niffler.tests.web;

import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;


@WebTest
public class SpendingWebTest {


    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @DisabledByIssue("5")
    @DisplayName("Название категории должно быть изменено в таблице")
    @Test
    void categoryDescriptionShouldBeChangedFromTable(@NotNull UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .editSpendingClick(user.testData().spends().getFirst().description())
                .editDescription(newDescription)
                .saveChange()
                .checkAlertMessage("Spending is edited successfully")
                .checkThatTableContainsSpending(newDescription);
    }

    @User
    @DisplayName("Должна быть добавлена новая трата")
    @Test
    void shouldAddNewSpending(@NotNull UserJson user) {
        String category = "Duck";
        int amount = 100;
        LocalDate currentDate = LocalDate.now();
        String description = RandomDataUtils.randomSentence(1);

        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .addSpendingPage()
                .editCategory(category)
                .editAmount(amount)
                .editDate(currentDate)
                .editDescription(description)
                .saveChange()
                .checkAlertMessage("New spending is successfully created")
                .getSpendingTable()
                .checkTableContains(description);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @DisplayName("Проверка компонента статистики")
    @ScreenShotTest(value = "image/expected-stat.png", rewriteExpected = true)
    void checkStatComponentTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
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
    @ScreenShotTest(value = "image/expected-edit-stat.png")
    void checkStatComponentAfterEditSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        String newAmount = "2000";
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
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
    @ScreenShotTest(value = "image/expected-delete-stat.png",
            rewriteExpected = true)
    void checkStatComponentAfterDeleteSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
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
    @ScreenShotTest(value = "image/expected-archived-stat.png")
    void checkStatComponentAfterArchivedCategoryTest(@NotNull UserJson user, BufferedImage expected) throws IOException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
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
    @Test
    void checkBubblesInAnyOderTest(UserJson user) {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
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
    @Test
    void checkStatComponentContainsBubblesTest(UserJson user) {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
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
    @Test
    void checkSpendExistTest(UserJson user) {

        SpendJson[] expectedSpends = user.testData().spends().toArray(SpendJson[]::new);
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getSpendingTable()
                .checkSpendingTable(expectedSpends);
    }
}

