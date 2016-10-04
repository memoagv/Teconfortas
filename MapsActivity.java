package gomez.alejandro.teconfortas;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String la="";
    String lo="";
    String server_url="http://teconfortascolima.esy.es/webservice/servicios/sitioscercas.php";
    String server_url2="http://teconfortascolima.esy.es/webservice/servicios/todoslossitios.php";
    private RequestQueue requestQueue;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ListView listView = (ListView) findViewById(R.id.list_view);
       final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       final  String[] opciones = { "Sitios cercas de mi", "Todos los sitios de colima", "Buscar por filtro", "Calificar Sitio" };
        listView.setAdapter(new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                opciones));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(MapsActivity.this, "Item: " + opciones[arg2],
                        Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                if(opciones[arg2]=="Sitios cercas de mi"){
                    mMap.clear();
                    contorno();
                   sitioscercas();
                }
                else{
                    if(opciones[arg2]=="Todos los sitios de colima"){
                        mMap.clear();
                        contorno();
                        todoslossitios();
                    }

                }
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

//hola



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



}
