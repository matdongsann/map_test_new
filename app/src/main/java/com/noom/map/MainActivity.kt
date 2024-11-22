package com.noom.map

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import org.json.JSONObject
import com.naver.maps.map.overlay.PathOverlay

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    // 코스별 묶음
    private val courses = listOf(
        listOf( // 아차산팔각정길코스
            LatLng(37.5682855771655, 127.08954113941945) to 100, // 팔각정 출발
            LatLng(37.568964917250035, 127.09361975824189) to 400  // 팔각정 코스 정상
        ),
        listOf( // 아차산중곡동코스
            LatLng(37.56282047179632, 127.09599460201434) to 100, // 출발점1
            LatLng(37.572157676087585, 127.10324563543132) to 600  // 갈림길6, 중동곡 정상
        ),
        listOf( // 아차산해맞이길코스
            LatLng(37.556149253255256, 127.09517684687525) to 100, // 출발점2
            LatLng(37.55954971434981, 127.10154752012248) to 500  // 갈림길5, 해맞이길 정상
        ),
        listOf( // 아차산고구려정길코스
            LatLng(37.55525905943138, 127.0984831965767) to 150, // 출발점3-2
            LatLng(37.55776093554935, 127.1018959407125) to 300, // 갈림길3, 구의동 정상
            LatLng(37.55867030411321, 127.10263283725347) to 400  // 갈림길4, 고구려 정길 정상
        ),
        listOf( // 아차산구의동코스
            LatLng(37.55438856103244, 127.0970136480338) to 100, // 출발점3-1
            LatLng(37.55525905943138, 127.0984831965767) to 150, // 출발점3-2
            LatLng(37.55547258367111, 127.10168628344292) to 200, // 구의동 코스 중간지점
            LatLng(37.55776093554935, 127.1018959407125) to 300  // 갈림길3, 구의동 정상
        ),
        listOf( // 아차산정상길코스
            LatLng(37.552661018633124, 127.09957468886509) to 100, // 출발점4
            LatLng(37.55788978681432, 127.10395310824592) to 300, // 갈림길2, 산성길 정상
            LatLng(37.55867030411321, 127.10263283725347) to 400, // 갈림길4, 고구려 정길 정상
            LatLng(37.55954971434981, 127.10154752012248) to 500, // 갈림길5, 해맞이길 정상
            LatLng(37.572157676087585, 127.10324563543132) to 600, // 갈림길6, 중동곡 정상
            LatLng(37.573076634684895, 127.1007027343968) to 650, // 정상 중간지점
            LatLng(37.57105576028153, 127.09576179734566) to 700  // 정상
        ),
        listOf( // 아차산광장동코스
            LatLng(37.5513775739213, 127.10165808627804) to 100, // 출발점5
            LatLng(37.55233540513523, 127.10105110599241) to 120, // 광장동 코스 지점1
            LatLng(37.552124517500296, 127.10268044460652) to 140, // 광장동 코스 지점2
            LatLng(37.55244505023488, 127.10189719060794) to 160, // 광장동 코스 지점3
            LatLng(37.554934477544, 127.10399710132221) to 200  // 갈림길1, 광장동 코스 정상
        ),
        listOf( // 아차산성길코스
            LatLng(37.551861745076486, 127.10437194404655) to 100, // 출발점6
            LatLng(37.554934477544, 127.10399710132221) to 200, // 갈림길1, 광장동 코스 정상
            LatLng(37.55788978681432, 127.10395310824592) to 300  // 갈림길2, 산성길 정상
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // 위치 권한 요청
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))

        // FusedLocation
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // naverMap 객체 획득을 위한 getMapAsync() 수행
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // naverMap 객체 획득
        this.naverMap = naverMap
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)

        // naverMap locationSource 에 FusedLocationSource 적용
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.None
        naverMap.mapType = NaverMap.MapType.Terrain

        // 카메라 초기위치
        val initialPosition = LatLng(37.562367, 127.102649) // 아차산역 좌표
        val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
        naverMap.moveCamera(cameraUpdate)

        drawHikingRoutesByCourses(courses)

    }

    private fun drawHikingRoutesByCourses(courses: List<List<Pair<LatLng, Int>>>) {
        courses.forEachIndexed { courseIndex, course ->
            val path = PathOverlay().apply {
                coords = course.map { it.first } // Pair에서 LatLng만 추출
                color = when (courseIndex) {
                    0 -> 0xAAFF0000.toInt() // 빨간색
                    1 -> 0xAAFF7F00.toInt() // 주황색
                    2 -> 0xAAFFFF00.toInt() // 노란색
                    3 -> 0xAA00FF00.toInt() // 초록색
                    4 -> 0xAA0000FF.toInt() // 파란색
                    5 -> 0xAA4B0082.toInt() // 남색
                    6 -> 0xAA8B00FF.toInt() // 보라색
                    else -> 0xAAFFFFFF.toInt() // 흰색
                }
                map = naverMap
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted
            Toast.makeText(this, "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT)
            finish()
        }
        }
    }
}

/*

    LatLng(37.5682855771655, 127.08954113941945) to 100,  // 팔각정 출발
    LatLng(37.56282047179632, 127.09599460201434) to 100, // 출발점1
    LatLng(37.556149253255256, 127.09517684687525) to 100, // 출발점2
    LatLng(37.55438856103244, 127.0970136480338) to 100, // 출발점3-1
    LatLng(37.55525905943138, 127.0984831965767) to 150, // 출발점3-2
    LatLng(37.552661018633124, 127.09957468886509) to 100, // 출발점4
    LatLng(37.5513775739213, 127.10165808627804) to 100, // 출발점5
    LatLng(37.551861745076486, 127.10437194404655) to 100, // 출발점6
    LatLng(37.568964917250035, 127.09361975824189) to 400, // 팔각정 코스 정상
    LatLng(37.554934477544, 127.10399710132221) to 200, // 갈림길1, 광장동 코스 정상
    LatLng(37.55233540513523, 127.10105110599241) to 120, // 광장동 코스 지점1
    LatLng(37.552124517500296, 127.10268044460652) to 140, // 광장동 코스 지점2
    LatLng(37.55244505023488, 127.10189719060794) to 160, // 광장동 코스 지점3
    LatLng(37.55788978681432, 127.10395310824592) to 300, // 갈림길2, 산성길 정상
    LatLng(37.55776093554935, 127.1018959407125) to 300, // 갈림길3, 구의동 정상
    LatLng(37.55547258367111, 127.10168628344292) to 200, // 구의동 코스 중간지점
    LatLng(37.55867030411321, 127.10263283725347) to 400, // 갈림길4, 고구려 정길 정상
    LatLng(37.55954971434981, 127.10154752012248) to 500, // 갈림길5, 해맞이길 정상
    LatLng(37.572157676087585, 127.10324563543132) to 600, // 갈림길6, 중동곡 정상
    LatLng(37.573076634684895, 127.1007027343968) to 650, // 정상 중간지점
    LatLng(37.57105576028153, 127.09576179734566) to 700  // 정상

*/