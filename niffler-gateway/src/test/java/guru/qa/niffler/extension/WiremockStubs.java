package guru.qa.niffler.extension;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WiremockJsonStubExtension.class)
public @interface WiremockStubs {
    String [] configPath();
    int port();
}
