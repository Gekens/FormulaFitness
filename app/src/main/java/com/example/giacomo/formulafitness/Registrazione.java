package com.example.giacomo.formulafitness;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Registrazione extends AppCompatActivity {
    String sname, ssurname, smail, spassword, spassword2, encryp, entityResponse;
    Boolean sicurezza = false;
    View view1, view2, view3, view4;
    EditText name, surname, mail, password, password2;
    Button registrati, accedi;
    TextView textSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        mail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        registrati = (Button) findViewById(R.id.btnRegister);
        accedi = (Button) findViewById(R.id.btnLinkToLoginScreen);
        textSeek = (TextView) findViewById(R.id.textViewSeek);
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        view3 = (View) findViewById(R.id.view3);
        view4 = (View) findViewById(R.id.view4);

        File file = getBaseContext().getFileStreamPath("utente.txt");
        if(file.exists()) {
            launchHomeScreen();
            finish();
        }



        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().length() < 6) {
                    view1.setBackgroundColor(getResources().getColor(R.color.moltodebole));
                    view2.setBackgroundColor(getResources().getColor(R.color.defaultview));
                    textSeek.setTextColor(getResources().getColor(R.color.moltodebole));
                    textSeek.setText("Molto Debole");
                    sicurezza = false;
                }
                else if (password.getText().toString().length() < 10) {
                    view1.setBackgroundColor(getResources().getColor(R.color.debole));
                    view2.setBackgroundColor(getResources().getColor(R.color.debole));
                    view3.setBackgroundColor(getResources().getColor(R.color.defaultview));
                    textSeek.setTextColor(getResources().getColor(R.color.debole));
                    textSeek.setText("Debole");
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    sicurezza = false;
                }
                else if (password.getText().toString().length() < 14) {
                    view1.setBackgroundColor(getResources().getColor(R.color.media));
                    view2.setBackgroundColor(getResources().getColor(R.color.media));
                    view3.setBackgroundColor(getResources().getColor(R.color.media));
                    view4.setBackgroundColor(getResources().getColor(R.color.defaultview));
                    textSeek.setTextColor(getResources().getColor(R.color.media));
                    textSeek.setText("Media");
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    sicurezza = true;
                }
                else if (password.getText().toString().length() >= 14) {
                    view1.setBackgroundColor(getResources().getColor(R.color.sicura));
                    view2.setBackgroundColor(getResources().getColor(R.color.sicura));
                    view3.setBackgroundColor(getResources().getColor(R.color.sicura));
                    view4.setBackgroundColor(getResources().getColor(R.color.sicura));
                    textSeek.setTextColor(getResources().getColor(R.color.sicura));
                    textSeek.setText("Sicura");
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    sicurezza = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = name.getText().toString();
                ssurname = surname.getText().toString();
                smail = mail.getText().toString();
                spassword = password.getText().toString();
                spassword2 = password2.getText().toString();

                if (sname.length() == 0) {
                    Toast.makeText(Registrazione.this, "Inserisci il tuo nome", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                }
                else if (ssurname.length() == 0) {
                    Toast.makeText(Registrazione.this, "Inserisci il tuo cognome", Toast.LENGTH_SHORT).show();
                    surname.requestFocus();
                }
                else if (smail.length() == 0) {
                    Toast.makeText(Registrazione.this, "Inserisci la tua mail", Toast.LENGTH_SHORT).show();
                    mail.requestFocus();
                }
                else if (spassword.length() == 0) {
                    Toast.makeText(Registrazione.this, "Inserisci la tua password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                }
                else if (spassword2.length() == 0) {
                    Toast.makeText(Registrazione.this, "Inserisci di nuovo la password", Toast.LENGTH_SHORT).show();
                    password2.requestFocus();
                }
                else {
                    if (sicurezza == true) {

                        if (spassword.equals(spassword2)) {
                            //Le password corrispondono

                            encryp = getEncryptedPassword(getSalt(), spassword);

                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                            StrictMode.setThreadPolicy(policy);

                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                            nameValuePairs.add(new BasicNameValuePair("name", sname));
                            nameValuePairs.add(new BasicNameValuePair("surname", ssurname));
                            nameValuePairs.add(new BasicNameValuePair("mail", smail));
                            nameValuePairs.add(new BasicNameValuePair("encryp", encryp));

                            try {
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpGet httpGet = new HttpGet("http://zgdata.esy.es/checkUser.php?mail=" + smail);
                                HttpResponse httpResponse = httpClient.execute(httpGet);
                                HttpEntity httpEntity = httpResponse.getEntity();

                                if (httpEntity != null) {
                                    try {
                                        entityResponse = EntityUtils.toString(httpEntity);

                                        Log.e("Entity Response  : ", entityResponse);


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (ClientProtocolException e) {
                                Log.d("Log tag", e + "");
                            } catch (IOException e) {
                                Log.d("Log tag", e + "");
                            }

                            if (entityResponse.equals(smail)) {
                                Toast.makeText(Registrazione.this, "Account già registrato", Toast.LENGTH_SHORT).show();
                            } else {

                                try {
                                    HttpClient httpClient = new DefaultHttpClient();
                                    HttpPost httpPost = new HttpPost("http://zgdata.esy.es/setUser.php");
                                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                                    HttpResponse httpResponse = httpClient.execute(httpPost);
                                    HttpEntity httpEntity = httpResponse.getEntity();

                                    Toast.makeText(Registrazione.this, "Registrato con successo", Toast.LENGTH_SHORT).show();

                                    Intent vIntent = new Intent(Registrazione.this, Login.class);
                                    startActivity(vIntent);
                                } catch (ClientProtocolException e) {
                                    Log.d("Log tag", e + "");
                                } catch (IOException e) {
                                    Log.d("Log tag", e + "");
                                }
                            }

                        } else {
                            textSeek.setTextColor(getResources().getColor(R.color.moltodebole));
                            textSeek.setText("Le password non corrispondono");
                        }
                    }
                    else {
                        Toast.makeText(Registrazione.this, "La password deve contenere almeno 10 caratteri", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void launchHomeScreen() {
        startActivity(new Intent(Registrazione.this, Login.class));
        finish();
    }

    public static String getSalt(){
        String uuid = "palcast";
        return uuid;
    }
    public static String getEncryptedPassword(String salt, String password){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            md.update(salt.getBytes());

            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

}
