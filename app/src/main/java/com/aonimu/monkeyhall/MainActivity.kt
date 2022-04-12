package com.aonimu.monkeyhall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //変数の宣言
    private val CHOICE_STATE = "CHOICE_STATE"
    private val CHOICE_NUM = "CHOICE_NUM"
    private val OPEN_NUM = "OPEN_NUM"
    private val CHALLENGE_COUNT = "CHALLENGE_COUNT"
    private val BANANA_COUNT = "BANANA_COUNT"
    private val SUCCESS_RATE = "SUCCESS_RATE"
    // CHOICE1は「3つの箱から選ぶ」状態を表す
    private val CHOICE1 = "choice1"
    // CHOICE2は「2つの箱から選ぶ」状態を表す
    private val CHOICE2 = "choice2"
    // RESTARTは最終結果の状態を表す
    private val RESTART = "restart"
    // デフォルトの状態
    private var choiceState = "choice1"
    private var choiceNum = -1
    private var openNum = -1
    // デフォルトの箱の中身を0(はずれ)にする
    private var boxContent = mutableListOf<Int>(0,0,0)
    // デフォルトの挑戦結果
    private var challengeCount = 0
    private var bananaCount = 0
    private var successRate = 0

    private var boxImage1: ImageView? = null
    private var boxImage2: ImageView? = null
    private var boxImage3: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //
        //savedInstanceState(アプリの状態を管理するためのオブジェクト)
        if (savedInstanceState != null) {
            choiceState = savedInstanceState.getString(CHOICE_STATE, CHOICE1)
            choiceNum = savedInstanceState.getInt(CHOICE_NUM, -1)
            openNum = savedInstanceState.getInt(OPEN_NUM, -1)
            challengeCount = savedInstanceState.getInt(CHALLENGE_COUNT, 0)
            bananaCount = savedInstanceState.getInt(BANANA_COUNT, 0)
            successRate = savedInstanceState.getInt(SUCCESS_RATE, 0)
        }


        setViewElements()
        boxImage1 = findViewById(R.id.imageBox1)
        boxImage2 = findViewById(R.id.imageBox2)
        boxImage3 = findViewById(R.id.imageBox3)
        boxImage1!!.setOnClickListener {
            choiceNum = 0
            clickBoxImage()
        }
        boxImage2!!.setOnClickListener {
            choiceNum = 1
            clickBoxImage()
        }
        boxImage3!!.setOnClickListener {
            choiceNum = 2
            clickBoxImage()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CHOICE_STATE, choiceState)
        outState.putInt(CHOICE_NUM, choiceNum)
        outState.putInt(OPEN_NUM, openNum)
        outState.putInt(CHALLENGE_COUNT, challengeCount)
        outState.putInt(BANANA_COUNT, bananaCount)
        outState.putInt(SUCCESS_RATE, successRate)

        super.onSaveInstanceState(outState)
    }

    private fun setViewElements() {
        val textAction: TextView = findViewById(R.id.text_action)
        val box1Image: ImageView = findViewById(R.id.imageBox1)
        val box2Image: ImageView = findViewById(R.id.imageBox2)
        val box3Image: ImageView = findViewById(R.id.imageBox3)
        val monkeyImage: ImageView = findViewById(R.id.imageMonkey)
        var drawableResource_box1 = R.drawable.box
        var drawableResource_box2 = R.drawable.box
        var drawableResource_box3 = R.drawable.box
        val drawableResource_monkey:Int
        val textResource_action:Int

        when(choiceState){
            CHOICE1 -> {
                val textResult: TextView = findViewById(R.id.text_result)
                textResult.text = getString(R.string.banana_result, challengeCount, bananaCount, successRate)
                textResource_action = R.string.monkey_choice1
                drawableResource_monkey = R.drawable.monkey2
            }
            CHOICE2 -> {
                textResource_action = R.string.monkey_choice2
                drawableResource_monkey = R.drawable.monkey2
                //選んだ箱によって条件分岐
                when(openNum){
                    0 -> drawableResource_box1 = R.drawable.greenpepper
                    1 -> drawableResource_box2 = R.drawable.greenpepper
                    else -> drawableResource_box3 = R.drawable.greenpepper
                }
            }
            else -> {
                //選んだ箱によって条件分岐
                if(choiceNum == boxContent.indexOf(1)){
                    textResource_action = R.string.monkey_success
                    drawableResource_monkey = R.drawable.monkey3
                    //2回目の選択と当たりの箱が一致した場合、当たりの箱をオープン
                    when(openNum){
                        0 -> drawableResource_box1 = R.drawable.greenpepper
                        1 -> drawableResource_box2 = R.drawable.greenpepper
                        else -> drawableResource_box3 = R.drawable.greenpepper
                    }
                    when(choiceNum){
                        0 -> drawableResource_box1 = R.drawable.banana
                        1 -> drawableResource_box2 = R.drawable.banana
                        else -> drawableResource_box3 = R.drawable.banana
                    }
                    bananaCount++
                }else{
                    textResource_action = R.string.monkey_failure
                    drawableResource_monkey = R.drawable.monkey1
                    //2回目の選択と当たりの箱が一致しない場合、ハズレをオープン
                    when(openNum){
                        0 -> drawableResource_box1 = R.drawable.greenpepper
                        1 -> drawableResource_box2 = R.drawable.greenpepper
                        else -> drawableResource_box3 = R.drawable.greenpepper
                    }
                    when(choiceNum){
                        0 -> drawableResource_box1 = R.drawable.greenpepper
                        1 -> drawableResource_box2 = R.drawable.greenpepper
                        else -> drawableResource_box3 = R.drawable.greenpepper
                    }
                }
                boxContent = mutableListOf<Int>(0,0,0)
                challengeCount++
                successRate = ((bananaCount.toDouble()/challengeCount.toDouble())*100).toInt()
            }
        }
        textAction.setText(textResource_action)
        box1Image.setImageResource(drawableResource_box1)
        box2Image.setImageResource(drawableResource_box2)
        box3Image.setImageResource(drawableResource_box3)
        monkeyImage.setImageResource(drawableResource_monkey)
    }

    private fun clickBoxImage() {
        when(choiceState){
            CHOICE1 -> {
                choiceState = CHOICE2
                boxContent = make(boxContent)
                var hazure = -1
                if(choiceNum == boxContent.indexOf(1)){
                    //1回目の選択と当たりの箱が一致した場合、ハズレのうちどちらかをオープン
                    do{
                        hazure = (0..2).random()
                    }while(choiceNum == hazure)
                    boxContent[hazure] = 2
                    openNum = hazure

                }else{
                    //1回目の選択と当たりの箱が一致しない場合、ハズレをオープン
                    boxContent[choiceNum] = 2
                    openNum = boxContent.indexOf(0)
                }
            }
            CHOICE2 -> {
                if(choiceNum != openNum) choiceState = RESTART
            }
            else -> {
                choiceState = CHOICE1
            }
        }
        setViewElements()
    }

    private fun make(boxContent: MutableList<Int>): MutableList<Int>{
        boxContent[(0..2).random()] = 1
        return boxContent
    }
}



