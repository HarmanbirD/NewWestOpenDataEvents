package opendata;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.room.Room;
import ca.bcit.myapplication.R;
import opendata.data.CulturalEvents;
import opendata.database.CulturalEvent;
import opendata.database.CulturalEventDOA;
import opendata.database.CultureEventsDatabase;

import static opendata.data.CulturalEvents.Feature.Properties;

public class MainActivity
    extends ListActivity
{
    @NonNull
    private static final String TAG = MainActivity.class.getName();

    @NonNull
    private CultureEventsDatabase database;

    @NonNull
    private ArrayAdapter<String> namesAdapter;

    @NonNull
    private List<String> names;

    @NonNull
    private TextToSpeech textToSpeechEngine;

    CulturalEventDOA culturalEventDOA;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(),
                CultureEventsDatabase.class,
                "artists").build();

        textToSpeechEngine = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener()
                {
                    @Override
                    public void onInit(final int status)
                    {
                        if(status != TextToSpeech.ERROR)
                        {
                            textToSpeechEngine.setLanguage(Locale.CANADA);
                        }
                    }
                });

        culturalEventDOA = database.culturalEventDOA();

        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                names = culturalEventDOA.getAllNames();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        namesAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, names);
                        setListAdapter(namesAdapter);

                        AsyncTask.execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                downloadData("http://opendata.newwestcity.ca/downloads/cultural-events/EVENTS.json", culturalEventDOA);
                            }
                        });
                    }
                });
            }
        });
    }

    private void downloadData(@NonNull final String url,
                              @NonNull final CulturalEventDOA culturalEventDOA)
    {
        // TODO: check date of download and date of file on server, if different download
        // Hints:
        // Look at https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
        // When you download the file get the date of it
        // Store that date in the settings
        // Before downloading check the date of the file, if it is later than the stored one download it
        //     and overwrite the database
        if (culturalEventDOA.count() == 0)
        {
            Ion.with(MainActivity.this)
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(final Exception ex,
                                                final JsonObject json)
                        {
                            if (ex != null)
                            {
                                Log.e(TAG, "Error", ex);
                            }

                            if (json != null)
                            {
                                AsyncTask.execute(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        final List<String> tempNames;

                                        parseJSON(json, culturalEventDOA);
                                        tempNames = culturalEventDOA.getAllNames();
                                        names.clear();
                                        names.addAll(tempNames);

                                        runOnUiThread(new Runnable()
                                        {
                                            public void run()
                                            {
                                                namesAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void parseJSON(final JsonObject json,
                           final CulturalEventDOA culturalEventDOA)
    {
        final Gson      gson;
        final CulturalEvents culturalEvents;

        gson = new Gson();
        culturalEvents = gson.fromJson(json, CulturalEvents.class);

        for(CulturalEvents.Feature feature : culturalEvents.getFeatures())
        {
            final Properties properties;
            final int       id;
            final String    name;
            final String    email;
            final String    phone;
            final String    address;
            final String    description;
            final String    city;
            final String    province;
            final String    website;
            final double    latitude;
            final double    longitude;
            final CulturalEvent culturalEvent;

            properties  = feature.getProperties();
            name        = properties.getName();
            email       = properties.getEmail();
            phone       = properties.getPhone();
            address     = properties.getAddress();
            description = properties.getDescription();
            city        = properties.getCity();
            province    = properties.getProvince();
            website     = properties.getWebsite();
            latitude    = Double.parseDouble(properties.getX());
            longitude   = Double.parseDouble(properties.getY());
            culturalEvent = new CulturalEvent();
            culturalEvent.setName(name);
            culturalEvent.setEmail(email);
            culturalEvent.setPhone(phone);
            culturalEvent.setAddress(address);
            culturalEvent.setDescription(description);
            culturalEvent.setCity(city);
            culturalEvent.setProvince(province);
            culturalEvent.setWebsite(website);
            culturalEvent.setLatitude(latitude);
            culturalEvent.setLongitude(longitude);
            culturalEventDOA.insertAll(culturalEvent);
        }
    }

    // http://www.mkyong.com/android/android-listview-example/
    @Override
    protected void onListItemClick(final ListView listView, final View view, final int position, final long id)
    {
        final String selectedValue;

        selectedValue = (String)getListAdapter().getItem(position);
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                Context context = MainActivity.this;
                final String address = culturalEventDOA.getAddress(selectedValue);
                final String description = culturalEventDOA.getDescription(selectedValue);
                final TextView message = new TextView(context);
                // i.e.: R.string.dialog_message =>
                // "Test this dialog following the link to dtmilano.blogspot.com"
                final SpannableString s =
                        new SpannableString(culturalEventDOA.getWebsite(selectedValue));
                Linkify.addLinks(s, Linkify.WEB_URLS);
                String temp = "Address: " + address + "\n\nDescription: " + description;
                message.setText(s);
                message.setMovementMethod(LinkMovementMethod.getInstance());



                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        dlgAlert.setMessage(temp);
                        dlgAlert.setView(message);
                        dlgAlert.setTitle(selectedValue);
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                });
            }
        });
    }
}
