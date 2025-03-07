package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CONFIG = Config.getInstance();

    private final SelenideElement alert;

    public BasePage() {
        this.alert = $(".MuiSnackbar-root");
    }

    public BasePage(SelenideDriver driver) {
        this.alert = driver.$(".MuiSnackbar-root");
    }

    @Step("Проверка, что alert-message содержит текст <expectedText>")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(visible).should(text(expectedText));
        return (T) this;
    }
}
