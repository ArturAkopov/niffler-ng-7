package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrowserConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (context.getParameter().getType() != SelenideDriver.class) {
            throw new ArgumentConversionException("Target type must be SelenideDriver");
        }

        if (Browser.CHROME == source) {
            return new SelenideDriver(Browser.CHROME.getConfig());
        } else if (Browser.FIREFOX == source) {
            return new SelenideDriver(Browser.FIREFOX.getConfig());
        } else {
            throw new ArgumentConversionException("Unsupported browser type: " + source);
        }
    }
}
