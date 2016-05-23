package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import constants.ResponseType;
import helper.datasources.ObjectFactory;
import models.BasicRequest;
import models.TestCase;
import play.Logger;
import utils.Common;
import utils.HttpConnectionHellper;

public class FactoryMethod {
	public static BasicRequest getBasicRequest(JsonNode jsonBody) throws Exception{
		String errorMsg = "";
		try {
				if (Common.isNotNull(jsonBody.get("id"))) {
					BasicRequest basicRequest = ObjectFactory.getBasicRequest(jsonBody.get("id").asText());
					if (Common.isNotNull(jsonBody.get("requestEndPoint"))) {
						basicRequest.setEndPoint(jsonBody.get("requestEndPoint").asText());
					} else {
						errorMsg = "please specify requested End Point!!!";
					}
					if (Common.isNotNull(jsonBody.get("basicAuthUserKey"))
							|| Common.isNotNull(jsonBody.get("basicAuthPwd"))) {
						basicRequest.setBasicAuth(true);
						basicRequest.setUsername(jsonBody.get("basicAuthUserKey").asText());
						basicRequest.setPassword(jsonBody.get("basicAuthPwd").asText());
					}
					basicRequest.setId(basicRequest.getId());
					basicRequest.setReqName(jsonBody.get("reqName").asText());
					if (jsonBody.get("responseType").asText().equalsIgnoreCase("JSON")) {
						basicRequest.setResponseType(ResponseType.JSON);
					} else if (jsonBody.get("responseType").asText().equalsIgnoreCase("TEXT")) {
						basicRequest.setResponseType(ResponseType.TEXT);
					}
					basicRequest.setReqHeader(getMap(jsonBody, "reqHeader"));
					basicRequest.setReqParams(getMap(jsonBody, "reqParams"));
					basicRequest.setFormParams(getMap(jsonBody, "formParams"));
					basicRequest.setDependsUpon(getMap(jsonBody, "dependsUpon"));;
					basicRequest.setJsonExtractorParams(getMap(jsonBody, "formParams"));
					basicRequest.setDescription(getNotNull(jsonBody.get("description").asText()));
					basicRequest.setKeywords(getNotNull(jsonBody.get("keywords").asText()));
					basicRequest.setEnvName(getNotNull(jsonBody.get("envName").asText()));
					
					if (jsonBody.has("jsonBody")) {
						basicRequest.setReqBody(jsonBody.get("jsonBody").asText());
					}
					
					return basicRequest;
				} else {
					errorMsg = "Something went wrong, no test case was found!!!";
					throw new Exception();
				}

		} catch (Exception e) {
			e.printStackTrace();
			errorMsg=e.getMessage();
			throw e;
		}
	}
	
	
	public static Map<String, String> getMap(JsonNode jsonBody, String nodeName) {
		// header parameters
		Iterator<Entry<String, JsonNode>> fields = jsonBody.get(nodeName).fields();
		Map<String, String> arugHeader = new HashMap<>();

		while(fields.hasNext()){
			Entry<String, JsonNode> next = fields.next();
			Logger.info(" >>>"+nodeName+" key: "+next.getKey());
			Logger.info(" >>>"+nodeName+" value: "+next.getValue().asText());
			arugHeader.put(next.getKey(), next.getValue().asText());
		}
		return arugHeader;
	}
	static String getNotNull(String str) {
		if (Common.isNotNull(str)) {
			return str;
		} else
			return "";
	}

}
