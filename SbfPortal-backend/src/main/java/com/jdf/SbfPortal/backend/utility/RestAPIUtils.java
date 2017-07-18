package com.jdf.SbfPortal.backend.utility;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom.input.SAXBuilder;

import com.jdf.SbfPortal.backend.data.DraftRankings;
import com.jdf.SbfPortal.backend.data.Players;


public class RestAPIUtils {
	private static RestAPIUtils INSTANCE = null;
	private static final String KEY = "7kacpcmh4nrq";
	//private String 		baseUri = "https://www.fantasyfootballnerd.com/service/players/xml/{key}/";
	private HttpClient 	client = HttpClients.createDefault();


	private enum Operation {
		QUERY_PLAYERS(getApiUriBuilder().path("service/{service}/xml/" + KEY)),
		QUERY_RANKS(getApiUriBuilder().path("service/{service}/xml/" + KEY + "/{ppr}/"));

		private final UriBuilder builder;

		Operation(UriBuilder mbuilder) {
			builder = mbuilder;
		}

		String getUrl(Object... values) {
			return builder.build(values).toString();
		}
	}

	private static UriBuilder getApiUriBuilder() {
		return UriBuilder.fromPath("https://www.fantasyfootballnerd.com/");
		// return UriBuilder.fromPath("https://" + PropertyReader.getProperty("tableauInternal") + "/api/2.1");
	}

	public static RestAPIUtils getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RestAPIUtils();
			//initialize();
		}

		return INSTANCE;
	}
	
	private RestAPIUtils(){}

	public Players invokeQueryPlayers() {

		//m_logger.info(String.format("Adding permissions to workbook '%s'.", workbookId));

		String url = Operation.QUERY_PLAYERS.getUrl("players");
		Players players = new Players();
		try {
			HttpGet getStubMethod = new HttpGet(url);
			HttpResponse getStubResponse = client.execute(getStubMethod);

			int getStubStatusCode = getStubResponse.getStatusLine()
					.getStatusCode();
			if (getStubStatusCode < 200 || getStubStatusCode >= 300) {
				// Handle non-2xx status code
				return null;
			}
			String responseBody = EntityUtils.toString(getStubResponse.getEntity());

			JAXBContext jaxbContext;
			org.jdom.input.SAXBuilder saxBuilder = new SAXBuilder();

			jaxbContext = JAXBContext.newInstance(Players.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 

			StringReader reader = new StringReader(responseBody);
			players= jaxbUnmarshaller.unmarshal(new StreamSource(reader), Players.class).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return players;

	}
	
	public DraftRankings invokeQueryRanks() {

		//m_logger.info(String.format("Adding permissions to workbook '%s'.", workbookId));

		String url = Operation.QUERY_RANKS.getUrl("draft-rankings","1");
		DraftRankings ranks = new DraftRankings();
		try {
			HttpGet getStubMethod = new HttpGet(url);
			HttpResponse getStubResponse = client.execute(getStubMethod);

			int getStubStatusCode = getStubResponse.getStatusLine()
					.getStatusCode();
			if (getStubStatusCode < 200 || getStubStatusCode >= 300) {
				// Handle non-2xx status code
				return null;
			}
			String responseBody = EntityUtils.toString(getStubResponse.getEntity());

			JAXBContext jaxbContext;
			org.jdom.input.SAXBuilder saxBuilder = new SAXBuilder();

			jaxbContext = JAXBContext.newInstance(DraftRankings.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 

			StringReader reader = new StringReader(responseBody);
			ranks= jaxbUnmarshaller.unmarshal(new StreamSource(reader), DraftRankings.class).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ranks;

	}

	public void marshalPlayerList(Players playerList, String xmlFileName) {
		JAXBContext jaxbContext;

		try {
			String backupFileName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
			jaxbContext = JAXBContext.newInstance(Players.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
			File outputFile = new File("C:\\Users\\jferron\\Documents\\RankXML\\" + xmlFileName);
			
			//File backupFile = new File("C:\\Users\\jferron\\Documents\\RankXML\\myRanks" + backupFileName + ".xml");
			//outputFile.renameTo(backupFile);
			
			jaxbMarshaller.marshal((Object) playerList,outputFile );
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Players unMarshalPlayerList(String xmlFileName) {
		JAXBContext jaxbContext;
		Players playerList = new Players();

		try {
			
			jaxbContext = JAXBContext.newInstance(Players.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 
			File inputFile = new File("C:\\Users\\jferron\\Documents\\RankXML\\" + xmlFileName);
			playerList = (Players) jaxbUnmarshaller.unmarshal(inputFile );
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return playerList;
	}
	
//	public void marshalFantasyTeams(FantasyLeague teamList, String xmlFileName) {
//		JAXBContext jaxbContext;
//
//		try {
//			//String backupFileName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
//			jaxbContext = JAXBContext.newInstance(FantasyLeague.class);
//
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
//			File outputFile = new File("C:\\Users\\jferron\\Documents\\RankXML\\" + xmlFileName);
//			
//			
//			jaxbMarshaller.marshal((Object) teamList,outputFile );
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}
	
//	public FantasyLeague unMarshalFantasyTeams(String xmlFileName) {
//		JAXBContext jaxbContext;
//		FantasyLeague league = new FantasyLeague();
//
//		try {
//			
//			jaxbContext = JAXBContext.newInstance(FantasyLeague.class);
//			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 
//			File inputFile = new File("C:\\Users\\jferron\\Documents\\RankXML\\" + xmlFileName);
//			league = (FantasyLeague) jaxbUnmarshaller.unmarshal(inputFile );
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		return league;
//	}

}
