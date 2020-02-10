package com.burca.lubyapp.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.burca.lubyapp.activity.MainActivity;
import com.burca.lubyapp.model.Token;
import com.burca.lubyapp.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LoginService {
    private Context context;
    private Token token;
    private Callable callback;

    public LoginService(Context context, Callable<Void> callback) {
        this.context = context;
        this.callback = callback;
    }

    public Token getToken(User user, Context context) {
        Map<String, String> params = new HashMap<>();

        params.put("email", user.getEmail());
        params.put("password", user.getPassword());

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://teste.luby.com.br/api/token";

        System.out.println("Request -> " + new JSONObject(params));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response -> " + response);
                        assignToken(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error -> " + error);
                        showError();
                    }
                });

        queue.add(jsonObjectRequest);

        return token;
    }

    private void assignToken(JSONObject token) {
        this.token = new Gson().fromJson(token.toString(), Token.class);

        /*
        System.out.println("token -> " + token);
        System.out.println("this.token -> " + this.token.getToken());
        System.out.println("this.token -> " + this.token.getExpiresIn());
        System.out.println("this.token -> " + this.token.getTokenType());
        System.out.println("this.token -> " + this.token.getIssued());
        System.out.println("this.token -> " + this.token.getExpires());
        System.out.println("this.token -> " + this.token.getId());
        System.out.println("this.token -> " + this.token.getDocumentNumber());
        System.out.println("this token -> " + this.token.getEmail());
        System.out.println("this.token -> " + this.token.getName());
        */

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Token.TOKEN_TAG, token.toString());
        editor.commit();

        try {
            callback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showError()
    {
        Toast.makeText(context, "Status: 422\nUsuario não reconhecido", Toast.LENGTH_SHORT).show();
    }

}
