package com.sap.core.extensions.employeeonboarding.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.core.extensions.employeeonboarding.vdm.namespaces.upsert.UpsertResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class UpsertDTO {

	@SerializedName("d")
	@JsonProperty("d")
	@Nullable
	@ODataField(odataName = "d")
	private List<UpsertResult> upsetResult;

}