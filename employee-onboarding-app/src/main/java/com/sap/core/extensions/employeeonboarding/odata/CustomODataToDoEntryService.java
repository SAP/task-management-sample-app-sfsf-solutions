package com.sap.core.extensions.employeeonboarding.odata;

import com.sap.core.extensions.employeeonboarding.dto.UpsertDTO;
import com.sap.core.extensions.employeeonboarding.vdm.namespaces.todoentity.TodoEntryV2;

public interface CustomODataToDoEntryService {

	UpsertDTO upsertToDo(TodoEntryV2 toDoEntryV2);

}
