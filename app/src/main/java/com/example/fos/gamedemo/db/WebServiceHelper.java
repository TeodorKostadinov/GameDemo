package com.example.fos.gamedemo.db;

import android.content.Context;
import android.util.Log;

import com.example.fos.gamedemo.cmn.Question;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fos on 8.12.2015 г..
 */
public class WebServiceHelper {
    private static final String DB_ADDRESS = "https://blinding-heat-4968.firebaseio.com/";

    private static WebServiceHelper instance;
    private Firebase myFirebaseRef;

    public static WebServiceHelper getInstance(Context context) {
        if(instance == null) {
            instance = new WebServiceHelper(context);
        }
        return instance;
    }

    private WebServiceHelper(Context context) {
        init(context);
    }

    public void init(Context context) {
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase(DB_ADDRESS);
    }

    public void createUser(String email, String password, final UserAuthenticationListener listener) {
        myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                listener.onCreateSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                listener.onCreateError();
            }
        });
    }

    public void authUser(String email, String password, final UserAuthenticationListener listener) {
        myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                listener.onAuthSuccess();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onAuthError();
            }
        });
    }

    public void readQuestions(final QuestionsDownloadListener listener) {
        myFirebaseRef.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //TODO Parsing the data
//                List<Question> questions = new ArrayList<Question>();
//                questions.add(new Question(new String[] {"asdf1","as2df","a3sdf","4asdf"},
//                        2, new double[] {46.235, 23.689}, "Guess what is this?2"));
//                questions.add(new Question(new String[] {"asdf1","as2df","a3sdf","4asdf"},
//                        0, new double[] {46.935, 27.689}, "Guess what is this?0"));
//                questions.add(new Question(new String[] {"asdf1","as2df","a3sdf","4asdf"},
//                        3, new double[] {42.695751, 23.332838}, "Guess what is this?3"));

                ArrayList<Map<String, Object>> qs = (ArrayList<Map<String, Object>>) snapshot.getValue();
                List<Question> questions = new ArrayList<Question>();
                for (int i=0; i < qs.size(); i++){
                    Map<String, Object> q = qs.get(i);
                    String text = (String) q.get("text");
                    Log.v(text, text);
                    Map<String, Double> location = (Map<String, Double>) q.get("location");
                    Double lon = location.get("lon");
                    Double lat = location.get("lat");
                    Log.v("lon", lon.toString());
                    Log.v("lat", lat.toString());
                    Long correct = (Long) q.get("correct");
                    Log.v("correct", correct + "");
                    ArrayList answers = (ArrayList) q.get("answers");
                    Log.v("answers", answers.toString());
                    questions.add(new Question((String[]) answers.toArray(new String [answers.size()]), Integer.valueOf(correct.toString()),  new double[] {lon, lat}, text));
                }


                listener.onQuestionsDownloaded(questions);
            }
            @Override public void onCancelled(FirebaseError error) {
                listener.onQuestionsError();
            }
        });
    }

    public interface UserAuthenticationListener {
        void onCreateSuccess();
        void onCreateError();
        void onAuthSuccess();
        void onAuthError();
    }

    public interface QuestionsDownloadListener {
        void onQuestionsDownloaded(List<Question> questions);
        void onQuestionsError();
    }
}
