package com.ika.airpsollution.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ika.airpsollution.R;
import com.ika.airpsollution.messages.Message;
import com.ika.airpsollution.messages.MessageAdapter;
import com.ika.airpsollution.rest.AqicnService;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap.OnMyLocationButtonClickListener locationButtonClickListener;
    private boolean permissionDenied = false;
    private GoogleMap map;

    double currentLatitude;
    double currentLongitude;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(locationButtonClickListener);

            LatLng etf = new LatLng(44.8057225423691, 20.47609300860253);
            googleMap.addMarker(new MarkerOptions().position(etf).title("Marker in ETF"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(etf));

            LatLng tesla = new LatLng(44.805724150117754, 20.470684693983138);
            googleMap.addMarker(new MarkerOptions().position(tesla).title("Marker in Tesla Museum"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(tesla));

            LatLng curr = new LatLng(currentLatitude, currentLongitude);
            googleMap.addMarker(new MarkerOptions().position(curr).title("Your position"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        locationButtonClickListener = () -> {
            fusedLocationClient = getFusedLocationProviderClient(getContext());

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken()).addOnSuccessListener(location -> {
                if(location!=null){
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    Toast.makeText(getContext(),"Lat: " + currentLatitude + " Long: " + currentLongitude, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"NULL", Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        };

        AqicnService aqicnService = new AqicnService();
        aqicnService.getAllStationss();
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            fusedLocationClient = getFusedLocationProviderClient(getContext());
                        } else {
                            Toast.makeText(getContext(),"U tom slucaju nije moguce odrediti vasu lokaciju...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
            );
}