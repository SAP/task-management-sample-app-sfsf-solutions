package com.sap.core.extensions.taskmanagement.rest;

import java.util.List;
import java.util.stream.Collectors;

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

import com.sap.core.extensions.taskmanagement.dto.TaskRequestDTO;
import com.sap.core.extensions.taskmanagement.dto.TaskRequestsDTO;
import com.sap.core.extensions.taskmanagement.dto.ToDoEntryV2DTO;
import com.sap.core.extensions.taskmanagement.dto.UserDTO;
import com.sap.core.extensions.taskmanagement.services.ToDoEntityManager;
import com.sap.core.extensions.taskmanagement.services.UserManager;

@RestController
@RequestMapping("/v1/onboardings")
public class TaskManagementController {

	private static final String TO_DO_NAME_PATTERN = "([\\w\\s-]{1,32})\\s(\\([\\w-]{1,32}\\))";

	private final ToDoEntityManager toDoEntityManager;
	private final UserManager userManager;

	public TaskManagementController(ToDoEntityManager toDoEntityManager, UserManager userManager) {
		this.toDoEntityManager = toDoEntityManager;
		this.userManager = userManager;
	}

	@GetMapping
	public ResponseEntity<TaskRequestsDTO> listTasks() {
		List<TaskRequestDTO> requests = toDoEntityManager //
				.getAllGenericIntelligentServicesCategoryToDos() //
				.stream() //
				.filter(toDo -> toDo.getToDoEntryName().matches(TO_DO_NAME_PATTERN))
				.map(this::createTaskDTO) //
				.collect(Collectors.toList()); //

		return ResponseEntity.ok(new TaskRequestsDTO(requests));
	}

	@PatchMapping(value = "/{requestId}/complete")
	public ResponseEntity<?> completeTasks(@PathVariable(name = "requestId") String requestId) {
		toDoEntityManager.completeToDo(requestId);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/{requestId}")
	public ResponseEntity<?> deleteTasks(@PathVariable(name = "requestId") String requestId) {
		toDoEntityManager.deleteToDo(requestId);

		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<TaskRequestDTO> createTask(@RequestBody TaskRequestDTO employeeOnboardRequestDTO) {
		TaskRequestDTO toDo = toDoEntityManager.createGenericIntelligentServicesCategoryToDo(employeeOnboardRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(toDo);
	}

	private TaskRequestDTO createTaskDTO(ToDoEntryV2DTO toDoEntryV2DTO) {
		UserDTO userProfile = userManager.getUserProfile(extractUserIdFromToDoEntryName(toDoEntryV2DTO));

		return new TaskRequestDTO(toDoEntryV2DTO, userProfile);
	}

	private String extractUserIdFromToDoEntryName(ToDoEntryV2DTO toDoEntryV2DTO) {
		String toDoName = toDoEntryV2DTO.getToDoEntryName();

		return toDoName.substring(toDoName.indexOf('(') + 1, toDoName.lastIndexOf(')'));
	}

}
