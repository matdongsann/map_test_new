package com.noom.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import android.widget.TextView

class SubActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var naverMap: NaverMap
    private lateinit var currentCourseTextView: TextView // 현재 코스를 표시하는 TextView!
    private var currentPath: PathOverlay? = null
    private var currentCourseIndex = -1 // 현재 선택된 코스
    private val activeMarkers = mutableListOf<Marker>() // 활성화된 마커 리스트

    // 코스 데이터
    private val ascendingCourses = listOf(
        listOf( // 아차산팔각정길코스
            LatLng(37.56828773085198, 127.0896713136574), // 팔각정 출발
            LatLng(37.56812745954494, 127.09012389154401), // 지점1
            LatLng(37.56815851963888, 127.09074365818617), // 지점2
            LatLng(37.56836079171448, 127.09132967660608), // 지점3
            LatLng(37.56844566450484, 127.09225796271039), // 지점4
            LatLng(37.56879827142802, 127.09357144242063), // 지점5
            LatLng(37.568964917250035, 127.09361975824189)  // 팔각정 코스 정상
        ),
        listOf( // 아차산중곡동코스
            LatLng(37.56238784437634, 127.09617797062278), // 중곡동 출발
            LatLng(37.56225070721982, 127.09585239145795), // 지점1
            LatLng(37.56282497447187, 127.09599743740357), // 지점2
            LatLng(37.56339459582589, 127.09631508725624), // 지점3
            LatLng(37.563901285448075, 127.09646288151082), // 지점4
            LatLng(37.56410155776834, 127.09670649051887), // 지점5
            LatLng(37.564383065382074, 127.09677193684443), // 지점6
            LatLng(37.564867227144575, 127.09692253666354), // 지점7
            LatLng(37.566068979419796, 127.09822577159323), // 지점8
            LatLng(37.566843258316794, 127.09891725067058), // 지점9
            LatLng(37.56759282967628, 127.09952098761794), // 지점10
            LatLng(37.56836733342785, 127.09993800183499), // 지점11
            LatLng(37.568796838896695, 127.10078469716672), // 지점12
            LatLng(37.56911417561774, 127.10109357836957), // 지점13
            LatLng(37.56985734294168, 127.10127286511715), // 지점14
            LatLng(37.57027136699472, 127.10177714674398), // 지점15
            LatLng(37.57138799715469, 127.10247483042048), // 지점16
            LatLng(37.571500432025154, 127.10269288984823), // 지점17
            LatLng(37.57195300640772, 127.10289443821448), // 지점18
            LatLng(37.572157676087585, 127.10324563543132)  // 갈림길6, 중동곡 정상
        ),
        listOf( // 아차산해맞이길코스
            LatLng(37.556149253255256, 127.09517684687525), // 해맞이길 출발
            LatLng(37.55627064332638, 127.09547974322237), // 지점1
            LatLng(37.55662181878015, 127.09574332338534), // 지점2
            LatLng(37.55663717905569, 127.09624414293124), // 지점3
            LatLng(37.55657403086798, 127.0963402604826), // 지점4
            LatLng(37.55676707036073, 127.0971638594071), // 지점5
            LatLng(37.557045987031245, 127.09763955964675), // 지점6
            LatLng(37.55846366811313, 127.09930512429943), // 지점7
            LatLng(37.558512267953276, 127.10043413884624), // 지점8
            LatLng(37.55873955490679, 127.10068626561774), // 지점9
            LatLng(37.558811436480624, 127.10091837806436), // 지점10
            LatLng(37.55921649914755, 127.1013688109583), // 지점11
            LatLng(37.55954971434981, 127.10154752012248)  // 갈림길5, 해맞이길 정상
        ),
        listOf( // 아차산고구려정길코스
            LatLng(37.55438856103244, 127.0970136480338), // 구의동 출발
            LatLng(37.55482922916548, 127.09800730693125), // 지점(1)
            LatLng(37.55528834181234, 127.09848323511196), // 고구려정길 출발
            LatLng(37.555499903491736, 127.09869005497214), // 지점1
            LatLng(37.555540490881874, 127.09863918044205), // 지점2
            LatLng(37.555634989057985, 127.09876662529803), // 지점3
            LatLng(37.55578588699597, 127.09878945921825), // 지점4
            LatLng(37.555848970935656, 127.09877256640743), // 지점5
            LatLng(37.55587145322688, 127.09882352436193), // 지점6
            LatLng(37.556101095886355, 127.09895680742866), // 지점7
            LatLng(37.556438393603074, 127.09964196108267), // 지점8
            LatLng(37.55667038338538, 127.0996620756392), // 지점9
            LatLng(37.556843636247166, 127.09988582784636), // 지점10
            LatLng(37.556685751036085, 127.10013460251247), // 지점11
            LatLng(37.55700750365273, 127.10055095366052), // 지점12
            LatLng(37.55698482922979, 127.10072634543305), // 지점13
            LatLng(37.55724562667414, 127.10130106484787), // 지점14
            LatLng(37.55776093554935, 127.1018959407125), // 갈림길3, 구의동 정상
            LatLng(37.55818646296036, 127.102120045639), // 지점15
            LatLng(37.55867030411321, 127.10263283725347)  // 갈림길4, 고구려 정길 정상
        ),
        listOf( // 아차산구의동코스
            LatLng(37.55438856103244, 127.0970136480338), // 구의동 출발
            LatLng(37.55482922916548, 127.09800730693125), // 지점1
            LatLng(37.55528834181234, 127.09848323511196), // 고구려정길 출발
            LatLng(37.5554722635903, 127.0994171573129), // 지점2
            LatLng(37.55503441836921, 127.10043512985955), // 지점3
            LatLng(37.555110302268595, 127.10125573452746), // 지점4
            LatLng(37.5555040941685, 127.10171461960904), // 지점5
            LatLng(37.55567501492547, 127.10202607947829), // 지점6
            LatLng(37.55624463053826, 127.10233242727341), // 지점7
            LatLng(37.556607283902494, 127.10233009381963), // 지점8ㅁ
            LatLng(37.55776093554935, 127.1018959407125)  // 갈림길3, 구의동 정상
        ),
        listOf( // 아차산정상길코스
            LatLng(37.552661018633124, 127.09957468886509), // 정상길 출발
            LatLng(37.55292223128744, 127.09966557199391), // 지점1
            LatLng(37.553387974813965, 127.1002829698098), // 지점2
            LatLng(37.55387144437506, 127.10123991200247), // 지점3
            LatLng(37.554150614888684, 127.10140155883144), // 지점4
            LatLng(37.554373650368746, 127.1013565923549), // 지점5
            LatLng(37.55454723292476, 127.10119272811559), // 지점6
            LatLng(37.555114812102566, 127.10125008198888), // 지점7
            LatLng(37.55549959162476, 127.10171178415446), // 지점8
            LatLng(37.55568177728633, 127.1020204300144), // 지점9
            LatLng(37.55578501701266, 127.10245346063125), // 지점10
            LatLng(37.55599880204848, 127.10268576031777), // 지점11
            LatLng(37.556494298198565, 127.10274585705673), // 지점12
            LatLng(37.5571943832293, 127.10325045129034), // 지점13
            LatLng(37.55788978681432, 127.10395310824592), // 갈림길2, 산성길 정상
            LatLng(37.558636070488284, 127.10314492135595), // 지점14
            LatLng(37.558690332690084, 127.10291298081052), // 지점15
            LatLng(37.55867030411321, 127.10263283725347), // 갈림길4, 고구려 정길 정상
            LatLng(37.5589835107233, 127.10250594086065), // 지점16
            LatLng(37.55954971434981, 127.10154752012248), // 갈림길5, 해맞이길 정상
            LatLng(37.560065485621614, 127.10160481025713), // 지점17
            LatLng(37.56068283294454, 127.10141324049519), // 지점18
            LatLng(37.56153430272465, 127.10138043985177), // 지점19
            LatLng(37.56244407433797, 127.10165331397953), // 지점20
            LatLng(37.56257895520829, 127.10196475351698), // 지점21
            LatLng(37.56327740216565, 127.1017619721459), // 지점22
            LatLng(37.564614826668134, 127.10240329892916), // 지점23
            LatLng(37.565378387798226, 127.10244113034629), // 지점24
            LatLng(37.56679939188598, 127.10280528585638), // 지점25
            LatLng(37.56733548849009, 127.1028003630678), // 지점26
            LatLng(37.56852443055816, 127.10322647115596), // 지점27
            LatLng(37.56998834278394, 127.10346054186112), // 지점28
            LatLng(37.57060517075378, 127.10386324175418), // 지점29
            LatLng(37.57105576719245, 127.10375067055782), // 지점30
            LatLng(37.57156917431155, 127.10393249907142), // 지점31
            LatLng(37.57193880069767, 127.10368397654425), // 지점32
            LatLng(37.572157676087585, 127.10324563543132), // 갈림길6, 중동곡 정상
            LatLng(37.57233586701657, 127.10296571423497), // 지점33
            LatLng(37.57229796392794, 127.1025185267705), // 지점34
            LatLng(37.57241556369861, 127.10197533284838), // 지점35
            LatLng(37.57278302362752, 127.10162208531983), // 지점36
            LatLng(37.572821539687425, 127.10136177857719), // 지점37
            LatLng(37.572893767267814, 127.10118924691274), // 지점38
            LatLng(37.57301546444411, 127.10111583164051), // 지점39
            LatLng(37.5729976019873, 127.10093185770745), // 지점40
            LatLng(37.57307438461503, 127.10069990136749), // 지점41
            LatLng(37.57298903850784, 127.10040829682826), // 지점42
            LatLng(37.57299366854809, 127.10026114322548), // 지점43
            LatLng(37.572627021658995, 127.09966069537822), // 지점44
            LatLng(37.572523483803195, 127.09956999808837), // 지점45
            LatLng(37.572480932092226, 127.09927845379241), // 지점46
            LatLng(37.57257356904323, 127.09893897908499), // 지점47
            LatLng(37.57250173111415, 127.09865022630052), // 지점48
            LatLng(37.57230383112915, 127.09826791925147), // 지점49
            LatLng(37.57213046282437, 127.09817996257902), // 지점50
            LatLng(37.57175272509436, 127.09735878132355), // 지점51
            LatLng(37.57114767017468, 127.09630526096169), // 지점52
            LatLng(37.5711095117124, 127.09614107615964), // 지점53
            LatLng(37.571175011029624, 127.09592325591412), // 지점54
            LatLng(37.57105576028153, 127.09576179734566)  // 정상
        ),
        listOf( // 아차산광장동코스
            LatLng(37.5513775739213, 127.10165808627804), // 광장동 출발
            LatLng(37.55164343859453, 127.10157640090205), // 지점1
            LatLng(37.55170655946639, 127.10151707354679), // 지점2
            LatLng(37.5518957663588, 127.10152015937798), // 지점3
            LatLng(37.55203331176485, 127.10135342292409), // 지점4
            LatLng(37.55208297580069, 127.10122617592467), // 지점5
            LatLng(37.55225198784622, 127.10113869893902), // 지점6
            LatLng(37.55234442720796, 127.10103697209598), // 지점7
            LatLng(37.552079894732515, 127.10218810163163), // 지점8
            LatLng(37.55211561817866, 127.10255311808952), // 지점9
            LatLng(37.5520975515443, 127.10260684825137), // 지점10
            LatLng(37.55209972040625, 127.10270304423689), // 지점11
            LatLng(37.552156062225535, 127.10266917104985), // 지점12
            LatLng(37.552198930881445, 127.10258718276607), // 지점13
            LatLng(37.552246586469316, 127.10217984158764), // 지점14
            LatLng(37.55239997598193, 127.10192542141691), // 지점15
            LatLng(37.552420238637744, 127.10193676588497), // 지점16
            LatLng(37.55248111695494, 127.10186611828428), // 지점17
            LatLng(37.55257565079213, 127.10194829445356), // 지점18
            LatLng(37.55260490142124, 127.10198511422715), // 지점19
            LatLng(37.55269273881858, 127.10199655083383), // 지점20
            LatLng(37.552661167269314, 127.10203894622005), // 지점21
            LatLng(37.55279634854266, 127.10200235049957), // 지점22
            LatLng(37.55288427388919, 127.10191193478329), // 지점23
            LatLng(37.55304643862352, 127.10192913105409), // 지점24
            LatLng(37.55314985272735, 127.10216127006471), // 지점25
            LatLng(37.5531340142691, 127.10224329655982), // 지점26
            LatLng(37.55316099270309, 127.10230274758953), // 지점27
            LatLng(37.5531654486459, 127.10235933861114), // 지점28
            LatLng(37.553354640748424, 127.10237940214226), // 지점29
            LatLng(37.55351432689179, 127.10265688803929), // 지점30
            LatLng(37.55361766556067, 127.10297390731168), // 지점31
            LatLng(37.55381343714914, 127.10319768908778), // 지점32
            LatLng(37.55386076665827, 127.10316663235035), // 지점33
            LatLng(37.55455642966605, 127.10357501040855), // 지점34
            LatLng(37.554729665221195, 127.10381008253415), // 지점35
            LatLng(37.5549434850143, 127.10399994315316)  // 갈림길1, 광장동 코스 정상
        ),
        listOf( // 아차산성길코스
            LatLng(37.551861745076486, 127.10437194404655), // 산성길 출발
            LatLng(37.55225860528677, 127.10389436061176), // 지점1
            LatLng(37.55233754947506, 127.10377281394416), // 지점2
            LatLng(37.552380369191056, 127.10374741036337), // 지점3
            LatLng(37.552583063594305, 127.10378164203088), // 지점4
            LatLng(37.55262365816223, 127.10372511380116), // 지점5
            LatLng(37.55261477238005, 127.10358364020202), // 지점6
            LatLng(37.552738723920484, 127.10351025176301), // 지점7
            LatLng(37.55316443238955, 127.10352498677165), // 지점8
            LatLng(37.553470565072196, 127.1037602386513), // 지점9
            LatLng(37.55354487738789, 127.10378297576581), // 지점10
            LatLng(37.55363257271952, 127.10395568248859), // 지점11
            LatLng(37.55372258763057, 127.10405200261943), // 지점12
            LatLng(37.55380820223381, 127.10402948748538), // 지점13
            LatLng(37.55383294473004, 127.10406913168235), // 지점14
            LatLng(37.55388032929572, 127.10397583154726), // 지점15
            LatLng(37.554017636520236, 127.10408353490179), // 지점16
            LatLng(37.5541077910095, 127.10402141607874), // 지점17
            LatLng(37.554267583163885, 127.10417441974083), // 지점18
            LatLng(37.554357782591765, 127.10406137363483), // 지점19
            LatLng(37.554414030006896, 127.10413501348198), // 지점20
            LatLng(37.554502081533435, 127.10390313364701), // 지점21
            LatLng(37.55458537886452, 127.10395417669828), // 지점22
            LatLng(37.55462131154146, 127.10407588657694), // 지점23
            LatLng(37.554704601310135, 127.10413541780557), // 지점24
            LatLng(37.5549434850143, 127.10399994315316),  // 갈림길1, 광장동 코스 정상
            LatLng(37.555326131358214, 127.10431453034558), // 지점25
            LatLng(37.55636212174448, 127.10449139523801), // 지점26
            LatLng(37.55788978681432, 127.10395310824592)  // 갈림길2, 산성길 정상
        )
    )

    // 코스 이름
    private val courseNames = listOf(
        "팔각정 코스",
        "중곡동 코스",
        "해맞이길 코스",
        "고구려 정길 코스",
        "구의동 코스",
        "정상길 코스",
        "광장동 코스",
        "산성길 코스"
    )

    // 교차 지점 정의
    private val intersections = mapOf(
        LatLng(37.572157676087585, 127.10324563543132) to listOf(1, 5), // 중동곡 정상
        LatLng(37.55954971434981, 127.10154752012248) to listOf(2, 5), // 해맞이길 정상
        LatLng(37.55776093554935, 127.1018959407125) to listOf(3, 4), // 구의동 정상
        LatLng(37.55867030411321, 127.10263283725347) to listOf(3, 5), // 고구려 정길 정상
        LatLng(37.5549434850143, 127.10399994315316)  to listOf(6, 7), // 광장동 코스 정상
        LatLng(37.55788978681432, 127.10395310824592) to listOf(5, 7) // 산성길 정상
    )

    // 출발 지점 정의 (각 코스의 첫 좌표)
    private val startingPoints = ascendingCourses.map { it.first() }
    // 하산 코스 계산
    private val descendingCourses = calculateDescendingCourses()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // 현재 코스 표시 TextView 초기화
        currentCourseTextView = findViewById(R.id.current_course_text)

        // Reset 버튼 연결
        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            resetMap()
        }

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 지도 초기 위치
        val initialPosition = LatLng(37.562367, 127.102649)
        naverMap.moveCamera(CameraUpdate.scrollTo(initialPosition))

        // 초기 코스 선택 Dialog
        showInitialCourseSelectionDialog()

    }

    private fun showInitialCourseSelectionDialog() {
        val options = courseNames.flatMap { name ->
            listOf("$name (등산)", "$name (하산)")
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("시작할 코스를 선택하세요")
            .setItems(options) { _, which ->
                val isDescending = which % 2 == 1
                currentCourseIndex = which / 2
                displayCourse(currentCourseIndex, isDescending)
            }
            .setCancelable(false)
            .show()
    }

    private fun displayCourse(courseIndex: Int, isDescending: Boolean) {
        currentPath?.map = null

        val selectedCourse = if (isDescending) descendingCourses[courseIndex] else ascendingCourses[courseIndex]
        currentPath = PathOverlay().apply {
            coords = selectedCourse
            color = if (isDescending) 0xAA0000FF.toInt() else 0xAAFF0000.toInt()
            map = naverMap
        }

        displayIntersections(courseIndex, isDescending)
        updateCurrentCourseText(isDescending)
    }

    private fun displayIntersections(courseIndex: Int, isDescending: Boolean) {
        activeMarkers.forEach { it.map = null }
        activeMarkers.clear()

        val intersectionsForCourse = intersections.filter { it.value.contains(courseIndex) }
        intersectionsForCourse.forEach { (position, connectedCourses) ->
            val marker = Marker().apply {
                this.position = position
                this.captionText = "교차 지점"
                this.map = naverMap
            }
            marker.setOnClickListener {
                showIntersectionDialog(connectedCourses, position)
                true
            }
            activeMarkers.add(marker)
        }
    }

    private fun showIntersectionDialog(connectedCourses: List<Int>, position: LatLng) {
        val options = mutableListOf<String>()
        connectedCourses.forEach { options.add("${courseNames[it]} (등산)") }
        connectedCourses.forEach { options.add("${courseNames[it]} (하산)") }

        AlertDialog.Builder(this)
            .setTitle("교차 지점: 새로운 코스 선택")
            .setItems(options.toTypedArray()) { _, which ->
                val isDescending = which >= connectedCourses.size
                val selectedIndex = if (isDescending) which - connectedCourses.size else which
                currentCourseIndex = connectedCourses[selectedIndex]
                displayCourse(currentCourseIndex, isDescending)
            }
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentCourseText(isDescending: Boolean) {
        val type = if (isDescending) "하산" else "등반"
        val courseName = courseNames.getOrNull(currentCourseIndex) ?: "없음"
        currentCourseTextView.text = "현재 코스: $courseName ($type)"
    }

    private fun calculateDescendingCourses(): List<List<LatLng>> {
        return ascendingCourses.map { course ->
            val reversedCourse = course.reversed()
            reversedCourse
        }
    }

    private fun resetMap() {
        // 기존 경로 및 마커 제거
        currentPath?.map = null
        activeMarkers.forEach { it.map = null }
        activeMarkers.clear()

        // 카메라를 초기 위치로 이동
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(37.562367, 127.102649)))

        // 현재 코스 정보 초기화
        currentCourseIndex = -1
        currentCourseTextView.text = "현재 코스: 없음"

        // 초기 코스 선택 다이얼로그 표시
        showInitialCourseSelectionDialog()
    }
}
