package eu.xenit.restrequests.api.http;

public interface HttpResponse<TResponse> extends HttpResponseInfo {

    TResponse body();

}


