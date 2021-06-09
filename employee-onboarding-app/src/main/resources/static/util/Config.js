sap.ui.define([], function () {
	"use strict";

	return {
		serviceUrl: "v1",
		jsonMediaType: "application/json; charset=utf-8",
		highContrastTheme: "sap_belize_hcb",
		standardTheme: sap.ui.getCore().getConfiguration().getTheme(),

		initApp: function (elementId) {

			new sap.m.Shell({
				showLogout: false,
				app: new sap.ui.core.ComponentContainer({
					name: 'ext.samples.employeeonboarding'
				})
			}).placeAt(elementId);
		}

	};
});