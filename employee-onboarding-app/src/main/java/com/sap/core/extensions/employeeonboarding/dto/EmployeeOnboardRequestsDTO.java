package com.sap.core.extensions.employeeonboarding.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class EmployeeOnboardRequestsDTO {

	private List<EmployeeOnboardRequestDTO> onboardRequests;
}
