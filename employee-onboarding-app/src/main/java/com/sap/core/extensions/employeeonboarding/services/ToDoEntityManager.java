package com.sap.core.extensions.employeeonboarding.services;

import java.util.List;

import com.sap.core.extensions.employeeonboarding.dto.EmployeeOnboardRequestDTO;
import com.sap.core.extensions.employeeonboarding.dto.TodoEntryV2DTO;
import com.sap.core.extensions.employeeonboarding.dto.UpsertDTO;

public interface ToDoEntityManager {

	List<TodoEntryV2DTO> getActiveGenericIntelligentServicesCategoryToDos();

	List<TodoEntryV2DTO> getAllGenericIntelligentServicesCategoryToDos();

	EmployeeOnboardRequestDTO createGenericIntelligentServicesCategoryToDo(
			EmployeeOnboardRequestDTO employeeOnboardRequestDTO);

	void deleteToDo(String requestId);

	UpsertDTO completeToDo(String requestId);

}
