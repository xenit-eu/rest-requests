package eu.xenit.restrequests.impl.jdk11;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;

class JdkResponseBodyHandler<TResponse> implements BodyHandler<TResponse> {

    protected final JdkHttpContext httpContext;
    protected final Class<TResponse> type;

    public JdkResponseBodyHandler(JdkHttpContext httpContext, Class<TResponse> type) {
        this.httpContext = httpContext;
        this.type = type;
    }

    @Override
    public BodySubscriber<TResponse> apply(ResponseInfo responseInfo) {

        httpContext.setResponseInfo(responseInfo);
        httpContext.filterProcessor().onResponse(this.httpContext);

        // buffer response, making sure we don't leak input streams right now
        BodySubscriber<byte[]> upstream = BodySubscribers.ofByteArray();

        // convert the byte array to an input stream so filters can nicely wrap
        BodySubscriber<InputStream> middleware = BodySubscribers.mapping(upstream, (byteArray) -> {
            var inputStream = new ByteArrayInputStream(byteArray);
            return httpContext.filterProcessor().filterBodyResponseStream(inputStream);
        });

        // let the converterProcessor figure out what to do with the input stream
        return BodySubscribers.mapping(middleware, (inputStream) -> {
            var context = new JdkResponseInfoDeserializationContext(responseInfo, inputStream);
            return this.httpContext.converterProcessor().read(context, type);
        });
    }

    public static class DiscardingJdkResponseBodyHandler extends JdkResponseBodyHandler<Void> {

        public DiscardingJdkResponseBodyHandler(JdkHttpContext httpContext) {
            super(httpContext, Void.class);
        }

        @Override
        public BodySubscriber<Void> apply(ResponseInfo responseInfo) {

            httpContext.setResponseInfo(responseInfo);
            httpContext.filterProcessor().onResponse(this.httpContext);

            return BodySubscribers.discarding();
        }
    }
}
