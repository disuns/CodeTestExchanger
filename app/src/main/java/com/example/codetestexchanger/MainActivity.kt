package com.example.codetestexchanger

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.codetestexchanger.databinding.ActivityMainBinding
import com.example.codetestexchanger.dataclass.RestData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch{
            restCall()
        }
        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        binding.tvCheckTime.text = resources.getString(R.string.checkTime,dateFormat.format(date))
    }

    private fun setUI(restData: RestData){
        with(binding){
            curCountry(0,restData.quotes.USDKRW)
            spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(adpaterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(position){
                        0->{curCountry(0,restData.quotes.USDKRW) }
                        1->{curCountry(1,restData.quotes.USDJPY)}
                        else->{curCountry(2,restData.quotes.USDPHP)}
                    }
                }

                override fun onNothingSelected(adpaterView: AdapterView<*>?) {
                }
            }
        }
    }

    private fun curCountry(position : Int, curQuotes : Double){
        val receptCountry = resources.getStringArray(R.array.receptCountry)
        val receptUnit = resources.getStringArray(R.array.receptUnit)

        with(binding){
            tvReception.text = resources.getString(R.string.reception,receptCountry[position])
            val quotesFormat = DecimalFormat("#,###.##")
            tvExchange.text = resources.getString(R.string.exchange,quotesFormat.format(curQuotes),receptUnit[position])

            resultSet(quotesFormat, curQuotes, receptUnit[position])
            editTextUSD.addTextChangedListener {
                resultSet(quotesFormat, curQuotes, receptUnit[position])
            }

        }

    }

    private fun resultSet(quotesFormat : DecimalFormat, curQuotes : Double, receptUnit : String) {
        with(binding) {
            val editText = editTextUSD.text.toString()
            when (editText.isNotEmpty() && curQuotes * editText.toInt() in 0.0..10000.0) {
                true -> {tvVisibility(true)
                    tvResult.text = resources.getString(
                        R.string.result,
                        quotesFormat.format(curQuotes * editText.toInt()),
                        receptUnit
                    )
                }
                else -> {tvVisibility(false)
                    tvError.text = resources.getString(R.string.error)
                }

            }
        }
    }

    private fun tvVisibility(check : Boolean){
        with(binding){
            when(check){
                true->{ tvError.visibility = View.INVISIBLE
                    tvResult.visibility = View.VISIBLE
                }
                else->{ tvError.visibility = View.VISIBLE
                    tvResult.visibility = View.INVISIBLE}
            }
        }
    }

    private suspend fun restCall(){
        val restService = RetrofitOkHttpManager.restService

        val response = restService.requestRest(KEY,"USD",REST_CURRENCIES)

        with(response){
            when(isSuccessful){
                true->{
                    Log.e("",raw().request.url.toString())
                    runOnUiThread { setUI(body() as RestData) }
                }
                else->{}
            }
        }
    }
}