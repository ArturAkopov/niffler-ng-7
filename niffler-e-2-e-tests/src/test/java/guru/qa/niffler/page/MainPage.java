package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.page.components.NavigateMenuComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    public final NavigateMenuComponent navigateMenuComponent = new NavigateMenuComponent();

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$("td", 5).click();

        return new EditSpendingPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).shouldBe(visible);
    }
}