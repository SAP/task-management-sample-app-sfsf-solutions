package com.sap.core.extensions.employeeonboarding.destination;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.ScpCfServiceInfo;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

@Component
public class DestinationProvider {

	private static final String SERVICE_TYPE = "sap-successfactors-extensibility";
	private DestinationLoader destinationLoader;
	private String serviceName;

	private static final Logger LOGGER = LoggerFactory.getLogger(DestinationProvider.class);

	@Autowired
	DestinationProvider(DestinationLoader destinationLoader) {
		this.destinationLoader = destinationLoader;
		this.serviceName = getServiceName();
	}

	private String getServiceName() {
		List<ScpCfServiceInfo> scpCfServiceInfos = ScpCfServiceInfo.createFor(SERVICE_TYPE);

		if (scpCfServiceInfos.isEmpty()) {
			throw new IllegalArgumentException("Service from type " + SERVICE_TYPE + " is not bound");
		}

		String serviceName = scpCfServiceInfos.get(0).getServiceName();
		LOGGER.info("Destination with name [{}] will be used for authorization", serviceName);

		return serviceName;
	}

	public HttpDestination getDestination() {
		Destination destination = destinationLoader.tryGetDestination(serviceName).get();
		return destination.asHttp();
	}

}
