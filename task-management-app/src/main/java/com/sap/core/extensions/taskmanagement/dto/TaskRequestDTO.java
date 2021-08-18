package com.sap.core.extensions.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class TaskRequestDTO {

	private final ToDoEntryV2DTO toDoEntryV2;
	private final UserDTO user;

}