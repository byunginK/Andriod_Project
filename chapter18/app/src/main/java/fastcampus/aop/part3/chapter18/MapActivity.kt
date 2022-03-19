package fastcampus.aop.part3.chapter18

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fastcampus.aop.part3.chapter18.databinding.ActivityMapBinding
import fastcampus.aop.part3.chapter18.model.LocationLatLngEntity
import fastcampus.aop.part3.chapter18.model.SearchResultEntity
import fastcampus.aop.part3.chapter18.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private lateinit var searchResult: SearchResultEntity
    private var currentMarker: Marker? = null

    private lateinit var locationManager: LocationManager

    private lateinit var myLocationListener: MyLocationListener

    private lateinit var job: Job

    //코루틴 context 오버라이드
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
                setUpGoogleMap()
            }
        }
        bindViews()
    }

    private fun bindViews() = with(binding) {
        currentLocationButton.setOnClickListener {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        //초기화가 되어있는지 확인
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        //GPS 권한을 가져옴
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        //권한 승인
        if (isGpsEnabled) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                setMyLocationListener()
            }
        }
    }

    //권한을 불러옴옴
    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )
        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener()
    }

    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let {
                                currentMarker = setUpMarker(
                                    SearchResultEntity(
                                        fullAdress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                        name = "내 위치",
                                        locationLatLng = locationLatLngEntity
                                    )
                                )
                                currentMarker?.showInfoWindow()
                            }
                        }
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationListener()
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MyLocationListener : LocationListener {
        //현재 위치를 콜백
        override fun onLocationChanged(location: Location) {
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
            onCurrentLocationChanged(locationLatLngEntity)
        }

    }

    private fun setUpGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentMarker = setUpMarker(searchResult)
        currentMarker?.showInfoWindow()
    }

    private fun setUpMarker(searchResultEntity: SearchResultEntity): Marker? {
        val positionLatLng = LatLng(
            searchResultEntity.locationLatLng.latitude.toDouble(),
            searchResultEntity.locationLatLng.longitude.toDouble()
        )
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResultEntity.name)
            snippet(searchResultEntity.fullAdress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }
}