package com.example.station_admin.prometheus_15;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class FragmentProgress extends Fragment {
    TextView LogContView;
    TextView HumContView;
    String LogContText = null;
    String HumContText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        LogContView = (TextView) view.findViewById(R.id.LogContView);
        HumContView = (TextView) view.findViewById(R.id.HumContView);

        Button OnPostBtn = (Button) view.findViewById(R.id.OnPostBtn);

        OnPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LogContText == null) {
                    LogContView.setText("Загрузка...");
                    new FragmentProgress.ProgressTask().execute("https://api.exmo.com/v1/currency/");
                }
            }
        });

        Button OnTranceBtn = (Button) view.findViewById(R.id.OnTranceBtn);

        OnTranceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LogContText == null) {
                   // HumContView.setText("Пусто...");
                    Toast.makeText(getActivity(), "Запросите данные", Toast.LENGTH_SHORT).show();
                }
                else {
                    HumContView.setText("Для людей:\n"); //очистка TextView
            /**необходима доработка для объединения регулярных выражений(изучить синтаксис)*/
                    String pattern1 = "\"";
                    String pattern2 = "\\[";
                    String pattern3 = "]";
                    String chpattern = "";

                    Pattern ptrn1 = Pattern.compile(pattern1);
                    Pattern ptrn2 = Pattern.compile(pattern2);
                    Pattern ptrn3 = Pattern.compile(pattern3);
                         Matcher mtch1 = ptrn1.matcher(LogContText);
                             String inputString1 = mtch1.replaceAll(chpattern);
                         Matcher mtch2 = ptrn2.matcher(inputString1);
                             String inputString2 = mtch2.replaceAll(chpattern);
                          Matcher mtch3 = ptrn3.matcher(inputString2);
                             String inputString3 = mtch3.replaceAll(chpattern);

                    Pattern pattern = Pattern.compile(","); //условие для разделения на массив

                    String[] mas = pattern.split(inputString3);
                    for (int i=0; i<mas.length;i++)
                        HumContView.append(mas[i]+"\n");
                    Toast.makeText(getActivity(), "Данные преобразованы", Toast.LENGTH_SHORT).show();
                }

            }

        });

 //       Button GoToPairSetBtn = (Button) view.findViewById(R.id.GoToPairSet);
 //       GoToPairSetBtn.setOnClickListener(new View.OnClickListener() {
 //           @Override
 //           public void onClick(View v) {
 //               Intent intent = new Intent(this, PairSettingsActivity.class);
 //               startActivity(intent);
 //           }
 //       });

        return view;
    }

    private class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {
            String content;
            try{
                content = getContent(path[0]);
            }
            catch (IOException ex){
                content = ex.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            LogContText=content;
            LogContView.setText(content);
            Toast.makeText(getActivity(), "Данные загружены", Toast.LENGTH_SHORT).show();
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader=null;
            try {
                URL url=new URL(path);
                HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
                c.setRequestMethod("POST");
                c.setReadTimeout(10000);
                c.connect();
                reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line=null;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return(buf.toString());
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }
}
