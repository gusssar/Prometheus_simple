package com.example.station_admin.prometheus_15;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView LogContText;
    TextView HumContText;
    int pos=0;
    String[] viewlist = {   "Выберите...",
                            "Список валют",
                            "Валютные пары",
                            "Статистика цен",
                            "Книга ордеров",
                            "Список сделок"};
    String[] GateWaySite ={ "",
                            "https://api.exmo.com/v1/currency/",
                            "https://api.exmo.com/v1/pair_settings/",
                            "https://api.exmo.com/v1/ticker/",
                            "https://api.exmo.com/v1/order_book/?pair=BTC_USD",
                            "https://api.exmo.com/v1/trades/?pair=BTC_USD"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogContText = (TextView) findViewById(R.id.LogContView);
        HumContText = (TextView) findViewById(R.id.HumContView);

        //адаптер для spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, viewlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.CheckMethodView);
        spinner.setAdapter(adapter);

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // показываем позицию нажатого элемента
                pos=position;
                Toast.makeText(getBaseContext(), "Позиция = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void go_post_btn(View v) {
        Intent intent = new Intent(this, PairSettingsActivity.class);
        startActivity(intent);
    }

    public void on_post_btn(View v) {
 //       if (LogContText.length() == 0) {
            LogContText.setText("Загрузка...");
            new MainActivity.ProgressTask().execute(GateWaySite[pos]);
 //       }
 //       else {
 //           Toast.makeText(getApplicationContext(),"Заполенено", Toast.LENGTH_SHORT).show();
 //       }
    }

    class ProgressTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... path) {
            String content;
            try {
                content = getContent(path[0]);
            } catch (IOException ex) {
                content = ex.getMessage();
            }
            return content;
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

        @Override
        protected void onPostExecute(String content) {
            LogContText.setText(content);
            Toast.makeText(getApplicationContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
        }

    }

    public void on_trance_btn(View v) {
        if (LogContText.length() == 0) {
            Toast.makeText(getApplicationContext(), "Запросите данные", Toast.LENGTH_SHORT).show();
        }
        else {
            HumContText.setText("Для людей:\n"); //очистка TextView
            /**необходима доработка для объединения регулярных выражений(изучить синтаксис)*/
            String pattern1 = "\"";
            String pattern2 = "\\[";
            String pattern3 = "]";
            String chpattern = "";

            Pattern ptrn1 = Pattern.compile(pattern1);
            Pattern ptrn2 = Pattern.compile(pattern2);
            Pattern ptrn3 = Pattern.compile(pattern3);
            Matcher mtch1 = ptrn1.matcher(LogContText.getText().toString()); //преобразователь char в string
            String inputString1 = mtch1.replaceAll(chpattern);
            Matcher mtch2 = ptrn2.matcher(inputString1);
            String inputString2 = mtch2.replaceAll(chpattern);
            Matcher mtch3 = ptrn3.matcher(inputString2);
            String inputString3 = mtch3.replaceAll(chpattern);

            Pattern pattern = Pattern.compile(","); //условие для разделения на массив

            String[] mas = pattern.split(inputString3);
            for (int i=0; i<mas.length;i++)
                HumContText.append(mas[i]+"\n");
            Toast.makeText(getApplicationContext(), "Данные преобразованы", Toast.LENGTH_SHORT).show();
        }
    }
}






