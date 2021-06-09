sap.ui.define([
	"sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel"
], function(Controller, JSONModel) {
	"use strict";

	return Controller.extend("ext.samples.employeeonboarding.controller.UserDetails", {

		onBeforeRendering : function() {},
		
		onInit : function() {
			this.getView().setModel(new JSONModel(), "details");
		}
	});
});
