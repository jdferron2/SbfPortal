(function () {
	var myConnector = tableau.makeConnector();

	myConnector.getSchema = function (schemaCallback) {
		var cols = [
			{ id : "playerId", alias : "playerId", dataType : tableau.dataTypeEnum.int },
			{ id : "rank", alias : "rank", dataType : tableau.dataTypeEnum.int },
			{ id : "sbfId", alias : "sbfId", dataType : tableau.dataTypeEnum.int }
			];
		var tableInfo = {
				id : "sbfDataFeed",
				alias : "Sbf Data",
				columns : cols
		};
		schemaCallback([tableInfo]);
	}

	myConnector.getData = function(table, doneCallback) {
		$.getJSON("http://localhost:8080/SbfPortal-REST/REST/playerservice/ranks/sbfid/1/", function(resp) {
			var ranks = resp.sbfRanks,
			tableData = [];

			// Iterate over the JSON object
			for (var i = 0, len = ranks.length; i < len; i++) {
				tableData.push({
					"playerId": ranks[i].playerId,
					"rank": ranks[i].rank,
					"sbfId": ranks[i].sbfId
				});
			}

			table.appendRows(tableData);
			doneCallback();
		});
	};

	tableau.registerConnector(myConnector);
	$(document).ready(function () {
		$("#draftDataButton").click(function () {
			tableau.connectionName = "SBF Data Feed";
			tableau.submit();
		});
	});
})();