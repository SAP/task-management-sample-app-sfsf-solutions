package com.sap.core.extensions.employeeonboarding.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

@ControllerAdvice
class ExceptionHandlerConfiguraiton {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerConfiguraiton.class);

	@ExceptionHandler(ODataServiceErrorException.class)
	public ResponseEntity<?> handleODataServiceErrorException(ODataServiceErrorException exception) {

		LOGGER.error("OData error response: {}", exception.getHttpBody().getOrNull());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.valueOf(exception.getHttpCode()));
	}
}