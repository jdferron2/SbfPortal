(function () {
	var myConnector = tableau.makeConnector();

	myConnector.getSchema = function (schemaCallback) {
		var playerCols = [
			{ id : "playerId", alias : "playerId", dataType : tableau.dataTypeEnum.int },
			{ id : "proRank", alias : "proRank", dataType : tableau.dataTypeEnum.int },
			{ id : "displayName", alias : "displayName", dataType : tableau.dataTypeEnum.string },
			{ id : "dob", alias : "dob", dataType : tableau.dataTypeEnum.date },
			{ id : "position", alias : "position", dataType : tableau.dataTypeEnum.string },
			{ id : "team", alias : "team", dataType : tableau.dataTypeEnum.string },
			{ id : "weight", alias : "weight", dataType : tableau.dataTypeEnum.int },
			{ id : "height", alias : "height", dataType : tableau.dataTypeEnum.string },
			{ id : "jersey", alias : "jersey", dataType : tableau.dataTypeEnum.int }
			];
		var playerTable = {
				id : "playerDataFeed",
				alias : "Player Data",
				columns : playerCols
		};

		var ranksCols = [
			{ id : "playerId", alias : "playerId", dataType : tableau.dataTypeEnum.int },
			{ id : "rank", alias : "rank", dataType : tableau.dataTypeEnum.int },
			{ id : "rankSetId", alias : "rankSetId", dataType : tableau.dataTypeEnum.int }
			];
		var ranksTable = {
				id : "sbfRankDataFeed",
				alias : "Sbf Ranks Data",
				columns : ranksCols
		};
		
		var draftCols = [
			{ id : "playerId", alias : "playerId", dataType : tableau.dataTypeEnum.int },
			{ id : "timeDrafted", alias : "timeDrafted", dataType : tableau.dataTypeEnum.datetime },
			{ id : "teamId", alias : "teamId", dataType : tableau.dataTypeEnum.int },
			{ id : "slotDrafted", alias : "slotDrafted", dataType : tableau.dataTypeEnum.int }
			];
		var draftTable = {
				id : "sbfDraftDataFeed",
				alias : "Sbf Draft Data",
				columns : draftCols
		};
		
		var sbfTeamsCols = [
			{ id : "ownerName", alias : "ownerName", dataType : tableau.dataTypeEnum.string },
			{ id : "draftSlot", alias : "draftSlot", dataType : tableau.dataTypeEnum.int },
			{ id : "userId", alias : "userId", dataType : tableau.dataTypeEnum.int },
			{ id : "teamId", alias : "teamId", dataType : tableau.dataTypeEnum.int },
			{ id : "leagueId", alias : "leagueId", dataType : tableau.dataTypeEnum.int }
			];
		var sbfTeamsTable = {
				id : "sbfTeamsDataFeed",
				alias : "Sbf Teams Data",
				columns : sbfTeamsCols
		};

		schemaCallback([playerTable, ranksTable, draftTable, sbfTeamsTable]);
	}

	myConnector.getData = function(table, doneCallback) {
		var tableData = [];
		if (table.tableInfo.id == "playerDataFeed") {
			$.getJSON("http://sbf.hopto.org/SbfPortal-REST/REST/playerservice/players/", function(resp) {
				//var players = resp.players;

				// Iterate over the JSON object
				for (var i = 0, len = resp.length; i < len; i++) {
					tableData.push({
						"playerId": resp[i]['playerId'],
						"proRank": resp[i]['proRank'],
						"displayName": resp[i]['displayName'],
						"dob": resp[i]['dob'],
						"position": resp[i]['position'],
						"team": resp[i]['team'],
						"weight": resp[i]['weight'],
						"height": resp[i]['height'],
						"jersey": resp[i]['jersey'],
					});
				}

				table.appendRows(tableData);
				doneCallback();
			});
		} else if (table.tableInfo.id == "sbfRankDataFeed"){
			$.getJSON("http://sbf.hopto.org/SbfPortal-REST/REST/playerservice/ranks/sbfid/1/", function(resp) {
				//var ranks = resp.sbfRanks;
				
				// Iterate over the JSON object
				for (var i = 0, len = resp.length; i < len; i++) {
					tableData.push({
						"playerId": resp[i].playerId,
						"rank": resp[i].rank,
						"rankSetId": resp[i].rankSetId
					});
				}

				table.appendRows(tableData);
				doneCallback();
			});
		}else if (table.tableInfo.id == "sbfDraftDataFeed"){
			$.getJSON("http://sbf.hopto.org/SbfPortal-REST/REST/sbfdraftservice/draftresults/1/", function(resp) {
				//var draftPicks = resp.draftPicks;
				
				// Iterate over the JSON object
				for (var i = 0, len = resp.length; i < len; i++) {
					tableData.push({
						"playerId": resp[i].playerId,
						"timeDrafted": resp[i].timeDrafted,
						"teamId": resp[i].teamId,
						"slotDrafted": resp[i].slotDrafted
					});
				}				
				table.appendRows(tableData);
				doneCallback();
			});
		}else if (table.tableInfo.id == "sbfTeamsDataFeed"){
			$.getJSON("http://sbf.hopto.org/SbfPortal-REST/REST/sbfdraftservice/sbfteams/1/", function(resp) {
				//var draftPicks = resp.draftPicks;
				
				// Iterate over the JSON object
				for (var i = 0, len = resp.length; i < len; i++) {
					tableData.push({
						"ownerName": resp[i].ownerName,
						"draftSlot": resp[i].draftSlot,
						"teamId": resp[i].teamId,
						"userId": resp[i].userId,
						"leagueId": resp[i].leagueId
					});
				}				
				table.appendRows(tableData);
				doneCallback();
			});
		}
	};

	tableau.registerConnector(myConnector);
	$(document).ready(function () {
		$("#playerDataButton").click(function () {
			tableau.connectionName = "SBF Player Data Feed";
			tableau.submit();
		});
	});
})();