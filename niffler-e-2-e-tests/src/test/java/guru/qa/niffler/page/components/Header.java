package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

    private final SelenideElement
            mainPageLink,
            newSpendingButton,
            menuButton,
            profileButton,
            friendsButton,
            allPeopleButton,
            signOutButton;

    public Header(SelenideElement self) {
        super(self);
        this.mainPageLink = self.$("a[href*='/main']");
        this.newSpendingButton = self.$("a[href*='/spending']");
        this.menuButton = self.$("button[aria-label='Menu']");
        this.profileButton = $("a[href*='/profile']");
        this.friendsButton = $("a[href*='/people/friends']");
        this.allPeopleButton = $("a[href*='/people/all']");
        this.signOutButton = $$("li").find(text("Sign out"));
    }

    public Header() {
        super($("#root header"));
        this.mainPageLink = self.$("a[href*='/main']");
        this.newSpendingButton = self.$("a[href*='/spending']");
        this.menuButton = self.$("button[aria-label='Menu']");
        this.profileButton = $("a[href*='/profile']");
        this.friendsButton = $("a[href*='/people/friends']");
        this.allPeopleButton = $("a[href*='/people/all']");
        this.signOutButton = $$("li").find(text("Sign out"));
    }

    public Header(SelenideDriver driver) {
        super(driver.$("#root header"));
        this.mainPageLink = self.$("a[href*='/main']");
        this.newSpendingButton = self.$("a[href*='/spending']");
        this.menuButton = self.$("button[aria-label='Menu']");
        this.profileButton = driver.$("a[href*='/profile']");
        this.friendsButton = driver.$("a[href*='/people/friends']");
        this.allPeopleButton = driver.$("a[href*='/people/all']");
        this.signOutButton = driver.$$("li").find(text("Sign out"));
    }

    @Nonnull
    @Step("Нажатие на меню аккаунта ")
    public Header clickAccountMenuButton() {
        menuButton.click();
        return this;
    }

    @Nonnull
    @Step("Переход на страницу 'Профиль'")
    public ProfilePage toProfilePage() {
        menuButton.click();
        profileButton.click();
        return new ProfilePage();
    }

    @Nonnull
    @Step("Переход на страницу 'Профиль'")
    public ProfilePage toProfilePage(SelenideDriver driver) {
        menuButton.click();
        profileButton.click();
        return new ProfilePage(driver);
    }

    @Nonnull
    @Step("Переход на страницу 'Друзья'")
    public FriendsPage toFriendsPage() {
        menuButton.click();
        friendsButton.click();
        return new FriendsPage();
    }

    @Nonnull
    @Step("Переход на страницу 'Все люди'")
    public PeoplePage toAllPeoplesPage() {
        menuButton.click();
        allPeopleButton.click();
        return new PeoplePage();
    }

    @Step("Выход из профиля клиента")
    @Nonnull
    public LoginPage signOut() {
        menuButton.click();
        signOutButton.click();
        return new LoginPage();
    }

    @Step("Добавить новую статью затрат")
    @Nonnull
    public EditSpendingPage addSpendingPage() {
        newSpendingButton.click();
        return new EditSpendingPage();
    }

    @Step("Переход на главную страницу")
    @Nonnull
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }
}

