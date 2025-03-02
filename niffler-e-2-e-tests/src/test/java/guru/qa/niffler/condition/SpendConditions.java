package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.rest.SpendJson;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SpendConditions {

    private static final int CATEGORY_COLUMN_NUMBER = 1;
    private static final int AMOUNT_COLUMN_NUMBER = 2;
    private static final int DESCRIPTION_COLUMN_NUMBER = 3;
    private static final int DATE_COLUMN_NUMBER = 4;

    @Nonnull
    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        return new WebElementsCondition() {

            private String expectedSpend;

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (expectedSpends.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedSpends.length, elements.size());
                    return rejected(message, elements);
                }

                ElementsCollection rows = $$(elements);

                for (int i = 0; i < rows.size(); i++) {
                    ElementsCollection cells = rows.get(i).$$("td");

                    if (!cells.get(CATEGORY_COLUMN_NUMBER).getText().equals(expectedSpends[i].category().name())) {
                        expectedSpend = expectedSpends[i].category().name();
                        String message = String.format(
                                "Spend category mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(CATEGORY_COLUMN_NUMBER).getText()
                        );
                        return rejected(message, cells.get(CATEGORY_COLUMN_NUMBER).getText());

                    } else if (!cells.get(AMOUNT_COLUMN_NUMBER).getText().equals(expectedSpends[i].amount().intValue() + " ₽")) {
                        expectedSpend = expectedSpends[i].amount().intValue() + " ₽";
                        String message = String.format(
                                "Spend amount mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(AMOUNT_COLUMN_NUMBER).getText()
                        );
                        return rejected(message, cells.get(AMOUNT_COLUMN_NUMBER).getText());

                    } else if (!cells.get(DESCRIPTION_COLUMN_NUMBER).getText().equals(expectedSpends[i].description())) {
                        expectedSpend = expectedSpends[i].description();
                        String message = String.format(
                                "Spend description mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(DESCRIPTION_COLUMN_NUMBER).getText()
                        );
                        return rejected(message, cells.get(DESCRIPTION_COLUMN_NUMBER).getText());

                    } else if (!cells.get(DATE_COLUMN_NUMBER).getText().equals(dateFormat(expectedSpends[i].spendDate()))) {
                        expectedSpend = dateFormat(expectedSpends[i].spendDate());
                        String message = String.format(
                                "Spend date mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(DATE_COLUMN_NUMBER).getText()
                        );
                        return rejected(message, cells.get(DATE_COLUMN_NUMBER).getText());
                    }
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedSpend;
            }
        };
    }

    @Nonnull
    private static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return formatter.format(date);
    }

}
