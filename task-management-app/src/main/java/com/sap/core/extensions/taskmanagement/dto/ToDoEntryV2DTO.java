package com.sap.core.extensions.taskmanagement.dto;

import java.time.ZonedDateTime;

import com.sap.core.extensions.taskmanagement.vdm.namespaces.todoentity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class ToDoEntryV2DTO {

	private String toDoEntryId;
	private String toDoEntryName;
	private int status;
	private String userId;
	private String categoryId;
	private String linkUrl;
	private String subjectId;
	private ZonedDateTime dueDate;
	private User userNav;

}
