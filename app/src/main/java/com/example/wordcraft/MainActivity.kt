package com.example.wordcraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordcraft.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    lateinit var adapter: MeaningAdapter

    companion object {
        private const val INTERNET_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBtn.setOnClickListener{
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerview.adapter = adapter

    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        GlobalScope.launch {

            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null){
                    throw (Exception())
                }
                runOnUiThread{
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }

            }catch (e : Exception){

                runOnUiThread{
                    setInProgress(false)
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_LONG).show()
                }

            }

        }
    }

    private fun setUI(response: WordResult){
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text= response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress : Boolean){
        if(inProgress){
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}