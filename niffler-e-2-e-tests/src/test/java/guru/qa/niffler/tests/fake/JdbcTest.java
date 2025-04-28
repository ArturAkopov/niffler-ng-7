//package guru.qa.niffler.tests.fake;
//
//import guru.qa.niffler.jupiter.extension.InjectClientExtension;
//import guru.qa.niffler.model.rest.CategoryJson;
//import guru.qa.niffler.model.CurrencyValues;
//import guru.qa.niffler.model.rest.SpendJson;
//import guru.qa.niffler.model.rest.UserJson;
//import guru.qa.niffler.service.SpendClient;
//import guru.qa.niffler.service.UsersClient;
//import guru.qa.niffler.utils.RandomDataUtils;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import java.util.Date;
//
//@ExtendWith(InjectClientExtension.class)
//public class JdbcTest {
//
//    private UsersClient usersClient;
//    private SpendClient spendClient;
//
//    @Test
//    void txTest() {
//        SpendJson spend = spendClient.createSpend(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                RandomDataUtils.randomCategoryName(),
//                                RandomDataUtils.randomUsername(),
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        1000.0,
//                        RandomDataUtils.randomCategoryName(),
//                        RandomDataUtils.randomUsername()
//                )
//        );
//
//        System.out.println(spend);
//    }
//
//
//    @Test
//    void springJdbcTest() {
//        UserJson user = usersClient.createUser(
//                RandomDataUtils.randomUsername(),
//                "12345"
//        );
//
//        usersClient.addIncomeInvitation(user, 1);
//        usersClient.addOutcomeInvitation(user, 1);
//    }
//}