package com.sap.core.extensions.taskmanagement.services.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;
import com.sap.core.extensions.taskmanagement.destination.DestinationProvider;
import com.sap.core.extensions.taskmanagement.dto.TaskRequestDTO;
import com.sap.core.extensions.taskmanagement.dto.ToDoEntryV2DTO;
import com.sap.core.extensions.taskmanagement.dto.UpsertDTO;
import com.sap.core.extensions.taskmanagement.dto.UserDTO;
import com.sap.core.extensions.taskmanagement.odata.CustomODataToDoEntryService;
import com.sap.core.extensions.taskmanagement.services.ToDoEntityManager;
import com.sap.core.extensions.taskmanagement.services.UserInfo;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.TodoEntryV2;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.TodoEntryV2FluentHelper;
import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.User;
import com.sap.core.extensions.taskmanagement.vdm.services.ToDoEntityService;

@Component
class ToDoEntityManagerImpl implements ToDoEntityManager {

	private static final int ACTIVE_STATUS = 2;
	private static final int COMPLETED_STATUS = 3;
	private static final String GENERIC_INTELLIGENT_SERVICES_CATEGORY = "41";

	private final DestinationProvider destinationProvider;
	private final ToDoEntityService toDoEntityService;
	private final UserInfo userInfo;

	@Autowired
	private CustomODataToDoEntryService customUserService;

	@Autowired
	ToDoEntityManagerImpl(DestinationProvider destinationProvider, ToDoEntityService toDoEntityService,
			UserInfo userInfo) {
		this.destinationProvider = destinationProvider;
		this.toDoEntityService = toDoEntityService;
		this.userInfo = userInfo;
	}

	@Override
	public List<ToDoEntryV2DTO> getActiveGenericIntelligentServicesCategoryToDos() {
		HttpDestination destination = destinationProvider.getDestination();

		return getAllToDoQueryHelperFilteredByCategoryAndStatus(GENERIC_INTELLIGENT_SERVICES_CATEGORY, ACTIVE_STATUS) //
				.executeRequest(destination) //
				.stream() //
				.map(this::toDTO) //
				.collect(Collectors.toList()); //
	}

	@Override
	public UpsertDTO completeToDo(String toDoEntryId) {
		TodoEntryV2 toDoEntryV2 = new TodoEntryV2();
		toDoEntryV2.setStatus(COMPLETED_STATUS);
		JsonObject o = new JsonObject();
		o.addProperty("uri", "TodoEntryV2(" + toDoEntryId + "M)");
		toDoEntryV2.setCustomField("__metadata", o);

		UpsertDTO result = customUserService.upsertToDo(toDoEntryV2);

		if (result.getUpsertResult().stream().allMatch(s -> "OK".equals(s.getStatus()))) {
			return result;
		}

		throw new RuntimeException("Completing ToDo with id [" + toDoEntryId + "] failed. Server response:" + result);
	}

	@Override
	public void deleteToDo(String requestId) {
		TodoEntryV2 toDoEntryV2 = new TodoEntryV2();
		toDoEntryV2.setTodoEntryId(new BigDecimal(requestId));
		deleteToDo(toDoEntryV2);
	}

	private ModificationResponse<TodoEntryV2> deleteToDo(TodoEntryV2 toDoEntry) {
		HttpDestination destination = destinationProvider.getDestination();

		return toDoEntityService.deleteTodoEntryV2(toDoEntry).executeRequest(destination);
	}

	@Override
	public List<ToDoEntryV2DTO> getAllGenericIntelligentServicesCategoryToDos() {
		HttpDestination destination = destinationProvider.getDestination();

		TodoEntryV2FluentHelper queryHelper = getAllToDoQueryHelperFilteredByCategory(
				GENERIC_INTELLIGENT_SERVICES_CATEGORY);
		return queryHelper //
				.executeRequest(destination) //
				.stream() //
				.map(this::toDTO) //
				.collect(Collectors.toList()); //
	}

	@Override
	public TaskRequestDTO createGenericIntelligentServicesCategoryToDo(
			TaskRequestDTO taskRequestDTO) {
		UserDTO user = taskRequestDTO.getUser();
		if (user == null || user.getUserId() == null) {
			throw new IllegalArgumentException("User: [" + user + "] is not valid");
		}
		TodoEntryV2 toDoEntry = createToDo(userInfo.getName(), user.getDefaultFullName(), user.getUserId());
		return createEmployeeOnboardRequestDTO(toDoEntry, user);
	}

	private TodoEntryV2 createToDo(String targetUser, String userName, String userId) {
		String entryName = userName + " (" + userId + ")";
		User user = new User();
		JsonObject o = new JsonObject();
		o.addProperty("uri", "User('" + targetUser + "')");
		user.setCustomField("__metadata", o);

		ToDoEntryV2DTO toDoEntryV2 = new ToDoEntryV2DTO(null, entryName, ACTIVE_STATUS, null,
				GENERIC_INTELLIGENT_SERVICES_CATEGORY, "https://sap.com", userId, getNextWeekDate(), user);

		return createToDo(toDoEntryV2);
	}

	private TodoEntryV2 createToDo(ToDoEntryV2DTO toDoEntryV2DTO) {
		HttpDestination destination = destinationProvider.getDestination();

		return toDoEntityService.createTodoEntryV2(fromDTO(toDoEntryV2DTO)).executeRequest(destination)
				.getModifiedEntity();

	}

	private ZonedDateTime getNextWeekDate() {
		return ZonedDateTime.now().plusWeeks(1);
	}

	private TodoEntryV2FluentHelper getAllToDoQueryHelperFilteredByCategory(String category) {
		return toDoEntityService //
				.getAllTodoEntryV2() //
				.select(TodoEntryV2.TODO_ENTRY_ID) //
				.select(TodoEntryV2.TODO_ENTRY_NAME) //
				.select(TodoEntryV2.STATUS) //
				.select(TodoEntryV2.USER_ID) //
				.select(TodoEntryV2.SUBJECT_ID) //
				.select(TodoEntryV2.CATEGORY_ID) //
				.select(TodoEntryV2.DUE_DATE) //
				.select(TodoEntryV2.LINK_URL) //
				.filter(TodoEntryV2.CATEGORY_ID.eq(category)) //
				.filter(TodoEntryV2.USER_ID.eq(userInfo.getName()));
	}

	private TodoEntryV2FluentHelper getAllToDoQueryHelperFilteredByCategoryAndStatus(String category, int status) {
		return getAllToDoQueryHelperFilteredByCategory(category) //
				.filter(TodoEntryV2.STATUS.eq(status));
	}

	private TodoEntryV2 fromDTO(ToDoEntryV2DTO toDoEntryDTO) {
		TodoEntryV2 toDoEntry = new TodoEntryV2();

		toDoEntry.setTodoEntryId(convertToDoEntryId(toDoEntryDTO.getToDoEntryId()));
		toDoEntry.setTodoEntryName(toDoEntryDTO.getToDoEntryName());
		toDoEntry.setStatus(toDoEntryDTO.getStatus());
		toDoEntry.setUserId(toDoEntryDTO.getUserId());
		toDoEntry.setLinkUrl(toDoEntryDTO.getLinkUrl());
		toDoEntry.setSubjectId(toDoEntryDTO.getSubjectId());
		toDoEntry.setCategoryId(toDoEntryDTO.getCategoryId());
		toDoEntry.setDueDate(toDoEntryDTO.getDueDate());
		toDoEntry.setUserNav(toDoEntryDTO.getUserNav());

		return toDoEntry;
	}

	private ToDoEntryV2DTO toDTO(TodoEntryV2 toDoEntry) {
		ToDoEntryV2DTO toDoEntryDTO = new ToDoEntryV2DTO(toDoEntry.getTodoEntryId().toString(),
				toDoEntry.getTodoEntryName(), toDoEntry.getStatus(), toDoEntry.getUserId(), toDoEntry.getCategoryId(),
				toDoEntry.getLinkUrl(), toDoEntry.getSubjectId(), toDoEntry.getDueDate(), null);

		return toDoEntryDTO;
	}

	private TaskRequestDTO createEmployeeOnboardRequestDTO(TodoEntryV2 toDoEntry, UserDTO user) {
		return new TaskRequestDTO(toDTO(toDoEntry), user);
	}

	private BigDecimal convertToDoEntryId(String id) {
		return id == null ? null : new BigDecimal(id);
	}

}
