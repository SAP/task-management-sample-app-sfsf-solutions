package com.sap.core.extensions.employeeonboarding.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationLoader;
import com.sap.core.extensions.employeeonboarding.vdm.services.DefaultPhotoService;
import com.sap.core.extensions.employeeonboarding.vdm.services.DefaultToDoEntityService;
import com.sap.core.extensions.employeeonboarding.vdm.services.DefaultUserService;
import com.sap.core.extensions.employeeonboarding.vdm.services.PhotoService;
import com.sap.core.extensions.employeeonboarding.vdm.services.ToDoEntityService;
import com.sap.core.extensions.employeeonboarding.vdm.services.UserService;

@Configuration
class EmployeeOnboardingConfigurations {

	private static final String SERVICE_PATH = "/odata/v2";

	@Bean
	DestinationLoader getDestinationLoader() {
		return new ScpCfDestinationLoader();
	}

	@Bean
	@Primary
	ToDoEntityService getToDoEntityService() {
		return new DefaultToDoEntityService().withServicePath(SERVICE_PATH);
	}

	@Bean
	@Primary
	PhotoService getPhotoService() {
		return new DefaultPhotoService().withServicePath(SERVICE_PATH);
	}

	@Bean
	@Primary
	UserService getUserService() {
		return new DefaultUserService().withServicePath(SERVICE_PATH);
	}

}
