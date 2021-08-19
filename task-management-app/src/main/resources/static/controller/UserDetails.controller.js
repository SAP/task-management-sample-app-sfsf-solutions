sap.ui.define([
	"sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"ext/samples/taskmanagement/util/Config"
], function(Controller, JSONModel, MessageBox, Config) {
	"use strict";

	return Controller.extend("ext.samples.taskmanagement.controller.UserDetails", {

		onBeforeRendering : function() {},
		
		onInit : function() {
			this.getView().setModel(new JSONModel(), "details");
		},

		fetchEmployeeDetails : function() {
			var detailsModel = this.getView().getModel("details");
			var employeeId = detailsModel.getProperty("/employeeId");

			function fillDetailsModel(data) {
				detailsModel.setProperty("/employeeName", data.defaultFullName);
				detailsModel.setProperty("/employeeTitle", data.title);
				detailsModel.setProperty("/employeeEmail", data.email);
				detailsModel.setProperty("/employeeDepartment", data.department);
				detailsModel.setProperty("/employeePhoto", "data:image/jpeg;base64," + data.photo.base64Encoded);
			};

			function showGetDetailsError() {
				var messageBundle = this.getView().getModel("i18n").getResourceBundle();
				MessageBox.error(messageBundle.getText("couldNotGetEmployeeDetailsError"));
			}

			jQuery.ajax({
				method: "GET",
				url: Config.serviceUrl + "/users/" + employeeId,
				context: this,
				beforeSend: this.showBusy("detailsPopover")
			}).done(fillDetailsModel)
			.fail(showGetDetailsError)
			.always(this.hideBusy("detailsPopover"));
		},

		showBusy: function (controlId) {
			return function (jqXHR, settings) {
				this.byId(controlId).setBusy(true);
			};
		},

		hideBusy: function (controlId) {
			return function () {
				this.byId(controlId).setBusy(false);
			};
		}
	});
});
