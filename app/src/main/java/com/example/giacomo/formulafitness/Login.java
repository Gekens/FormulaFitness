package com.example.giacomo.formulafitness;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    String smail, spassword, encryp;
    EditText mail, password;
    Button accedi, rip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        accedi = (Button) findViewById(R.id.btnLogin);
        rip = (Button) findViewById(R.id.rip);


        try {
            FileInputStream fileIn=openFileInput("utente.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[100];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            if (s != "") {
                Intent vIntent = new Intent(Login.this, MainActivity.class);
                startActivity(vIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smail = mail.getText().toString();
                spassword = password.getText().toString();

                if (smail.length() == 0) {
                    Toast.makeText(Login.this, "Inserisci la tua mail", Toast.LENGTH_SHORT).show();
                    mail.requestFocus();
                }
                else if (spassword.length() == 0) {
                    Toast.makeText(Login.this, "Inserisci la tua password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                }
                else {
                    encryp = getEncryptedPassword(getSalt(), spassword);

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("mail", smail));
                    nameValuePairs.add(new BasicNameValuePair("encryp", encryp));

                    String entityResponse;
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("http://zgdata.esy.es/getUser.php?mail=" + smail + "&encryp=" + encryp);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();

                        if (httpEntity != null) {
                            try {
                                entityResponse = EntityUtils.toString(httpEntity);
                                Log.e("Entity Response  : ", entityResponse);

                                try {
                                    FileOutputStream fileout = openFileOutput("utente.txt", MODE_PRIVATE);
                                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                                    outputWriter.write(smail + " " + encryp);
                                    outputWriter.close();
                                }
                                catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Intent vIntent = new Intent(Login.this, MainActivity.class);
                                startActivity(vIntent);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (ClientProtocolException e) {
                        Log.d("Log tag", e + "");
                    } catch (IOException e) {
                        Log.d("Log tag", e + "");
                    }

                }
            }
        });
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
