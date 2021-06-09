sap.ui.define([
	"sap/ui/core/ValueState",
	"ext/samples/employeeonboarding/util/Status"
], function (ValueState, Status) {
	"use strict";

	var mapping = [undefined, undefined, Status.ACTIVE, Status.COMPLETED];

	return {
		isCompleted: function (status) {
			return mapping[status] === Status.COMPLETED;
		},

		getCompletedStatus: function () {
			return mapping.indexOf(Status.COMPLETED);
		},

		formatText: function (status) {
			switch (mapping[status]) {
				case Status.ACTIVE:
					return "Active";
				case Status.COMPLETED:
					return "Completed";
				default:
					return "Unknown";
			}
		},

		formatState: function (status) {
			switch (mapping[status]) {
				case Status.COMPLETED:
					return ValueState.Success;
				default:
					return ValueState.None;
			}
		}
	};
});