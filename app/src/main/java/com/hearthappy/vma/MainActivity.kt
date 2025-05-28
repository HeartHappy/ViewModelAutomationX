package com.hearthappy.vma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hearthappy.viewmodelautomationx.R
import com.hearthappy.viewmodelautomationx.databinding.ActivityMainBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.datastore.UserDataKeys
import com.hearthappy.vma.generate.datastore.userDataDataStore
import com.hearthappy.vma.generate.viewmodel.MainViewModel
import com.hearthappy.vma.model.process
import com.hearthappy.vma.model.readMultiple
import com.hearthappy.vma.model.toast
import com.hearthappy.vma_ktx.factory.vma
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by vma { RetrofitManage.apiService }


    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        viewBinding.apply {
            initListener()
            initViewModelListener()
            Log.d(TAG, "onCreate: ${System.identityHashCode(viewModel)}")
        }
    }


    private fun ActivityMainBinding.initListener() {
        btnGoSharedPage.setOnClickListener { startActivity(Intent(this@MainActivity, SharedActivity::class.java)) }

        btnQuotes.setOnClickListener {
            viewModel.getSentences()
        }

        btnGetImages.setOnClickListener { viewModel.getImages(1, 10) }


        btnGetStorageData.setOnClickListener { //获取dataStore数据
            lifecycleScope.launch {
                userDataDataStore.readMultiple(UserDataKeys.NAME) {
                    withContext(Dispatchers.Main) {
                        it.forEach { f -> showMessage("name:${f}") }
                    }
                }
            }
        }
    }

    private fun initViewModelListener() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sfImages.collect {
                    when (it) {
                        is FlowResult.Default -> {}
                        is FlowResult.Loading -> showMessage(getString(R.string.loading))
                        is FlowResult.Succeed -> it.process { showMessage(toString()) }
                        is FlowResult.Throwable -> it.toast()
                    }
                }
            }
        }
        viewModel.ldGetSentences.observe(this@MainActivity) {
            it?.let {
                when (it) {
                    is Result.Succeed -> it.process { showMessage(this.toString()) }
                    is Result.Throwable -> it.toast()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.sfLogin.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).collect {
                when (it) {
                    is FlowResult.Default -> Unit
                    is FlowResult.Loading -> showMessage(getString(R.string.loading))
                    is FlowResult.Succeed -> it.process { showMessage(toString()) }
                    is FlowResult.Throwable -> it.toast()
                }
            }
        }
    }

    private fun showMessage(message: String?) {
        viewBinding.tvResult.text = message
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}