package com.example.rss2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> authors;
    ArrayList<String> all;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRss = findViewById(R.id.lvRss);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        authors = new ArrayList<String>();
        all = new ArrayList<String>();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(links.get(i));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //intent.putExtra("URL", uri);
                startActivity(intent);

            }
        });

        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>{

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("המסך טוען נא להמתין");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try
            {
                URL url = new URL("https://www.yutorah.org/search/rss.cfm?q=&f=language:HE,seriesid:4331,teacherishidden:0&s=shiurdate%20desc");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {


                    if (eventType == XmlPullParser.START_TAG)
                    {

                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }

                        else if (xpp.getName().equalsIgnoreCase("author"))
                        {

                            if (insideItem)
                            {
                                Spanned spanned = Html.fromHtml(xpp.nextText());
                                authors.add(spanned.toString());
                            }
                        }

                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {


                            if (insideItem)
                            {
                                Spanned spanned = Html.fromHtml(xpp.nextText());
                                titles.add(spanned.toString());
                            }

                        }
                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideItem)
                            {
                                links.add(xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e)
            {
                exception = e;

            }
            catch (XmlPullParserException e)
            {
                exception = e;
            }
            catch (IOException e)
            {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);


            for (int i = 0; i < authors.size(); i++) {
                all.add(authors.get(i) + "\n" + titles.get(i));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, all);

            lvRss.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }
}