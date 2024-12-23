package guru.qa.niffler.tests.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(BrowserExtension.class)
@DisplayName("Web тесты на регистрацию")
public class RegistrationWebTests {

    static final Faker faker = new Faker();
    static final String ExistUserName = "Artur";
    static final String password = "12345";

    @DisplayName("Тест на успешную регистрацию нового пользователя")
    @Test
    void shouldRegisterNewUser() {
        new RegisterPage()
                .open()
                .setUserName(faker.name().username())
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkSuccessfulRegistration("Congratulations! You've registered!");
    }

    @DisplayName("Тест на невозможность регистрации с существующим именем пользователя")
    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        new RegisterPage()
                .open()
                .setUserName(ExistUserName)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkUnsuccessfulRegistrationWithExistUserName("Username `Artur` already exists");
    }

    @DisplayName("Тест на невозможность регистрации с неодинаковыми паролем и подтверждающим паролем")
    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        new RegisterPage()
                .open()
                .setUserName(ExistUserName)
                .setPassword(password)
                .setPasswordSubmit(faker.internet().password())
                .submitRegistration()
                .checkUnsuccessfulRegistrationIfPasswordAndConfirmPasswordAreNotEqual("Passwords should be equal");
    }

}
