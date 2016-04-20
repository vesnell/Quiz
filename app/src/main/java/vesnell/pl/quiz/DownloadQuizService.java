package vesnell.pl.quiz;

/**
 * Created by alek6 on 19.04.2016.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import vesnell.pl.quiz.json.JsonTags;
import vesnell.pl.quiz.database.model.Quiz;
import vesnell.pl.quiz.utils.Resources;

public class DownloadQuizService extends IntentService {

    private static final String TAG = "DownloadQuizService";

    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String RESULT = "results";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static final String QUIZ_PREFERENCES = "quizPreferences" ;
    public static final String MD5_QUIZ_PREFERENCES = "md5";

    private SharedPreferences sharedpreferences;

    public DownloadQuizService() {
        super(DownloadQuizService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sharedpreferences = getSharedPreferences(QUIZ_PREFERENCES, Context.MODE_PRIVATE);

        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        String url = intent.getStringExtra(URL);

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            //service is running
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                ArrayList<Quiz> results = downloadNewData(url);

                //send result back to activity
                bundle.putSerializable(RESULT, results);
                receiver.send(STATUS_FINISHED, bundle);
            } catch (Exception e) {

                //send error back to activity
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        this.stopSelf();
    }

    private ArrayList<Quiz> downloadNewData(String requestUrl) throws IOException, DownloadException {
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

            String quizMD5 = sharedpreferences.getString(MD5_QUIZ_PREFERENCES, null);
            String md5FromResponse = getMD5(response);
            if (!md5FromResponse.equals(quizMD5)) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(MD5_QUIZ_PREFERENCES, md5FromResponse);
                editor.commit();
                ArrayList<Quiz> results = parseResult(response);
                return results;
            }
            return null;
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

    public String getMD5(String s) {
        try {
            //create md5 hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            //create hex string
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}