package com.sap.core.extensions.taskmanagement.services;

import java.util.List;

import com.sap.core.extensions.taskmanagement.dto.TaskRequestDTO;
import com.sap.core.extensions.taskmanagement.dto.ToDoEntryV2DTO;
import com.sap.core.extensions.taskmanagement.dto.UpsertDTO;

public interface ToDoEntityManager {

	List<ToDoEntryV2DTO> getActiveGenericIntelligentServicesCategoryToDos();

	List<ToDoEntryV2DTO> getAllGenericIntelligentServicesCategoryToDos();

	TaskRequestDTO createGenericIntelligentServicesCategoryToDo(
			TaskRequestDTO taskRequestDTO);

	void deleteToDo(String requestId);

	UpsertDTO completeToDo(String requestId);

}
