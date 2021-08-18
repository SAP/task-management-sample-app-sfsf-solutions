package com.sap.core.extensions.taskmanagement.odata;

import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.TodoEntryV2;
import com.sap.core.extensions.taskmanagement.dto.UpsertDTO;

public interface CustomODataToDoEntryService {

	UpsertDTO upsertToDo(TodoEntryV2 toDoEntryV2);

}
