package it.marcosoft.ticketwave.NetworkActivity;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import it.marcosoft.ticketwave.EventModel.Event;

/**
 * Utility class for parsing JSON responses from the Ticketmaster API.
 */
public class TicketmasterClient {
    private static final String TAG = "TicketmasterClient";

    private static final String ENDPOINT_EVENTS = "discovery/v2/events";

    private static final String API_KEY = "apikey=KqtxCDlnofSteZ63m7gmezFR8PR34o78";
    
    private static final String API_URL = "https://app.ticketmaster.com/";

    private static final String KEY_EMBEDDED = "_embedded";

    private static final String KEY_EVENTS = "events";

    private final String endpoint;

    private final List<String> queryParams;
    
    private final TicketMasterListener listener;

    public TicketmasterClient(String endpoint, List<String> queryParams, TicketMasterListener listener) {
        this.endpoint = endpoint;
        this.queryParams = queryParams;
        this.listener = listener;
    }

    private String buildURL(){
        StringBuilder urlBuilder = new StringBuilder(API_URL).append(endpoint).append("?");
        for (String queryParam : queryParams) {
            urlBuilder.append(queryParam).append("&");
        }
        return urlBuilder.append(API_KEY).toString();
    }

    private List<Event> fetchEventsFromJSON(JSONObject response) throws JSONException {
        List<Event> events = new ArrayList<>();
        
        if (!response.has(KEY_EMBEDDED)) {
            return events; 
        }

        JSONObject embedded = response.getJSONObject(KEY_EMBEDDED);
        JSONArray eventsArray = embedded.getJSONArray(KEY_EVENTS);

        for (int i = 0; i < eventsArray.length(); i++) {
            JSONObject eventJson = eventsArray.getJSONObject(i);
            events.add(new Event(eventJson));
        }

        return events;
    }

    private void notifyListener(List<Event> events) {
        if (listener != null) {
            listener.onEventsParsed(events);
        }
    }

    private void handleResponse(JSONObject response) {
        try {
            if (ENDPOINT_EVENTS.equals(endpoint)) {
                List<Event> parsedEvents = fetchEventsFromJSON(response);
                notifyListener(parsedEvents);
            } else {
                String err = "Unsupported endpoint: " + endpoint;
                Log.w(TAG, err);
                if (listener != null) listener.onError(err);
            }
        } catch (JSONException e) {
            String err = "JSON Parsing error";
            Log.e(TAG, err, e);
            if (listener != null) listener.onError(err);
        }
    }     

    public JsonObjectRequest createEventsRequest() {
        String url = buildURL();
        Log.d(TAG, "Request URL: " + url);

        return new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> handleResponse(response),
            error -> { 
                String err = "Volley error: " + error.getMessage();
                Log.e(TAG,err);
                if (listener != null) listener.onError(err);
            }
        );
    }
    
   public interface TicketMasterListener {
        void onEventsParsed(List<Event> events);
        void onError(String message);
    }
}
