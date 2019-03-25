package com.zandero.rest;

import com.zandero.http.HttpUtils;
import com.zandero.rest.test.TestPathRest;
import com.zandero.rest.test.TestPostRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteRegistrationTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		TestRest testRest = new TestRest();
		TestPostRest testPostRest = new TestPostRest();

		Router router = RestRouter.register(vertx, testRest, testPostRest);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void registerTwoRoutesTest(TestContext context) throws IOException {

		final Async async = context.async();

		// check if both are active
		Dummy json = new Dummy("test", "me");
		StringEntity input = new StringEntity(JsonUtils.toJson(json));
		input.setContentType("application/json");

		// 2nd REST
		HttpPost request = (HttpPost) HttpUtils.post(ROOT_PATH + "/post/json", null, null, input, null);
		HttpResponse response = HttpUtils.execute(request);

		assertEquals(200, response.getStatusLine().getStatusCode());
		String output = HttpUtils.getContentAsString(response);
		assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", output);

		async.complete();
	}

	@Test
	public void registerTwoRoutesSeparate(TestContext context) throws IOException {

		final Async async = context.async();

		// check if both are active
		Dummy json = new Dummy("test", "me");
		StringEntity input = new StringEntity(JsonUtils.toJson(json));
		input.setContentType("application/json");

		HttpPost request = (HttpPost) HttpUtils.post(ROOT_PATH + "/test/json/post", null, null, input, null);
		HttpResponse response = HttpUtils.execute(request);

		assertEquals(200, response.getStatusLine().getStatusCode());
		String output = HttpUtils.getContentAsString(response);
		assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", output);

		// 2nd REST
		request = (HttpPost) HttpUtils.post(ROOT_PATH + "/post/json", null, null, input, null);
		response = HttpUtils.execute(request);

		assertEquals(200, response.getStatusLine().getStatusCode());
		output = HttpUtils.getContentAsString(response);
		assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", output);

		async.complete();
	}
}
