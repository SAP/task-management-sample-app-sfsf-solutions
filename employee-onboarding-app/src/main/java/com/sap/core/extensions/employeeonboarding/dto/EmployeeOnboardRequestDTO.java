package com.sap.core.extensions.employeeonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class EmployeeOnboardRequestDTO {

	private final TodoEntryV2DTO todoEntryV2;
	private final UserDTO user;

}