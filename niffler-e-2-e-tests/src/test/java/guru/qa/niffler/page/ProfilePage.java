package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<MainPage> {

    public static final String PROFILE_PAGE_URL = CONFIG.frontUrl() + "profile";

    private final Header header = new Header();

    private final SelenideElement
            uploadNewPictureButton = $("input#image__input"),
            nameInput = $("input#name"),
            saveChangesButton = $("button[type='submit']"),
            categoryInput = $("input#category"),
            showArchivedCheckbox = $("input[type='checkbox']"),
            alert = $(".MuiSnackbar-root"),
            avatarImage = $(".MuiAvatar-img");

    private final ElementsCollection
            listCategory = $("div").$$("div[role='button']"),
            archiveSubmitButtons = $$("div[role='dialog'] button");

    @Nonnull
    @Step("Загрузка изображения <file> на странице профиля")
    public ProfilePage uploadImage(String file) {
        uploadNewPictureButton.uploadFromClasspath(file);
        return this;
    }

    @Nonnull
    @Step("Ввод имени пользователя <name> на странице профиля")
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Ввод новой категории <newCategory> на странице профиля")
    public ProfilePage setNewCategory(String newCategory) {
        categoryInput.setValue(newCategory);
        return this;
    }

    @Nonnull
    @Step("Нажатие на чек-бокс отображения архивных категорий на странице профиля")
    public ProfilePage clickArchivedCheckbox() {
        showArchivedCheckbox.click();
        return this;
    }


    @Step("Проверка нахождения категории <categoryName> в отображаемом списке на странице профиля")
    public void checkCategoryInCategoryList(String categoryName) {
        $$(".css-17u3xlq").findBy(text(categoryName)).shouldBe(visible);
    }

    @Nonnull
    @Step("Сохранение изменений на странице пользователя")
    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Step("Архивирование категории {categoryName}")
    public ProfilePage archivedCategory(String categoryName) {
        listCategory.find(text(categoryName)).parent().parent().$("button[aria-label='Archive category']").click();
        archiveSubmitButtons.find(text("Archive")).click();
        return this;
    }
}
