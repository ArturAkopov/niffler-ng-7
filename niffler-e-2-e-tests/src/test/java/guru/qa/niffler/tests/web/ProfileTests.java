package guru.qa.niffler.tests.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


@DisplayName("Тесты для страницы профиля пользователя")
@WebTest
public class ProfileTests {

    @User(
            username = "Artur",
            categories = @Category( archived = true)
    )
    @DisplayName("Архивная категория должна присутствовать и отображаться в списке категорий")
    @Test
    void archivedCategoryShouldPresentInCategoriesList(@NotNull CategoryJson[] category) {
        new LoginPage()
                .open()
                .login(category[0].username(), "12345")
                .getHeader()
                .toProfilePage()
                .clickArchivedCheckbox()
                .checkCategoryInCategoryList(category[0].name());
    }

    @User(
            categories = @Category
    )
    @DisplayName("Активная категория должна присутствовать и отображаться в списке категорий")
    @Test
    void activeCategoryShouldPresentInCategoriesList(@NotNull UserJson user) {
        new LoginPage()
                .open()
                .login(user.testData().categories().getFirst().username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .checkCategoryInCategoryList(user.testData().categories().getFirst().name());
    }

    @User
    @DisplayName("Обновление всех полей профиля")
    @Test
    void updateAllFieldsProfile(@NotNull UserJson user) {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .uploadImage("image/duck.png")
                .setName(RandomDataUtils.randomName())
                .setNewCategory(RandomDataUtils.randomCategoryName())
                .saveChanges()
                .checkAlertMessage("Profile successfully updated");
    }

    @DisplayName("Проверка корректной загрузки аватарки")
    @User
    @ScreenShotTest(value = "image/expected-avatar.png")
    void checkCorrectUploadAvatar(@NotNull UserJson user, BufferedImage expected) throws IOException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .uploadImage("image/duck.png")
                .saveChanges();

        BufferedImage actual = ImageIO.read(Objects.requireNonNull(new ProfilePage().getAvatarImage().screenshot()));
        Assertions.assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }
}
