package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class NavigateMenu {
    private final SelenideElement
            accountMenuButton = $(".css-1obba8g"),
            profileButton = $(".css-z5w4ww");

    @Step("Нажатие на меню аккаунта ")
    public NavigateMenu clickAccountMenuButton() {
        accountMenuButton.click();
        return this;
    }

    @Step("Нажатие на кнопку Профиля")
    public ProfilePage clickProfileButton() {
        profileButton.click();
        return new ProfilePage();
    }
}
