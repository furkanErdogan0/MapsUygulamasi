package com.furkanerdogan.haritauygulamasi

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.furkanerdogan.haritauygulamasi.databinding.ActivityMapsBinding
import com.google.android.material.snackbar.Snackbar
import java.security.Permission
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var takipBoolean : Boolean? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()
        sharedPreferences = getSharedPreferences("com.furkanerdogan.haritauygulamasi", MODE_PRIVATE)
        takipBoolean = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val anitkabir = LatLng(39.9251, 32.8368)
        mMap.addMarker(MarkerOptions().position(anitkabir).title("Anıtkabir"))

        val ankaraKalesi = LatLng(39.9390704, 32.8647979)
        mMap.addMarker(MarkerOptions().position(ankaraKalesi).title("Ankara Kalesi"))

        val asti = LatLng(39.91832239527055, 32.81222087646622)
        mMap.addMarker(MarkerOptions().position(asti).title("Aşti"))

        val ankamall = LatLng(39.95058979063691, 32.83131528251907)
        mMap.addMarker(MarkerOptions().position(ankamall).title("ANKAmall"))

        val atakule = LatLng(39.88611621665867, 32.8558208854916)
        mMap.addMarker(MarkerOptions().position(atakule).title("Atakule"))

        val tbmm = LatLng(39.91207878784684, 32.854351351831994)
        mMap.addMarker(MarkerOptions().position(tbmm).title("TBMM"))

        val kizilayMeydani = LatLng(39.92081152738435, 32.853606423527246)
        mMap.addMarker(MarkerOptions().position(kizilayMeydani).title("Kızılay Meydanı"))

        val ulus = LatLng(39.94191794389442, 32.85457121082342)
        mMap.addMarker(MarkerOptions().position(ulus).title("Ulus Meydanı"))

        val genclikParki = LatLng(39.93738683051196, 32.84967121186207)
        mMap.addMarker(MarkerOptions().position(genclikParki).title("Gençlik Parkı"))

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                takipBoolean = sharedPreferences.getBoolean("takipBoolean", false)
                if(!takipBoolean!!) {
                    val kullaniciKonumu = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(kullaniciKonumu).title("Konumunuz"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonumu, 12f))
                    sharedPreferences.edit().putBoolean("takipBoolean", true).apply()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.root, "Konumunuzu almak için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction(
                    "İzin Ver"
                ) {
                    //izni isteyeceğiz
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                //izni isteyeceğiz
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, locationListener)
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null) {
                val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng, 14f))
            }
        }

    }

    private fun registerLauncher () {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                if(ContextCompat.checkSelfPermission(this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, locationListener)
                    val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (sonBilinenKonum != null) {
                        val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng, 14f))
                    }
                }
            } else {
                Toast.makeText(this@MapsActivity, "İzne ihtiyacımız var.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()

        //geocoder

        val geocoder = Geocoder(this, Locale.getDefault())
        val adres = ""

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    p0.latitude,
                    p0.longitude,
                    1,
                    Geocoder.GeocodeListener { adresListesi ->
                        val ilkAdres = adresListesi.first()

                        val ulkeAdi = ilkAdres.countryName
                        val sokak = ilkAdres.thoroughfare
                        val numara = ilkAdres.subThoroughfare

                        Toast.makeText(
                            this@MapsActivity,
                            "${sokak}, ${numara}, ${ulkeAdi} ",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

            } else {

                val adresListesi: List<Address>? = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                if (adresListesi != null && adresListesi.isNotEmpty()) {
                    val ilkAdres = adresListesi[0]
                    val ulkeAdi = ilkAdres.countryName
                    val sokak = ilkAdres.thoroughfare
                    val numara = ilkAdres.subThoroughfare

                    Toast.makeText(
                        this@MapsActivity,
                        "${sokak}, ${numara}, ${ulkeAdi} ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMap.addMarker(MarkerOptions().position(p0))

    }
}