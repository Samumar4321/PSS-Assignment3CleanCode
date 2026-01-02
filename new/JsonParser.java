package it.marcosoft.ticketwave.NetworkActivity;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.marcosoft.ticketwave.EventModel.Event;

/**
 * Utility class for parsing JSON responses from the Ticketmaster API.
 */
public class JsonParser {
    private static final String API_KEY = "apikey=KqtxCDlnofSteZ63m7gmezFR8PR34o78";
    
    private static final String API_URL = "https://app.ticketmaster.com/"

    private final String endpoint;

    private final List<String> queryParams;

    private final List<Event> events;

    private final OnEventsParsedListener onEventsParsedListener;

    public JsonParser(String endpoint, List<String> queryParams, OnEventsParsedListener listener) {
        this.endpoint = endpoint;
        this.queryParams = queryParams;
        this.events = new ArrayList<>();
        this.onEventsParsedListener = listener;
    }

    private String buildURL(){
        StringBuilder urlBuilder = new StringBuilder(API_URL).append(endpoint).append("?");
        for (String queryParam : queryParams) {
            urlBuilder.append(queryParam).append("&");
        }
        return urlBuilder.append(API_KEY).toString();
    }
    
    private void handleResponse(JSONObject response) {
        try {
            if (ENDPOINT_EVENTS.equals(endpoint)) {
                List<Event> parsedEvents = parseEventsFromJson(response);
                notifyListener(parsedEvents);
            } else {
                Log.w(TAG, "Unsupported endpoint: " + endpoint);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing error", e);
        }
    }

    private List<Event> parseEventsFromJson(JSONObject response) throws JSONException {
        List<Event> eventsList = new ArrayList<>();
        
        if (!response.has(KEY_EMBEDDED)) {
            return eventsList; // Return empty list instead of crashing
        }

        JSONObject embedded = response.getJSONObject(KEY_EMBEDDED);
        JSONArray eventsArray = embedded.getJSONArray(KEY_EVENTS);

        for (int i = 0; i < eventsArray.length(); i++) {
            JSONObject eventObj = eventsArray.getJSONObject(i);
            eventsList.add(new Event(eventObj));
        }
        
        return eventsList;
    }

    private void notifyListener(List<Event> events) {
        if (listener != null) {
            listener.onEventsParsed(events);
        }
    }

    /**
     * Creates a JsonObjectRequest specifically for fetching events.
     *
     * @return JsonObjectRequest ready to be added to the request queue.
     */
    public JsonObjectRequest createEventsRequest() {
        String url = buildUrl();
        Log.d(TAG, "Request URL: " + url);

        return new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> handleResponse(response),
            error -> Log.e(TAG, "Volley error: " + error.getMessage())
        );
    }
    
    public interface OnEventsParsedListener {
        void onEventsParsed(List<Event> events);
    }
}
