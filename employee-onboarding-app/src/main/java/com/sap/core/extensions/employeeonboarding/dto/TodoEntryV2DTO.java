package com.sap.core.extensions.employeeonboarding.dto;

import java.time.ZonedDateTime;

import com.sap.core.extensions.employeeonboarding.vdm.namespaces.todoentity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class TodoEntryV2DTO {

	private String todoEntryId;
	private String todoEntryName;
	private int status;
	private String userId;
	private String categoryId;
	private String linkUrl;
	private String subjectId;
	private ZonedDateTime dueDate;
	private User userNav;

}
