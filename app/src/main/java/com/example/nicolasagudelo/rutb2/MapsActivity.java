package com.example.nicolasagudelo.rutb2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RequestQueue requestQueue;
    public static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Intent login = new Intent();
        login.setClass(getApplicationContext(), Login.class);
        startActivity(login);*/
        setContentView(R.layout.activity_maps);
        String tokenGuardado = getDefaults("TokenGuardado", getApplicationContext());
        System.out.println("EXITO: " + tokenGuardado);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button logoutB = (Button) findViewById(R.id.sign_out);
        logoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cerrarSesion = new Intent();
                cerrarSesion.setClass(getApplicationContext(), Login.class);
                cerrarSesion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cerrarSesion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                cerrarSesion.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                setDefaults("Codigo", "", getApplicationContext());
                setDefaults("TokenGuardado", "", getApplicationContext());
                setDefaults("Password", "", getApplicationContext());
                startActivity(cerrarSesion);
            }
        });

    }

    private boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Presione otra vez para salir",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        // Nueva petición JSONObject
            JsonObjectRequest peticion = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://labsoftware03.unitecnologica.edu.co/archivoNicolas",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Double lat = Double.valueOf(response.getString("latitud"));
                                Double lon = Double.valueOf(response.getString("longitud"));
                                LatLng posicion = new LatLng(lat, lon);
                                mMap.addMarker(new MarkerOptions().position(posicion).title("Ruta 1"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 16));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                        }
                    }
            );
            requestQueue.add(peticion);
    }
}
