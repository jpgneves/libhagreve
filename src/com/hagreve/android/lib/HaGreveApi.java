package com.hagreve.android.lib;
/*    Copyright 2012 João Neves, Carlos Fonseca, Filipe Cabecinhas

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.util.Log;

/***
 * Implementation of the HaGreve API
 * http://hagreve.com/api
 * @author João Neves <sevenjp@gmail.com>
 *
 */
public class HaGreveApi {
	
	private static final String API_LOG_TAG = "HaGreveAPI";
	private static final int READ_BUFFER_SIZE = 512;
	private static final String BASE_URL = "http://hagreve.com/api/v1";
	private static final String USER_AGENT = "LibHaGreve";
	
	private static String readStream(InputStream in) throws IOException {
		Reader reader = new BufferedReader(new InputStreamReader(in));
		char buffer[] = new char[READ_BUFFER_SIZE];
		String contents = "";
		int read = 0;
		while((read = reader.read(buffer, 0, READ_BUFFER_SIZE)) > 0) {
			contents += String.valueOf(buffer, 0, read);
		}
		
		return contents;
	}
	
	/**
	 * Performs an HTTP GET request to the specified URL resource.
	 * @param url
	 * @return Content of the resource, as a String.
	 */
	private static String doGetRequest(String url) {
		String ret = null;
		HttpResponse response = null;
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "application/json");
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(request);
		} catch (IOException e) {
			Log.d(API_LOG_TAG, "Error getting " + url);
		}
		
		if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			try {
				InputStream in = new BufferedInputStream(response.getEntity().getContent());
				ret = readStream(in);
			} catch (IllegalStateException e) {
				Log.e(API_LOG_TAG, "Error reading stream");
			} catch (IOException e) {
				Log.e(API_LOG_TAG, "Error reading stream");
			}
		}
		
		return ret;
	}
	
	/**
	 * Obtains the list of current strikes.
	 * @return Array of Strike objects
	 */
	public static Strike[] getStrikes() {
		Strike[] items = new Strike[0];
		
		String result = doGetRequest(BASE_URL + "/strikes");
		
		if(result != null) {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

				public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						return formatter.parse(json.getAsString());
					} catch (ParseException e) {
						throw new JsonParseException(e.getMessage());
					}
				}

			});
			Gson gson = builder.create();
			items = gson.fromJson(result, Strike[].class);
		}
		
		return items;
	}
}
