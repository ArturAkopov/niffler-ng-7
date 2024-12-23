package guru.qa.niffler.tests.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.components.NavigateMenu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тесты для страницы профиля пользователя")
public class ProfileTests {

    @Category(
            username = "Artur",
            archived = true
    )
    @DisplayName("Архивная категория должна присутствовать и отображаться в списке категорий")
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open("http://127.0.0.1:9000/login", LoginPage.class)
                .login(category.username(), "12345");
        new NavigateMenu().clickAccountMenuButton()
                .clickProfileButton()
                .clickArchivedCheckbox()
                .checkCategoryInCategoryList(category.name());
    }

    @Category(
            username = "Artur",
            archived = false
    )
    @DisplayName("Активная категория должна присутствовать и отображаться в списке категорий")
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open("http://127.0.0.1:9000/login", LoginPage.class)
                .login(category.username(), "12345");
        new NavigateMenu().clickAccountMenuButton()
                .clickProfileButton()
                .checkCategoryInCategoryList(category.name());
    }
}
