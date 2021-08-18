package com.sap.core.extensions.taskmanagement.odata.impl;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.TodoEntryV2;
import com.sap.core.extensions.taskmanagement.destination.HttpClientProvider;
import com.sap.core.extensions.taskmanagement.dto.UpsertDTO;
import com.sap.core.extensions.taskmanagement.odata.CustomODataToDoEntryService;

@Component
class CustomODataToDoEntryServiceImpl implements CustomODataToDoEntryService {

	private static final String UPSERT_PATH = "upsert";
	private static final String ODATA_V2 = "/odata/v2";

	private final HttpClientProvider httpClientProvider;

	private final Gson gson = new Gson();

	@Autowired
	CustomODataToDoEntryServiceImpl(HttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
	}

	@Override
	public UpsertDTO upsertToDo(TodoEntryV2 toDoEntryV2) {

		ODataRequestAction request = new ODataRequestAction(ODATA_V2, UPSERT_PATH, gson.toJson(toDoEntryV2),
				ODataProtocol.V2);

		final HttpClient httpClient = httpClientProvider.createHttpClient();

		try {

			HttpResponse response = request.execute(httpClient).getHttpResponse();
			String entity = EntityUtils.toString(response.getEntity());

			UpsertDTO res = gson.fromJson(entity, UpsertDTO.class);

			return res;
		} catch (ParseException | IOException e) {
			throw new IllegalStateException("Error occured during upsert operation", e);
		}
	}

}
