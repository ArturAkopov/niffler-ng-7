package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;

public class BrowserExtension implements BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    static {
        Configuration.pageLoadStrategy = "eager";
        Configuration.browser = "firefox".equals(System.getProperty("browser")) ? "firefox" : "chrome";
        if ("docker".equals(System.getProperty("test.env"))) {
            Configuration.remote = "http://selenoid:4444/wd/hub";
            if ("firefox".equals(System.getProperty("browser"))) {
                Configuration.browserVersion = "125.0";
            } else {
                Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
                Configuration.browserVersion = "127.0";
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false));
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenShot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenShot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        throw throwable;
    }

    private static void doScreenShot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
