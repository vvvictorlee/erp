package com.eosrp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import com.eosrp.db.DbContract;
import com.eosrp.db.PostgresHelper;
import com.eosrp.network.*;

public class PriceDbWriter {	
	
	private static double getEosPrice() throws IOException {
		String urlEosPriceUsd = "https://api.coinmarketcap.com/v2/ticker/1765/";
		Double eosPriceUsd;
		
		//~ Get request test ~//
		HttpGetHelper getTest = new HttpGetHelper(urlEosPriceUsd);
		String jsonString = getTest.sendRequest();
		
		//~ Get USD price of EOS ~//
		JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
		JSONObject data = (JSONObject) jsonObject.get("data");
		JSONObject quotes = (JSONObject) data.get("quotes");
		JSONObject usd = (JSONObject) quotes.get("USD");
		eosPriceUsd = (Double) usd.get("price"); 
		System.out.println(eosPriceUsd);
		return eosPriceUsd;
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		
		String urlGetTableRows = "http://node1.eosphere.io:8888";
		String urlGetAccount = "http://node1.eosphere.io:8888/v1/chain/get_account";
		
		
		//~ Initialize DB connection ~//
		PostgresHelper client = new PostgresHelper(
				DbContract.HOST, 
				DbContract.DB_NAME,
				DbContract.USERNAME,
				DbContract.PASSWORD);

		try {
			if (client.connect()) {
				System.out.println("DB connected");
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//~ Get timestamp ~//
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp dt = new java.sql.Timestamp(date.getTime());


		
		//~ Attempt to insert record ~//
		if (client.insert("eoscpu", dt, 0.01554462, getEosPrice()) == 1) {
			System.out.println("Record added");
		}

		//~ Print result ~//
		ResultSet rs = client.execQuery("SELECT * FROM eosram");
		while(rs.next()) {
			System.out.printf("%s\t%s\t%s\n", 
					rs.getString(1),
					rs.getString(2),
					rs.getString(3));
		}

	}

}