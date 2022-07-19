package com.jdf.SbfPortal.backend.utility;

import java.io.IOException;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdf.SbfPortal.backend.data.jsonModel.PlayerJson;

public class RestAPIUtilsFFCalc {
	private static RestAPIUtilsFFCalc INSTANCE = null;
	//private String 		baseUri = "https://www.fantasyfootballnerd.com/service/players/xml/{key}/";
	private HttpClient 	client = HttpClients.createDefault();
	ObjectMapper objectMapper = new ObjectMapper();

	private enum Operation {
		QUERY_PLAYERS(getApiUriBuilder().queryParam("teams", "{teams}").queryParam("year", "{year}"));

		private final UriBuilder builder;

		Operation(UriBuilder mbuilder) {
			builder = mbuilder;
		}

		String getUrl(Object... values) {
			return builder.build(values).toString();
		}
	}

	private static UriBuilder getApiUriBuilder() {
		return UriBuilder.fromPath("https://fantasyfootballcalculator.com/api/v1/adp/half-ppr");
		
		// return UriBuilder.fromPath("https://" + PropertyReader.getProperty("tableauInternal") + "/api/2.1");
	}

	public static RestAPIUtilsFFCalc getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RestAPIUtilsFFCalc();
		}

		return INSTANCE;
	}
	
	private RestAPIUtilsFFCalc(){}

	public PlayerJson[] invokeQueryPlayers() {
		String url = Operation.QUERY_PLAYERS.getUrl("12", "2022");
		PlayerJson[] players;
		try {
			HttpGet getStubMethod = new HttpGet(url);
			HttpResponse getStubResponse;
			getStubResponse = client.execute(getStubMethod);


			int getStubStatusCode = getStubResponse.getStatusLine()
					.getStatusCode();
			if (getStubStatusCode < 200 || getStubStatusCode >= 300) {
				// Handle non-2xx status code
				return null;
			}
			String responseBody = EntityUtils.toString(getStubResponse.getEntity());

			JsonNode node = objectMapper.readTree(responseBody);
			players = objectMapper.treeToValue(node.get("players"), PlayerJson[].class);
			return players;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;

	}

}
