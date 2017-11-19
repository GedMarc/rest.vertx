package com.zandero.rest.writer;

import com.zandero.rest.RestRouter;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.utils.Assert;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * Produces vert.x response based on JAX-RS response builder output
 */
public class JaxResponseWriter implements HttpResponseWriter<Response> {

	@Override
	public void write(Response result, HttpServerRequest request, HttpServerResponse response) {

		Assert.notNull(result, "Expected result but got null!");

		response.setStatusCode(result.getStatus());
		addHeaders(result, response);

		if (result.getEntity() != null) {

			// try to find appropriate writer ...
			String mediaType = response.headers().get(HttpHeaders.CONTENT_TYPE);

			HttpResponseWriter writer;
			try {
				writer = RestRouter.getWriters().get(mediaType);
			}
			catch (ClassFactoryException e) {
				writer = null;
			}

			if (writer != null) {
				writer.write(result.getEntity(), request, response);
			} else {
				response.end(result.getEntity().toString());
			}
		} else {
			response.end();
		}
	}

	private static void addHeaders(Response jaxrsResponse, HttpServerResponse response) {

		if (jaxrsResponse.getMetadata() != null) {

			List<Object> cookies = jaxrsResponse.getMetadata().get(HttpHeaders.SET_COOKIE.toString());
			if (cookies != null) {

				Iterator<Object> it = cookies.iterator();
				while (it.hasNext()) {
					Object next = it.next();
					if (next instanceof NewCookie) {

						NewCookie cookie = (NewCookie) next;
						response.putHeader(HttpHeaders.SET_COOKIE, cookie.toString());

						it.remove();
					}
				}

				if (cookies.size() < 1) {
					jaxrsResponse.getMetadata().remove(HttpHeaders.SET_COOKIE.toString());
				}
			}
		}

		if (jaxrsResponse.getMetadata() != null && jaxrsResponse.getMetadata().size() > 0) {

			for (String name : jaxrsResponse.getMetadata().keySet()) {
				List<Object> meta = jaxrsResponse.getMetadata().get(name);

				if (meta != null && meta.size() > 0) {
					for (Object item : meta) {
						if (item != null) {
							response.putHeader(name, item.toString());
						}
					}
				}
			}
		}
	}
}
