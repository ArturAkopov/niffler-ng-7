package guru.qa.niffler.page.components;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    public StatComponent() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");

    @Step("Проверяем в блоке Statistics ячейку с категорией и суммой")
    public StatComponent checkCellCategoryAndAmountInStatisticsBlock(String categoryName, String amount) {
        bubbles.findBy(text(categoryName))
                .shouldHave(text(amount));
        return this;
    }

    @Step("Проверяем в блоке Statistics наличие текста {0}")
    @Nonnull
    public StatComponent checkStatisticBubblesContains(String... texts) {
        bubbles.should(CollectionCondition.texts(texts));
        return this;
    }

    @Step("Проверка совпадения актуального изображения статистики с искомым")
    @Nonnull
    public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
        Selenide.sleep(2000);
        assertFalse(
                new ScreenDiffResult(
                        chartScreenshot(),
                        expectedImage
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    @Step("Скриншот диаграммы статистики")
    @Nonnull
    public BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(chart.screenshot()));
    }

    @Step("Проверяем, что состояние диаграммы трат имеет {expectedBubbles}")
    @Nonnull
    public StatComponent checkBubbles(Bubble expectedBubbles) {
        bubbles.first().should(StatConditions.statBubbles(expectedBubbles));
        return this;
    }

    @Step("Проверяем, что состояние диаграммы трат имеет <expectedBubbles> в любом порядке")
    @Nonnull
    public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Step("Проверяем, что состояние диаграммы трат имеет среди прочих искомые <expectedBubbles>")
    @Nonnull
    public StatComponent checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesContains(expectedBubbles));
        return this;
    }
}
