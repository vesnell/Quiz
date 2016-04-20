package vesnell.pl.quiz;

/**
 * Created by alek6 on 19.04.2016.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vesnell.pl.quiz.json.JsonTags;
import vesnell.pl.quiz.database.model.Quiz;
import vesnell.pl.quiz.utils.Resources;

public class DownloadQuizService extends IntentService {

    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String RESULT = "result";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadQuizService";

    public DownloadQuizService() {
        super(DownloadQuizService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        String url = intent.getStringExtra(URL);

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            //service is running
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                ArrayList<Quiz> results = downloadData(url);

                //send result back to activity
                if (results.size() > 0) {
                    bundle.putSerializable(RESULT, results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {

                //send error back to activity
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        this.stopSelf();
    }

    private ArrayList<Quiz> downloadData(String requestUrl) throws IOException, DownloadException {
        InputStream inputStream;
        HttpURLConnection urlConnection;

        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();

        //statusCode=200? OK
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            ArrayList<Quiz> results = parseResult(response);
            return results;
        } else {
            throw new DownloadException(Resources.getString(R.string.error_download_data));
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return result;
    }

    private ArrayList<Quiz> parseResult(String result) {

        ArrayList<Quiz> quizzes = new ArrayList<Quiz>();
        try {
            JSONObject response = new JSONObject(result);
            JSONArray items = response.optJSONArray(JsonTags.items);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.optJSONObject(i);
                Quiz quiz = new Quiz(item);
                quizzes.add(quiz);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}