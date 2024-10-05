package com.zandero.resttest;

import com.zandero.rest.RestBuilder;
import com.zandero.resttest.test.ErrorThrowingRest2;
import com.zandero.resttest.test.handler.BaseExceptionHandler;
import com.zandero.resttest.test.handler.InheritedBaseExceptionHandler;
import com.zandero.resttest.test.handler.InheritedFromInheritedExceptionHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteExceptionHandlerTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .register(ErrorThrowingRest2.class)
                            .errorHandler(new InheritedFromInheritedExceptionHandler())
                            .errorHandler(InheritedBaseExceptionHandler.class,
                                          BaseExceptionHandler.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void throwOne(VertxTestContext context) {


        client.get(PORT, HOST, "/throw/exception/one").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("BaseExceptionHandler: BASE: first", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwTwo(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/exception/two").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("InheritedBaseExceptionHandler: BASE: INHERITED: second", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwThree(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/exception/three").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("InheritedFromInheritedExceptionHandler: BASE: INHERITED: INHERITED FROM: third", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwFour(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/exception/four").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("com.zandero.resttest.test.handler.MyExceptionClass", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }
}
