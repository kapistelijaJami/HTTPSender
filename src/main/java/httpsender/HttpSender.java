package httpsender;

import static httpsender.HttpSender.Method.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Needs java 11.
 * 
 * Usage:
 * HttpResponse<String> response = new HttpSender().addHeader(header).addHeader(header2).buildRequest(url).sendAndReceiveString();
 * System.out.println(response.body());
 * sendAndReceiveString() might return null if the connection didn't work.
 */
public class HttpSender {
	private String url;
	private ArrayList<NameValuePair> headers = new ArrayList<>();
	private HttpRequest request;
	private HttpClient client = HttpClient.newHttpClient();
	
	public enum Method {
		POST, GET;
	}
	
	public HttpSender() {
		
	}
	
	public HttpSender(String url) {
		this.url = url;
	}
	
	/**
	 * Adds a header with name and value.
	 * @param name
	 * @param value
	 * @return HttpSender for chaining.
	 */
	public HttpSender addHeader(String name, String value) {
		return addHeader(new NameValuePair(name, value));
	}
	
	/**
	 * Adds a header with name and value pair.
	 * @param header
	 * @return HttpSender for chaining.
	 */
	public HttpSender addHeader(NameValuePair header) {
		headers.add(header);
		return this;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void resetHeaders() {
		headers.clear();
	}
	
	public void removeHeader(String name) {
		for (NameValuePair header : headers) {
			if (header.getName().equals(name)) {
				headers.remove(header);
				return;
			}
		}
	}
	
	/**
	 * Builds the request with already set headers and URL. Uses GET method.
	 * @return HttpSender for chaining.
	 */
	public HttpSender buildRequest() {
		return buildRequest(null);
	}
	
	/**
	 * Builds the request with already set headers. Uses GET method.
	 * @param newUrl Updates the URL. If null, doesn't do anything, then URL must be set before.
	 * @return HttpSender for chaining.
	 */
	public HttpSender buildRequest(String newUrl) {
		return buildRequest(newUrl, GET, null);
	}
	
	/**
	 * Builds the request with already set headers and URL.
	 * @param method Which method to use.
	 * @param dataToSend Data to send in body. Only if POST method.
	 * @return HttpSender for chaining.
	 */
	public HttpSender buildRequest(Method method, String dataToSend) {
		return buildRequest(null, method, dataToSend);
	}
	
	/**
	 * Builds the request with already set headers and URL.
	 * @param newUrl Updates the URL. If null, doesn't do anything.
	 * @param method Method. POST and GET.
	 * @param dataToSend Data to send in body. Only if POST method.
	 * @return HttpSender for chaining.
	 */
	public HttpSender buildRequest(String newUrl, Method method, String dataToSend) {
		if (newUrl != null) {
			url = newUrl;
		}
		
		try {
			Builder builder = HttpRequest.newBuilder(new URI(url));
			
			for (NameValuePair header : headers) {
				builder.header(header.getName(), header.getValue());
			}
			
			request = method == POST ? builder.POST(BodyPublishers.ofString(dataToSend)).build() : builder.GET().build();
			return this;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * Sends the already built request and receives the body as String.
	 * @return Response, where body is a String.
	 */
	public HttpResponse<String> sendAndReceiveString() {
		try {
			return client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}
	
	/**
	 * Sends the already built request and receives the body as byte array.
	 * @return Response, where body is a byte array.
	 */
	public HttpResponse<byte[]> sendAndReceiveBytes() {
		try {
			return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}
}
