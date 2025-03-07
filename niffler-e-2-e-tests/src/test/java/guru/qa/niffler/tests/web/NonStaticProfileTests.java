package guru.qa.niffler.tests.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.converter.Browser;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.jupiter.extension.NonStaticBrowsersExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Тесты для страницы профиля пользователя")
public class NonStaticProfileTests {

    @RegisterExtension
    private static final NonStaticBrowsersExtension nonStaticBrowsersExtension = new NonStaticBrowsersExtension();

    @User(
            categories = @Category(archived = true)
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    @DisplayName("Архивная категория должна присутствовать и отображаться в списке категорий")
    void archivedCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, @NotNull UserJson user) {
        nonStaticBrowsersExtension.drivers().add(driver);
        new LoginPage(driver)
                .open(driver)
                .login(user.username(), user.testData().password(), driver)
                .getHeader()
                .toProfilePage(driver)
                .clickArchivedCheckbox()
                .checkCategoryInCategoryList(user.testData().categories().getFirst().name(),driver);
    }

    @User(
            categories = @Category
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    @DisplayName("Активная категория должна присутствовать и отображаться в списке категорий")
    void activeCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, @NotNull UserJson user) {
        nonStaticBrowsersExtension.drivers().add(driver);
        new LoginPage(driver)
                .open(driver)
                .login(user.username(), user.testData().password(),driver)
                .getHeader()
                .toProfilePage(driver)
                .checkCategoryInCategoryList(user.testData().categories().getFirst().name(),driver);
    }

    @User
    @DisplayName("Обновление всех полей профиля")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void updateAllFieldsProfile(@ConvertWith(BrowserConverter.class) SelenideDriver driver, @NotNull UserJson user) {
        nonStaticBrowsersExtension.drivers().add(driver);
        new LoginPage(driver)
                .open(driver)
                .login(user.username(), user.testData().password(),driver)
                .getHeader()
                .toProfilePage(driver)
                .uploadImage("image/duck.png")
                .setName(RandomDataUtils.randomName())
                .setNewCategory(RandomDataUtils.randomCategoryName())
                .saveChanges()
                .checkAlertMessage("Profile successfully updated");
    }
}
