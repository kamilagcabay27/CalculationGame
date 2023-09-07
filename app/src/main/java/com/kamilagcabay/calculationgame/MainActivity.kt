package com.kamilagcabay.calculationgame

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.kamilagcabay.calculationgame.databinding.ActivityMainBinding
import com.kamilagcabay.calculationgame.databinding.DialogResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    //binding
    private lateinit var binding: ActivityMainBinding
    private var isPlayed = false
    private var firstRandomNumber: Int = 0
    private var secondRandomNumber: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init Views
        binding.apply {
            btnStartOrNext.setOnClickListener {

                if (isPlayed) {
                    getRandomNumber()
                    tvScore.text = (tvScore.text.toString().toInt() -1).toString()
                } else  {
                    isPlayed = true
                    btnStartOrNext.text= "Next!"
                    cardQuestion.visibility = View.VISIBLE
                    cardScore.visibility = View.VISIBLE
                    getRandomNumber()
                    runTimer()
                    etAnswer.setText("")
                }


            }
            etAnswer.addTextChangedListener {
                val answer = firstRandomNumber!! + secondRandomNumber!!
                val answer2 = firstRandomNumber!! - secondRandomNumber!!
                if (!it.isNullOrEmpty() && it.toString().toInt() == answer ||
                    !it.isNullOrEmpty()&& it.toString().toInt() == answer2
                    ) {
                    //cevap doğruysa
                    tvScore.text = (tvScore.text.toString().toInt() +1).toString()
                    etAnswer.setText("")
                    getRandomNumber()
                }
            }
        }
    }

    private fun runTimer() {
        lifecycleScope.launch(Dispatchers.IO) {
            (1 .. 29).asFlow().onStart {
                binding.constraintLayout.transitionToEnd()


            }.onCompletion {
                //oyun bitti. kullanıcıya alert dialog göster

                runOnUiThread {
                    binding.cardQuestion.visibility = View.GONE
                    val dialogBinding= DialogResultBinding.inflate(layoutInflater)
                    val dialog = Dialog(this@MainActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(dialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.show()
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    //Tıklama
                    dialogBinding.apply {
                            //Show data in dialog
                        tvDialogScore.text = binding.tvScore.text
                        btnClose.setOnClickListener {
                            dialog.dismiss()
                            finish()
                        }
                        btnTryAgain.setOnClickListener {
                            dialog.dismiss()
                            binding.apply {
                                btnStartOrNext.text= getString(R.string.start_game)
                                cardQuestion.visibility = View.GONE
                                cardScore.visibility=  View.GONE
                                isPlayed= false
                                constraintLayout.setTransition(R.id.start, R.id.end)
                                constraintLayout.transitionToEnd()
                                tvScore.text = "0"
                            }
                        }
                    }
                }
            }.collect{delay(2000)}
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getRandomNumber(){

        val isAddition = Random.nextBoolean() // Generate a random boolean (true or false)

        firstRandomNumber = Random.nextInt(50, 99)
        secondRandomNumber = Random.nextInt(2, 50)

        binding.tvQuestionNumber.text = if (isAddition) {
            "$firstRandomNumber + $secondRandomNumber"
        } else {
            "$firstRandomNumber - $secondRandomNumber"
        }

    }


}