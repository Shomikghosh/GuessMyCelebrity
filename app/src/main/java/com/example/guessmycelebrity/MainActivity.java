package com.example.guessmycelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {


    ArrayList<String> celebUrls= new ArrayList<String>();
    ArrayList<String> celebNames= new ArrayList<String>();
    int choosenCeleb=0;
    int locationOfCorrectAnswer=0;
    String[] answers = new String[4];


    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen (View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong it was "+celebNames.get(choosenCeleb), Toast.LENGTH_SHORT).show();
        }
        createNewQuestion();
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url =new URL(urls[0]);

                HttpURLConnection connection =(HttpURLConnection)url.openConnection();

                connection.connect();
                InputStream inputStream =connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }



    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                String inputLine;

                while ((inputLine = br.readLine()) != null)
                {
                    // System.out.println(inputLine);
                    result += inputLine;
                }
                br.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView =(ImageView)findViewById(R.id.imageView);
        button0 =(Button)findViewById(R.id.button);
        button1 =(Button)findViewById(R.id.button2);
        button2 =(Button)findViewById(R.id.button3);
        button3 =(Button)findViewById(R.id.button4);




        DownloadTask task = new DownloadTask();

        String result = null;
        int x=0;


        try {result = task.execute("https://www.imdb.com/list/ls052283250/").get();

            String[] splitResult = result.split("<div id=\"top_rhs_wrapper\" class=\"cornerstone_slot\">");

            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                if (x > 5)
                    celebUrls.add(m.group(1));
                x++;
            }
            p = Pattern.compile(" alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {

                celebNames.add(m.group(1));

            }





        }catch (Exception e){

            e.printStackTrace();
        }
        createNewQuestion();
        
    }
    public void createNewQuestion() {


        Random random = new Random();
        choosenCeleb = random.nextInt(celebUrls.size());

        ImageDownloader imageTask = new ImageDownloader();

        Bitmap celebImage;

        try {
            celebImage = imageTask.execute(celebUrls.get(choosenCeleb - 2)).get();

            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = (celebNames.get(choosenCeleb));
                    Log.i("Correct answer", celebNames.get(choosenCeleb));
                } else {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    while (incorrectAnswerLocation == choosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}