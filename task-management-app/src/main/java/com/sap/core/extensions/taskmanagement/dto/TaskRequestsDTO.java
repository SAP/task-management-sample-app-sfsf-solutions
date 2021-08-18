package com.sap.core.extensions.taskmanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class TaskRequestsDTO {

	private List<TaskRequestDTO> onboardRequests;
}
