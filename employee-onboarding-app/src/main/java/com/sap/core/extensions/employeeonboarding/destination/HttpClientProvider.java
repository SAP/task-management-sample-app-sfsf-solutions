package com.sap.core.extensions.employeeonboarding.destination;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

@Component
public class HttpClientProvider {

	private final DestinationProvider destinationProvider;

	@Autowired
	HttpClientProvider(DestinationProvider destinationProvider) {
		this.destinationProvider = destinationProvider;
	}

	public HttpClient createHttpClient() {
		HttpDestination destination = destinationProvider.getDestination();

		return HttpClientAccessor.getHttpClient(destination);
	}

}
