package opendata;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Arrays;
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
    implements View.OnClickListener
{
    @NonNull
    private FirebaseAuth firebaseAuth;

    @NonNull
    private static final String TAG = MainActivity.class.getName();

    @NonNull
    private CultureEventsDatabase database;

    @NonNull
    private ArrayAdapter<String> namesAdapter;

    @NonNull
    private List<String> names;

    private Button logoutButton;
    private Button createEvent;

    private DatabaseReference databaseReference;

    private DatabaseReference databaseReferenceUserEvents;

    private ProgressDialog progressDialog;

    CulturalEventDOA culturalEventDOA;

    List<CulturalEvent> eventList;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null)
        {
            Intent intent = new Intent(this, ca.bcit.myapplication.login.LoginActivity.class);
            startActivity(intent);
        }

        createEvent = findViewById(R.id.newEvent);

        database = Room.databaseBuilder(getApplicationContext(),
                CultureEventsDatabase.class,
                "artists").build();

        culturalEventDOA = database.culturalEventDOA();

        names = new ArrayList<>();

        databaseReference           = FirebaseDatabase.getInstance().getReference("CulturalEvent");
        databaseReferenceUserEvents = FirebaseDatabase.getInstance().getReference("UserEvent");

        progressDialog = new ProgressDialog(this);

        eventList = new ArrayList<>();

        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        namesAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_2, android.R.id.text1, names);
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

        logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                names.clear();

                for (DataSnapshot eventSnapShot : dataSnapshot.getChildren())
                {
                    CulturalEvent culturalEvent = eventSnapShot.getValue(CulturalEvent.class);

                    eventList.add(culturalEvent);
                    names.add(culturalEvent.getName());
                }

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        namesAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        databaseReferenceUserEvents.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for (DataSnapshot eventSnapShot : dataSnapshot.getChildren())
                {
                    CulturalEvent culturalEvent = eventSnapShot.getValue(CulturalEvent.class);

                    eventList.add(culturalEvent);
                    names.add(culturalEvent.getName());
                }

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        namesAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                        parseJSON(json);
                                    }
                                });
                            }
                        }
                    });
    }

    private void parseJSON(final JsonObject json)
    {
        databaseReference.removeValue();
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

            String eyeD = databaseReference.push().getKey();
            databaseReference.child(eyeD).setValue(culturalEvent);
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

                CulturalEvent temp = new CulturalEvent();

                boolean isThere = false;

                for (CulturalEvent e : eventList)
                {
                    if (e.getName().equals(selectedValue))
                    {
                        isThere = true;
                        temp = e;
                    }
                }

                if (isThere) {
                    final String name = temp.getName();
                    final String address = temp.getAddress();
                    final String description = temp.getDescription();
                    final TextView message = new TextView(context);
                    // i.e.: R.string.dialog_message =>
                    // "Test this dialog following the link to dtmilano.blogspot.com"
                    final SpannableString s =
                            new SpannableString(temp.getWebsite());
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                    String addy = "Address: " + address + "\n\nDescription: " + description;
                    message.setText(s);
                    message.setMovementMethod(LinkMovementMethod.getInstance());
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);


                    runOnUiThread(new Runnable() {
                        public void run() {
                            dlgAlert.setMessage(addy);
                            dlgAlert.setView(message);
                            dlgAlert.setTitle(name);
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    });
                }
            }
        });
    }


    public void createEventIntent(View v)
    {
        startActivity(new Intent(this, opendata.CreateActivity.class));
    }

    @Override
    public void onClick(View v)
    {
        if (v == logoutButton)
        {
            firebaseAuth.signOut();
            startActivity(new Intent(this, ca.bcit.myapplication.login.LoginActivity.class));
        }
    }
}
