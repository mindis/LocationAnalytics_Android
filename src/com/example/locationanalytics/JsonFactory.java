package com.example.locationanalytics;

import org.json.JSONObject;

//import com.lolapau.cobradordelfrac.HomeActivity;
//import com.lolapau.cobradordelfrac.types.Debt;

public class JsonFactory {
    public static JSONObject userToJson(String latitude, String longitude){
    	JSONObject json = new JSONObject();
    	
    	try{
    	json.put("latitude", latitude);
    	json.put("longiude", longitude);
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }
    
//    public static JSONObject debtToJson(Debt debt){
//    	JSONObject json = new JSONObject();
//    	
//    	try{
//    	json.put("user_debtor_id", debt.getDebtorId());
//    	json.put("debtor_name", debt.getDebtorName());
//    	json.put("user_creditor_id", HomeActivity.id);
//    	json.put("quantity", debt.getQuantity());
//    	json.put("comments", debt.getComments());
//    	json.put("creditor_name", debt.getCreditorName());
//    	}
//    	catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	
//    	return json;
//    }
    
}
