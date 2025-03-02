package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgba) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgba.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition color(Color... expectedColors) {
        return new WebElementsCondition() {

            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgba).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColors.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgba.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    @Nonnull
    public static WebElementCondition statBubbles(Bubble expectedBubble) {
        return new WebElementCondition("text " + expectedBubble.text() + ", color " + expectedBubble.color()) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String actualColor = element.getCssValue("background-color");
                final String actualText = element.getText();

                if (!expectedBubble.color().rgba.equals(actualColor)) {
                    final String message = String.format(
                            "Bubble color mismatch (expected: %s, actual: %s)", expectedBubble.color().rgba, actualColor
                    );
                    return rejected(message, actualColor);
                }

                if (!expectedBubble.text().equals(actualText)) {
                    final String message = String.format(
                            "Bubble text mismatch (expected: %s, actual: %s)", expectedBubble.text(), actualText
                    );
                    return rejected(message, actualColor);
                }

                return accepted();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final List<Bubble> expectedBubbleList = Arrays.stream(expectedBubbles).toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements.toString());
                }

                boolean passed = true;
                final List<Bubble> actualBubblesList = new ArrayList<>();
                for (final WebElement element : elements) {
                    final Bubble actualBubble = new Bubble(
                            Color.fromRgba(element.getCssValue("background-color")),
                            element.getText());
                    actualBubblesList.add(actualBubble);

                    if (passed) {
                        passed = Arrays.asList(expectedBubbles).contains(actualBubble);
                    }
                }

                if (!passed) {
                    final String actualBubbles = actualBubblesList.toString();
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbleList, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbleList.toString();
            }

        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final List<Bubble> expectedBubbleList = Arrays.stream(expectedBubbles).toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length > elements.size()) {
                    final String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements.toString());
                }

                final List<Bubble> actualBubblesList = new ArrayList<>();
                for (final WebElement element : elements) {
                    final Bubble actualBubble = new Bubble(
                            Color.fromRgba(element.getCssValue("background-color")),
                            element.getText());
                    actualBubblesList.add(actualBubble);
                }

                boolean passed = Arrays.stream(expectedBubbles).anyMatch(actualBubblesList::contains);

                if (!passed) {
                    final String actualBubbles = actualBubblesList.toString();
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbleList, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbleList.toString();
            }
        };
    }
}
