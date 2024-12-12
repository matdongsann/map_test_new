package com.noom.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.noom.map.R
import com.noom.map.SubActivity


data class HikingCourse(
    val name: String,       // 코스 이름
    val MET: Int, // 난이도
    val time: Float,       // 소요 시간
    val distance: Float,   // 거리
    val gradient: Float,   // 경사도
    val number: Int,         // 코스 번호
    val index: Int          // 인덱스 번호
)

class MainActivity : AppCompatActivity() {



    // 가중치
    var C = 0
    private var fC_time: Float = 0f // 추천 코스 시간 변수
    var recommendedCourse: HikingCourse? = null // 추천 코스를 저장하는 변수


    private var userAge: Int? = null
    private var userHeight: Int? = null
    private var userWeight: Int? = null


    val hikingCourses = listOf(
        HikingCourse("아차산중곡동코스", 4, 0.68f, 1.4f, 0.175f, 3, 1),
        HikingCourse("아차산정상길코스", 6, 1.53f, 3.4f, 0.102f, 7, 5),
        HikingCourse("아차산고구려정길코스", 6, 0.33f, 0.64f, 0.313f, 6, 3),
        HikingCourse("아차산성길코스", 6, 0.43f, 0.801f, 0.244f, 9, 7),
        HikingCourse("아차산해맞이길코스", 6, 0.45f, 0.732f, 0.312f, 8, 2),
        HikingCourse("아차산광장동코스", 6, 0.43f, 0.769f, 0.225f, 4, 6),
        HikingCourse("아차산구의동코스", 6, 0.42f, 0.794f, 0.229f, 5, 4),
        HikingCourse("아차산팔각정코스", 8, 0.43f, 0.403f, 0.643f, 10, 0)
    )

    @SuppressLint("MissingInflatedId")
    fun course(){
        setContentView(R.layout.course)

        // 체크박스 초기화
        val purpose_box = listOf(
            findViewById<CheckBox>(R.id.Walk).apply { tag = 1 },
            findViewById<CheckBox>(R.id.Climb).apply { tag = 3 },
            findViewById<CheckBox>(R.id.Strong).apply { tag = 5 }
        )

        val career_box = listOf(
            findViewById<CheckBox>(R.id.A).apply { tag = 0 },
            findViewById<CheckBox>(R.id.B).apply { tag = 2 },
            findViewById<CheckBox>(R.id.C).apply { tag = 3 },
            findViewById<CheckBox>(R.id.D).apply { tag = 4 }
        )

        val timeout_box = listOf(
            findViewById<CheckBox>(R.id.one).apply { tag = 1 },
            findViewById<CheckBox>(R.id.two).apply { tag = 2 },
            findViewById<CheckBox>(R.id.three).apply { tag = 3 }
        )

        val checkBoxListener: (CompoundButton, Boolean) -> Unit = { buttonView, isChecked ->
            val tagValue = buttonView.tag as? Int ?: 0 // XML에서 설정한 Tag 값을 가져옴
            if (isChecked) {
                C += tagValue // C에 Tag 값을 더함
            } else {
                C -= tagValue // C에서 Tag 값을 뺌
            }
            Log.d("C_Value", "$C") // C 값만 출력
        }


        // 모든 체크박스에 리스너 등록
        purpose_box.forEach { it.setOnCheckedChangeListener(checkBoxListener) }
        career_box.forEach { it.setOnCheckedChangeListener(checkBoxListener) }
        timeout_box.forEach { it.setOnCheckedChangeListener(checkBoxListener) }

        val result: Button = findViewById(R.id.result)
        val resultText: TextView = findViewById(R.id.resultText)

        val nowButton: Button = findViewById(R.id.now) // 현재위치 버튼
        val whatButton: Button = findViewById(R.id.what) // 준비물 버튼

        // 처음에 숨김 처리
        nowButton.visibility = View.GONE
        whatButton.visibility = View.GONE

        var isFiltered = false

        result.setOnClickListener {
            if(!isFiltered) {
                // C 값으로 추천 코스 필터링
                val fC = hikingCourses.find { it.number == C }
                if (fC != null) {
                    resultText.text = "추천 코스: ${fC.name}"
                    fC_time = fC.time
                    recommendedCourse = fC // 추천된 코스 저장
                } else {
                    resultText.text = "추천할 코스가 없습니다."
                    fC_time = 0f
                    recommendedCourse = null // 추천 코스가 없으므로 null로 설정
                }
                result.text = "지도 보기"

                // 버튼 보이기
                nowButton.visibility = View.VISIBLE
                whatButton.visibility = View.VISIBLE

                isFiltered = true
            }
            else{
                if (recommendedCourse != null) {
                    val intent = Intent(this, SubActivity::class.java)
                    intent.putExtra("course_number", recommendedCourse!!.index) // index 값 전달
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "추천된 코스가 없습니다.", Toast.LENGTH_SHORT).show()
                    result.text = "추천 다시 보기"
                    isFiltered = false
                }


            }


        }



        whatButton.setOnClickListener {
            if (recommendedCourse != null && userWeight != null) {
                val courseTime = recommendedCourse!!.time // 추천 코스의 시간
                val courseMET = recommendedCourse!!.MET   // 추천 코스의 MET
                val Cal = userWeight!! * courseTime * courseMET // 칼로리 계산

                // 물 섭취량 계산 (1시간당 0.7L 기준)
                val waterIntake = 0.7 * courseTime // 시간에 비례한 물 섭취량
                // 소수점 첫째 자리까지 포맷팅
                val formattedCal = String.format("%.1f", Cal)

                // 물 섭취량에 따른 생수 병 개수 계산
                val bottleInfo = when {
                    waterIntake > 0 && waterIntake <= 0.5 -> "500mL 1병"
                    waterIntake > 0.5 && waterIntake <= 1.0 -> "500mL 1병"
                    waterIntake > 1.0 && waterIntake <= 1.5 -> "500mL 3병"
                    else -> "500mL 4병"
                }

                // AlertDialog로 결과 보여주기
                val builder = AlertDialog.Builder(this)
                builder.setTitle("결과")
                builder.setMessage("필요 칼로리 소모량: $formattedCal kcal\n필요 물 섭취량: $bottleInfo")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss() // 팝업창 닫기
                }
                val dialog = builder.create()
                dialog.show()
            } else {

                // AlertDialog로 에러 메시지 보여주기
                val builder = AlertDialog.Builder(this)
                builder.setTitle("오류")
                builder.setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss() // 팝업창 닫기
                }
                val dialog = builder.create()
                dialog.show()
            }
        }


    }


    @SuppressLint("MissingInflatedId")
    fun login(){
        setContentView(R.layout.login)

        // EditText 연결
        val ageInput: EditText = findViewById(R.id.age)
        val heightInput: EditText = findViewById(R.id.height)
        val weightInput: EditText = findViewById(R.id.weight)

        val Next: Button = findViewById(R.id.Next)
        Next.setOnClickListener{
            val age = ageInput.text.toString()
            val height = heightInput.text.toString()
            val weight = weightInput.text.toString()

            // 값 저장 (Int로 변환)
            userAge = age.toIntOrNull()
            userHeight = height.toIntOrNull()
            userWeight = weight.toIntOrNull()

            course()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btn_information: Button = findViewById(R.id.btn_information)
        btn_information.setOnClickListener{
            login()
        }


    }


}
