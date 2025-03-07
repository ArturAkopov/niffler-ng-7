package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private final static String LOGIN_PAGE_URL = CONFIG.frontUrl() + "login";

    private final SelenideElement
            usernameInput,
            passwordInput,
            submitButton;

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitButton = $("button[type='submit']");
    }

    public LoginPage(SelenideDriver driver) {
        super(driver);
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitButton = driver.$("button[type='submit']");
    }

    @Step("Ввод логина <username> и пароля <password>")
    @Nonnull
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();

        return new MainPage();
    }

    @Step("Ввод логина <username> и пароля <password>")
    @Nonnull
    public MainPage login(String username, String password, SelenideDriver driver) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();

        return new MainPage(driver);
    }

    @Step("Открытие страницы авторизации")
    @Nonnull
    public LoginPage open() {
        Selenide.open(LOGIN_PAGE_URL);
        return this;
    }

    @Step("Открытие страницы авторизации")
    @Nonnull
    public LoginPage open(SelenideDriver driver) {
        driver.open(LOGIN_PAGE_URL);
        return this;
    }
}