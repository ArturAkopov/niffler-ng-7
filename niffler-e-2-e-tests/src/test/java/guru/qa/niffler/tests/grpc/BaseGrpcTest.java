package guru.qa.niffler.tests.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    private static final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
            .usePlaintext()
            .intercept(new GrpcConsoleInterceptor())
            .build();

    protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub blockingStub
            = NifflerCurrencyServiceGrpc.newBlockingStub(channel);
}

