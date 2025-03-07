package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Browser {

    CHROME(chromeConfig()),
    FIREFOX(firefoxConfig());

    private final SelenideConfig config;

    private static SelenideConfig chromeConfig() {
        return new SelenideConfig()
                .browser("chrome")
                .pageLoadStrategy("eager");
    }

    private static SelenideConfig firefoxConfig() {
        return new SelenideConfig()
                .browser("firefox")
                .pageLoadStrategy("eager");
    }
}
