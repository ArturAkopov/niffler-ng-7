package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SearchField searchField = new SearchField();

    private final SelenideElement
            periodMenu = self.$("#period"),
            currencyMenu = self.$("#currency"),
            deleteButton = self.$("#delete"),
            popup = $("div[role='dialog']");

    private final ElementsCollection
            menuItems = $$(".MuiList-padding li"),
            tableRows = self.$("tbody").$$("tr");


    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Выбор периода в таблице затрат <period>")
    @Nonnull
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodMenu.click();
        menuItems.find(text(period.text)).click();
        return this;
    }

    @Step("Изменение траты <description>")
    @Nonnull
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description))
                .$("button[aria-label='Edit spending']").click();
        return new EditSpendingPage();
    }

    @Step("Удаление траты <description>")
    @Nonnull
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description)).$("input[type='checkbox']").click();
        deleteButton.click();
        popup.$(byText("Delete")).click();
        return this;
    }

    @Step("Поиск траты <description>")
    @Nonnull
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Проверка нахождения траты <expectedSpend> в таблице")
    @Nonnull
    public SpendingTable checkTableContains(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description)).should(visible);
        return this;
    }

    @Step("Проверка нахождения в таблице необходимого количества трат <expectedSize>")
    @Nonnull
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }

    @Step("Проверка нахождения в таблице трат искомых <expectedSpends>")
    @Nonnull
    public SpendingTable checkSpendingTable(SpendJson... expectedSpends) {
        tableRows.should(SpendConditions.spends(expectedSpends));
        return this;
    }
}
