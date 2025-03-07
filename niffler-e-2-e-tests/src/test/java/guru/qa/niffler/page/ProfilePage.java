package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Getter
@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<MainPage> {

    public static final String PROFILE_PAGE_URL = CONFIG.frontUrl() + "profile";

    private final Header header;

    private final SelenideElement
            uploadNewPictureButton,
            nameInput,
            saveChangesButton,
            categoryInput,
            showArchivedCheckbox,
            alert,
            avatarImage,
            avatar;

    private final ElementsCollection
            listCategory,
            archiveSubmitButtons;

    public ProfilePage() {
        this.header = new Header();
        this.uploadNewPictureButton = $("input#image__input");
        this.nameInput = $("input#name");
        this.saveChangesButton = $("button[type='submit']");
        this.categoryInput = $("input#category");
        this.showArchivedCheckbox = $("input[type='checkbox']");
        this.alert = $(".MuiSnackbar-root");
        this.avatarImage = $(".MuiAvatar-img");
        this.avatar = $("#image__input").parent().$("img");
        this.listCategory = $("div").$$("div[role='button']");
        this.archiveSubmitButtons = $$("div[role='dialog'] button");
    }

    public ProfilePage(SelenideDriver driver) {
        super(driver);
        this.header = new Header(driver);
        this.uploadNewPictureButton = driver.$("input#image__input");
        this.nameInput = driver.$("input#name");
        this.saveChangesButton = driver.$("button[type='submit']");
        this.categoryInput = driver.$("input#category");
        this.showArchivedCheckbox = driver.$("input[type='checkbox']");
        this.alert = driver.$(".MuiSnackbar-root");
        this.avatarImage = driver.$(".MuiAvatar-img");
        this.avatar = driver.$("#image__input").parent().$("img");
        this.listCategory = driver.$("div").$$("div[role='button']");
        this.archiveSubmitButtons = driver.$$("div[role='dialog'] button");
    }

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
        $$(".MuiChip-filled.MuiChip-clickable").findBy(text(categoryName)).shouldBe(visible);
    }

    @Step("Проверка нахождения категории <categoryName> в отображаемом списке на странице профиля")
    public void checkCategoryInCategoryList(String categoryName,SelenideDriver driver) {
        driver.$$(".MuiChip-filled.MuiChip-clickable").findBy(text(categoryName)).shouldBe(visible);
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

    @Step("Проверка фото")
    @Nonnull
    public ProfilePage checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }
}
