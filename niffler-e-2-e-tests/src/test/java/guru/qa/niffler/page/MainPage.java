package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.SpendingTable;
import guru.qa.niffler.page.components.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class MainPage extends BasePage<MainPage> {

    private final static String MAIN_PAGE_URL = CONFIG.frontUrl() + "login";

    private final SearchField searchField = new SearchField();
    private final Header header;
    private final SpendingTable spendingTable = new SpendingTable();
    private final StatComponent statComponent = new StatComponent();

    private final SelenideElement pieChart;

    private final ElementsCollection tableRows;

    public MainPage() {
        this.pieChart = $("canvas[role='img']");
        this.tableRows = $("#spendings tbody").$$("tr");
        this.header = new Header();
    }

    public MainPage(SelenideDriver driver) {
        super(driver);
        this.pieChart = driver.$("canvas[role='img']");
        this.tableRows = driver.$("#spendings tbody").$$("tr");
        this.header = new Header(driver);
    }

    @Step("Нажатие кнопки изменения траты <spendingDescription>")
    @Nonnull
    public EditSpendingPage editSpendingClick(String spendingDescription) {
        searchField.search(spendingDescription);
        tableRows.find(text(spendingDescription)).$("td", 5).click();

        return new EditSpendingPage();
    }

    @Step("Проверка нахождения траты <spendingDescription> в таблице трат")
    public void checkThatTableContainsSpending(String spendingDescription) {
        searchField.search(spendingDescription);
        tableRows.find(text(spendingDescription)).shouldBe(visible);
    }

    @Nonnull
    public StatComponent getStatComponent() {
        statComponent.getSelf().scrollIntoView(true);
        return statComponent;
    }
}