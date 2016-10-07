package gomez.alejandro.teconfortas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Modulos.DataParser;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    ListView list;
    public static Polyline ruta;
    public static boolean bandera=false;
    String[] itemname ={ "Hoteles cercas de mi", "Todos los hoteles", "Restaurantes cercas de mi","Todos los restaurantes","Busqueda personalizada","Calificar Sitio"  };
    Button btnruta;
    Integer[] imgid= {
            R.drawable.markhotel,
            R.drawable.markhotel,
            R.drawable.restaurantmarker,
            R.drawable.restaurantmarker,
            R.drawable.restaurantmarker,
            R.drawable.mr_ic_play_dark,
    };

            String la="";
    String lo="";
    String server_url="http://teconfortascolima.esy.es/webservice/servicios/sitioscercas.php";
    String server_url2="http://teconfortascolima.esy.es/webservice/servicios/todoslossitios.php";
    String server_url3="http://teconfortascolima.esy.es/webservice/servicios/informacionmarker.php";
    String server_url4="http://teconfortascolima.esy.es/webservice/servicios/todoslosrestaurantes.php";
    String server_url5="http://teconfortascolima.esy.es/webservice/servicios/restaurantescercas.php";
    private RequestQueue requestQueue;
    private ImageLoader mImageloader;



    ListView listView;
    DrawerLayout drawerLayout;
    Button btn;
private Marker MI;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);
        list=(ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter);
      btn=(Button)findViewById(R.id.btncal);
         listView = (ListView) findViewById(R.id.list_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        mMap.clear();
        contorno();
        sitioscercas();
    }
});


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
       contorno();
        ubicacion ubi =new ubicacion(this);
        if(mMap!=null) {
            //Activamos la capa o layer MyLocation
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                LatLng actual = new LatLng(ubi.lat, ubi.lo);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actual,15));
            } else {
                // Show rationale and request permission.
            }



        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String n=marker.getTitle();

                Toast.makeText(getApplicationContext(),n,Toast.LENGTH_LONG).show();
                obtiene(n);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),"tocaste",Toast.LENGTH_LONG).show();
                marker.showInfoWindow();
                latlon(marker.getTitle());
                return true;

            }
        });


       // final  String[] opciones = { "Hoteles cercas de mi", "Todos los hoteles", "Restaurantes cercas de mi", "Todos los restaurantes","Busqueda Personalizada","Calificar Sitio" };
        // listView.setAdapter(new ArrayAdapter(this,
             //   R.layout.list_white_color, R.id.list_content,
            //    opciones));




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(MapsActivity.this, "Item: " +itemname[arg2],
                        Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                if(arg2==0){
                    mMap.clear();
                    contorno();
                    sitioscercas();
                }
                else{
                    if(arg2==1){
                        mMap.clear();
                        contorno();
                        todoslossitios();
                    }else{
                        if(arg2==5){

                            Intent i=new Intent(MapsActivity.this,QRActivity.class);
                            MapsActivity.this.startActivity(i);
                        }else{
                            if(arg2==3){
                                mMap.clear();
                                contorno();
                                todoslosrestaurantes();
                            }else{
                                if(arg2==2){
                                    mMap.clear();
                                    contorno();
                                    restaurantescercas();
                                }
                            }
                        }

                    }

                }
            }
        });


    }


    public void contorno(){

        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(19.229418, -103.774255),
                        new LatLng(19.227696,-103.775209),
                        new LatLng(19.221921, -103.771154),
                        new LatLng(19.221962, -103.770102),
                        new LatLng(19.224252, -103.769437),
                        new LatLng(19.223765, -103.767967),
                        new LatLng(19.230046, -103.763880),
                        new LatLng(19.230431, -103.764631),
                        new LatLng(19.234280, -103.762249),
                        new LatLng(19.232194, -103.758236),
                        new LatLng(19.232133, -103.755940),
                        new LatLng(19.230066, -103.751134),
                        new LatLng(19.231241, -103.750061),
                        new LatLng(19.230978, -103.748859),
                        new LatLng(19.229357, -103.748838),
                        new LatLng(19.224393, -103.741542),
                        new LatLng(19.224545, -103.740115),
                        new LatLng(19.221638, -103.737358),
                        new LatLng(19.222003, -103.736843),
                        new LatLng(19.220220, -103.734740),
                        new LatLng(19.214384, -103.738281),
                        new LatLng(19.213553, -103.736178),
                        new LatLng(19.215154, -103.735298),
                        new LatLng(19.214830, -103.729118),
                        new LatLng(19.212682, -103.725256),
                        new LatLng(19.212763, -103.723453),
                        new LatLng(19.204962, -103.724762),
                        new LatLng(19.201841, -103.729633),
                        new LatLng(19.200696, -103.728550),
                        new LatLng(19.201264, -103.726533),
                        new LatLng(19.199967, -103.724816),
                        new LatLng(19.201183, -103.724859),
                        new LatLng(19.199926, -103.721984),
                        new LatLng(19.205519, -103.719409),
                        new LatLng(19.202682, -103.716448),
                        new LatLng(19.210463, -103.710096),
                        new LatLng(19.217839, -103.715761),
                        new LatLng(19.223390, -103.711469),
                        new LatLng(19.220027, -103.703358),
                        new LatLng(19.224201, -103.700741),
                        new LatLng(19.223998, -103.698638),
                        new LatLng(19.219257, -103.701041),
                        new LatLng(19.217150, -103.694818),
                        new LatLng(19.223188, -103.692587),
                        new LatLng(19.223431, -103.694647),
                        new LatLng(19.228618, -103.691814),
                        new LatLng(19.228456, -103.673661),
                        new LatLng(19.242881, -103.675850),
                        new LatLng(19.243083, -103.676794),
                        new LatLng(19.242637, -103.677008),
                        new LatLng(19.242394, -103.676322),
                        new LatLng(19.238586, -103.677309),
                        new LatLng(19.240936, -103.681686),
                        new LatLng(19.244137, -103.680055),
                        new LatLng(19.246973, -103.687780),
                        new LatLng(19.250133, -103.686192),
                        new LatLng(19.253293, -103.689540),
                        new LatLng(19.256899, -103.685463),
                        new LatLng(19.259695, -103.687952),
                        new LatLng(19.262774, -103.684046),
                        new LatLng(19.272456, -103.700140),
                        new LatLng(19.275210, -103.698723),
                        new LatLng(19.278535, -103.703393),
                        new LatLng(19.276732, -103.704809),
                        new LatLng(19.280418, -103.712663),
                        new LatLng(19.274352, -103.714884),
                        new LatLng(19.273684, -103.717609),
                        new LatLng(19.268114, -103.721171),
                        new LatLng(19.258492, -103.723660),
                        new LatLng(19.260740, -103.730119),
                        new LatLng(19.257216, -103.730247),
                        new LatLng(19.256446, -103.732243),
                        new LatLng(19.254785, -103.732780),
                        new LatLng(19.256365, -103.737329),
                        new LatLng(19.248302, -103.740504),
                        new LatLng(19.244413, -103.743680),
                        new LatLng(19.244251, -103.748315),
                        new LatLng(19.241941, -103.753744),
                        new LatLng(19.242144, -103.758979),
                        new LatLng(19.233685, -103.783709),
                        new LatLng(19.230565, -103.778774),
                        new LatLng(19.229418, -103.774255));

// Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions);

    }

    public void sitioscercas(){


        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                               mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" Mts").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhotel)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                 String lo=String.valueOf(ubi.lo);
                  params.put("latitud",la);
                  params.put("longitud",lo);
//




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

        public void todoslossitios(){
//prueba

        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url2,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                               mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" Mts").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhotel)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public void obtiene(final String nombre){
//prueba



        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url3,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String imagen=sitios.getString("imagen");
                                String descripcion=sitios.getString("descripcion");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");


                                  Toast.makeText(getApplicationContext(),imagen,Toast.LENGTH_LONG).show();
                                Intent nextScreen = new Intent(MapsActivity.this,InformacionActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("imagen",imagen);
                                bundle.putString("nombre",name);
                                bundle.putString("descripcion",descripcion);
                                bundle.putString("latitud",lat);
                                bundle.putString("longitud",lon);
                                nextScreen.putExtras(bundle);
                                startActivityForResult(nextScreen, 0);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre",nombre);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }
    public void  todoslosrestaurantes(){

        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url4,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" km").icon(BitmapDescriptorFactory.fromResource(R.drawable.restmarker)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void restaurantescercas(){
        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url5,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" km").icon(BitmapDescriptorFactory.fromResource(R.drawable.restmarker)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);
//




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void latlon(final String nombre){
//prueba

      final  ubicacion ubi=new ubicacion(this);

        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url3,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                if(bandera==true){
                                    ruta.setVisible(false);
                                }
                               LatLng destino=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                              LatLng origen=new LatLng(ubi.lat,ubi.lo);

                               // Toast.makeText(getApplicationContext(),"laa: "+latituddestino+" loo: "+longituddestino,Toast.LENGTH_LONG).show();
                                String url = getUrl(origen, destino);
                                Log.d("onMapClick", url.toString());
                                FetchUrl FetchUrl = new FetchUrl();
                                FetchUrl.execute(url);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre",nombre);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyCfYUMjL9bQOc58LIUXD2nmGVyOXdjDJvU";
        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {

                 ruta=mMap.addPolyline(lineOptions);
bandera=true;

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }



    }
