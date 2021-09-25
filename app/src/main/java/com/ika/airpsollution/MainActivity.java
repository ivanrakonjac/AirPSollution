package com.ika.airpsollution;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ika.airpsollution.db.FirebaseDB;
import com.ika.airpsollution.db.FirebaseStorageDB;
import com.ika.airpsollution.geofences.GeofenceBroadcastReceiver;
import com.ika.airpsollution.messages.Message;
import com.ika.airpsollution.ui.slideshow.ChatFragment;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;

    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    public static String userName = ANONYMOUS;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ImageButton photoPickerButton;

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new LinkedList<>();
    private PendingIntent geofencePendingIntent = null;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    Intent signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setLogo(R.drawable.logo)
            .setTheme(R.style.FirebaseUI_DefaultMaterialTheme)
            .build();

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            MainActivity.userName = user.getDisplayName();
            Toast.makeText(this, "You are signed in!", Toast.LENGTH_SHORT).show();

            onUserChanged();
        } else if (result.getResultCode() == RESULT_CANCELED) finishAffinity();
        else Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("ETF")

                .setCircularRegion(
                        44.866316905728844, 20.665574277113578,
                        100
                )
                .setExpirationDuration(86400000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Log.d("#GEOFENCE", "Dodato");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        Log.d("#GEOFENCE", e.toString());
                    }
                });
        */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_statistics, R.id.nav_chat)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user == null){
            signInLauncher.launch(signInIntent);
        }else{
            userName = user.getDisplayName();
        }

        onUserChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_logout:
                AuthUI.getInstance().signOut(this);
//                finishAffinity(); Imao sam bag pri logovanju kad ovo ubacim

                ChatFragment.clearMessageAdapter();
                ChatFragment.detachDatabaseReadListener();

                userName = ANONYMOUS;

                signInLauncher.launch(signInIntent);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){

            Uri selectedImageUri = data.getData();
            StorageReference photoRef = FirebaseStorageDB.getProfilePhotosReference().child(userName);

            Toast.makeText(this,"Izabrana slika " + selectedImageUri.getLastPathSegment(),Toast.LENGTH_SHORT).show();

            // Upload file to Firebase Storage

            UploadTask uploadTask = photoRef.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        NavigationView navigationView = findViewById(R.id.nav_view);
                        View headerView = navigationView.getHeaderView(0);
                        ImageView profilePhoto= (ImageView) headerView.findViewById(R.id.imageView);

                        Glide.with(getBaseContext())
                                .load(downloadUri.toString())
                                .into(profilePhoto);

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }

    private void onUserChanged(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView= (TextView) headerView.findViewById(R.id.userNameTextView);
        userNameTextView.setText(MainActivity.userName);

        if(photoPickerButton == null){
            photoPickerButton = (ImageButton) headerView.findViewById(R.id.userPhotoPickerBtn);

            photoPickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                }
            });
        }

        StorageReference photoRef = FirebaseStorageDB.getProfilePhotosReference().child(userName);

        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                ImageView profilePhoto= (ImageView) headerView.findViewById(R.id.imageView);

                Glide.with(getBaseContext())
                        .load(uri.toString())
                        .into(profilePhoto);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                ImageView profilePhoto= (ImageView) headerView.findViewById(R.id.imageView);
                profilePhoto.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.outline_person_24));
            }
        });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }



}