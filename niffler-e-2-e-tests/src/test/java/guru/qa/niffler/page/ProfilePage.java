package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {
    private final SelenideElement
            uploadNewPictureButton = $(".css-1f8fote"),
            nameInput = $("input[id='name']"),
            saveChangesButton = $("button[id=':r9:']"),
            categoryInput = $("input[id='category']"),
            showArchivedCheckbox = $("input[type='checkbox']");

    @Step("Загрузка изображения <file> на странице профиля")
    public ProfilePage uploadImage(File file) {
        uploadNewPictureButton.uploadFile(file);
        return this;
    }

    @Step("Ввод имени пользователя <name> на странице профиля")
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }


    @Step("Ввод новой категории <newCategory> на странице профиля")
    public ProfilePage setNewCategory(String newCategory) {
        categoryInput.setValue(newCategory);
        return this;
    }

    @Step("Нажатие на чек-бокс отображения архивных категорий на странице профиля")
    public ProfilePage clickArchivedCheckbox() {
        showArchivedCheckbox.click();
        return this;
    }

    @Step("Проверка нахождения категории <categoryName> в отображаемом списке на странице профиля")
    public void checkCategoryInCategoryList(String categoryName){
        $$(".css-17u3xlq").findBy(text(categoryName)).shouldBe(visible);
    }

    @Step("Сохранение изменений на странице пользователя")
    public void saveChanges() {
        saveChangesButton.click();
    }


}
