package com.sap.core.extensions.employeeonboarding.rest;

import java.util.List;
import java.util.stream.Collectors;

import com.sap.core.extensions.employeeonboarding.dto.EmployeeOnboardRequestDTO;
import com.sap.core.extensions.employeeonboarding.dto.EmployeeOnboardRequestsDTO;
import com.sap.core.extensions.employeeonboarding.dto.TodoEntryV2DTO;
import com.sap.core.extensions.employeeonboarding.dto.UpsertDTO;
import com.sap.core.extensions.employeeonboarding.dto.UserDTO;
import com.sap.core.extensions.employeeonboarding.services.ToDoEntityManager;
import com.sap.core.extensions.employeeonboarding.services.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/onboardings")
public class EmployeeOnboardingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeOnboardingController.class);

	private final ToDoEntityManager toDoEntityManager;
	private final UserManager userManager;

	public EmployeeOnboardingController(ToDoEntityManager toDoEntityManager, UserManager userManager) {
		this.toDoEntityManager = toDoEntityManager;
		this.userManager = userManager;
	}

	@GetMapping
	public ResponseEntity<EmployeeOnboardRequestsDTO> listEmployeeOnboardingRequests() {
		List<EmployeeOnboardRequestDTO> requests = toDoEntityManager //
				.getAllGenericIntelligentServicesCategoryToDos() //
				.stream() //
				.map(this::createEmployeeOnboardingRequestDTO) //
				.collect(Collectors.toList()); //

		return ResponseEntity.ok(new EmployeeOnboardRequestsDTO(requests));
	}

	@PatchMapping(value = "/{requestId}/complete")
	public ResponseEntity<?> completeEmployeeOnboardingRequest(@PathVariable(name = "requestId") String requestId) {
		UpsertDTO response = toDoEntityManager.completeToDo(requestId);

		LOGGER.debug("Complete To Do operation finished successfully with response: {}", response);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/{requestId}")
	public ResponseEntity<?> deleteEmployeeOnboardingRequest(@PathVariable(name = "requestId") String requestId) {
		toDoEntityManager.deleteToDo(requestId);

		LOGGER.debug("Deleted To Do operation finished successfully with request ID: {}", requestId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<EmployeeOnboardRequestDTO> createEmployeeOnboardingRequest(
			@RequestBody EmployeeOnboardRequestDTO employeeOnboardRequestDTO) {
		EmployeeOnboardRequestDTO todo = toDoEntityManager
				.createGenericIntelligentServicesCategoryToDo(employeeOnboardRequestDTO);

		LOGGER.debug("Created To Do user with ID {}", todo.getTodoEntryV2().getTodoEntryId());
		return ResponseEntity.status(HttpStatus.CREATED).body(todo);
	}

	private EmployeeOnboardRequestDTO createEmployeeOnboardingRequestDTO(TodoEntryV2DTO todoEntryV2DTO) {
		UserDTO userProfile = userManager.getUserProfile(getUserId(todoEntryV2DTO));
		LOGGER.debug("User profile: {}", userProfile);

		return new EmployeeOnboardRequestDTO(todoEntryV2DTO, userProfile);
	}

	private String getUserId(TodoEntryV2DTO todoEntryV2DTO) {
		String todoName = todoEntryV2DTO.getTodoEntryName();
		String relocatedUserId = todoName.substring(todoName.indexOf('(') + 1, todoName.indexOf(')'));

		return relocatedUserId;

	}

}
