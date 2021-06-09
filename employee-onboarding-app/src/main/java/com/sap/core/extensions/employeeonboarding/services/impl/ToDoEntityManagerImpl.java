package com.sap.core.extensions.employeeonboarding.services.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;
import com.sap.core.extensions.employeeonboarding.destination.DestinationProvider;
import com.sap.core.extensions.employeeonboarding.dto.EmployeeOnboardRequestDTO;
import com.sap.core.extensions.employeeonboarding.dto.TodoEntryV2DTO;
import com.sap.core.extensions.employeeonboarding.dto.UpsertDTO;
import com.sap.core.extensions.employeeonboarding.dto.UserDTO;
import com.sap.core.extensions.employeeonboarding.odata.CustomODataToDoEntryService;
import com.sap.core.extensions.employeeonboarding.services.ToDoEntityManager;
import com.sap.core.extensions.employeeonboarding.services.UserInfo;
import com.sap.core.extensions.employeeonboarding.vdm.namespaces.todoentity.TodoEntryV2;
import com.sap.core.extensions.employeeonboarding.vdm.namespaces.todoentity.TodoEntryV2FluentHelper;
import com.sap.core.extensions.employeeonboarding.vdm.namespaces.todoentity.User;
import com.sap.core.extensions.employeeonboarding.vdm.services.ToDoEntityService;

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
	public List<TodoEntryV2DTO> getActiveGenericIntelligentServicesCategoryToDos() {
		HttpDestination destination = destinationProvider.getDestination();

		return getAllTodoQuery(GENERIC_INTELLIGENT_SERVICES_CATEGORY, ACTIVE_STATUS) //
				.executeRequest(destination) //
				.stream() //
				.map(this::toDto) //
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

		if (result.getUpsetResult().stream().allMatch(s -> s.getStatus().equals("OK"))) {
			return result;
		}

		throw new IllegalStateException("ToDo Compliting is not successfull, " + result);
	}

	@Override
	public void deleteToDo(String requestId) {
		TodoEntryV2 todoEntryV2 = new TodoEntryV2();
		todoEntryV2.setTodoEntryId(new BigDecimal(requestId));
		deleteTodo(todoEntryV2);
	}

	@Override
	public List<TodoEntryV2DTO> getAllGenericIntelligentServicesCategoryToDos() {
		HttpDestination destination = destinationProvider.getDestination();

		return getAllTodoQuery(GENERIC_INTELLIGENT_SERVICES_CATEGORY) //
				.executeRequest(destination) //
				.stream() //
				.map(this::toDto) //
				.collect(Collectors.toList()); //
	}

	@Override
	public EmployeeOnboardRequestDTO createGenericIntelligentServicesCategoryToDo(
			EmployeeOnboardRequestDTO employeeOnboardRequestDTO) {
		UserDTO user = employeeOnboardRequestDTO.getUser();
		if (user == null || user.getUserId() == null || user.getUserId() == null) {
			throw new IllegalArgumentException("User: [" + user + "] is not valid");
		}
		TodoEntryV2 todoEntryV2 = createTodo(userInfo.getName(), user.getDefaultFullName(), user.getUserId());
		return createEmployeeOnboardRequestDTO(todoEntryV2, user);
	}

	private ModificationResponse<TodoEntryV2> deleteTodo(TodoEntryV2 todoEntryV2) {
		HttpDestination destination = destinationProvider.getDestination();

		return toDoEntityService.deleteTodoEntryV2(todoEntryV2).executeRequest(destination);

	}

	private TodoEntryV2 createTodo(TodoEntryV2DTO todoEntryV2DTO) {
		HttpDestination destination = destinationProvider.getDestination();

		return toDoEntityService.createTodoEntryV2(fromDTO(todoEntryV2DTO)).executeRequest(destination)
				.getModifiedEntity();

	}

	private ZonedDateTime getNextWeekDate() {
		return ZonedDateTime.now().plusWeeks(1);
	}

	private TodoEntryV2 createTodo(String targetUser, String userName, String userId) {
		TodoEntryV2DTO todoEntryV2 = new TodoEntryV2DTO();
		todoEntryV2.setTodoEntryName(userName + " (" + userId + ")");
		todoEntryV2.setSubjectId(targetUser);
		todoEntryV2.setCategoryId(GENERIC_INTELLIGENT_SERVICES_CATEGORY);
		todoEntryV2.setStatus(ACTIVE_STATUS);
		todoEntryV2.setDueDate(getNextWeekDate());
		todoEntryV2.setLinkUrl("https://sap.com");
		User user = new User();
		JsonObject o = new JsonObject();
		o.addProperty("uri", "User('" + targetUser + "')");
		user.setCustomField("__metadata", o);
		todoEntryV2.setUserNav(user);

		return createTodo(todoEntryV2);
	}

	private TodoEntryV2FluentHelper getAllTodoQuery(String category) {
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

	private TodoEntryV2FluentHelper getAllTodoQuery(String category, int status) {
		return getAllTodoQuery(category) //
				.filter(TodoEntryV2.STATUS.eq(status));
	}

	private TodoEntryV2 fromDTO(TodoEntryV2DTO todoEntryV2DTO) {
		TodoEntryV2 todoEntryV2 = new TodoEntryV2();

		todoEntryV2.setTodoEntryId(toDoEntryId(todoEntryV2DTO));
		todoEntryV2.setTodoEntryName(todoEntryV2DTO.getTodoEntryName());
		todoEntryV2.setStatus(todoEntryV2DTO.getStatus());
		todoEntryV2.setUserId(todoEntryV2DTO.getUserId());
		todoEntryV2.setLinkUrl(todoEntryV2DTO.getLinkUrl());
		todoEntryV2.setSubjectId(todoEntryV2DTO.getSubjectId());
		todoEntryV2.setCategoryId(todoEntryV2DTO.getCategoryId());
		todoEntryV2.setDueDate(todoEntryV2DTO.getDueDate());
		todoEntryV2.setUserNav(todoEntryV2DTO.getUserNav());

		return todoEntryV2;
	}

	private TodoEntryV2DTO toDto(TodoEntryV2 todoEntryV2) {
		TodoEntryV2DTO todoEntryV2DTO = new TodoEntryV2DTO();

		todoEntryV2DTO.setTodoEntryId(todoEntryV2.getTodoEntryId().toString());
		todoEntryV2DTO.setTodoEntryName(todoEntryV2.getTodoEntryName());
		todoEntryV2DTO.setStatus(todoEntryV2.getStatus());
		todoEntryV2DTO.setUserId(todoEntryV2.getUserId());
		todoEntryV2DTO.setLinkUrl(todoEntryV2.getLinkUrl());
		todoEntryV2DTO.setSubjectId(todoEntryV2.getSubjectId());
		todoEntryV2DTO.setCategoryId(todoEntryV2.getCategoryId());
		todoEntryV2DTO.setDueDate(todoEntryV2.getDueDate());

		return todoEntryV2DTO;
	}

	private EmployeeOnboardRequestDTO createEmployeeOnboardRequestDTO(TodoEntryV2 todoEntryV2, UserDTO user) {
		return new EmployeeOnboardRequestDTO(toDto(todoEntryV2), user);
	}

	private BigDecimal toDoEntryId(TodoEntryV2DTO todoEntryV2DTO) {
		return todoEntryV2DTO.getTodoEntryId() == null ? null : new BigDecimal(todoEntryV2DTO.getTodoEntryId());
	}

}
