package com.hearthappy.vma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hearthappy.viewmodelautomationx.R
import com.hearthappy.viewmodelautomationx.databinding.ActivityMainBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.viewmodel.MainViewModel
import com.hearthappy.vma_ktx.factory.vma
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import com.hearthappy.vma_ktx.network.asFailedMessage
import com.hearthappy.vma_ktx.network.asThrowableMessage
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by vma(RetrofitManage.apiService)


    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


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


        // TODO: 暂且支持具体类型，不支持泛型的检测
        //        btnGetStorageData.setOnClickListener { //获取dataStore数据
        //            lifecycleScope.launch { //                    val token = userInfoDataStore.read(UserInfoKeys.TOKEN)
        //                userInfoDataStore.readMultiple(UserInfoKeys.TOKEN) {
        //                    withContext(Dispatchers.Main) {
        //                        it.forEach { f -> showMessage("token:${f}") }
        //                    }
        //                }
        //            }
        //        }
    }

    private fun initViewModelListener() {
        lifecycleScope.launch {
            viewModel.loginStateFlow //                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {
                    when (it) {
                        is FlowResult.Default -> {}
                        is FlowResult.Failed -> showMessage(it.asFailedMessage())

                        is FlowResult.Loading -> showMessage(getString(R.string.loading))

                        is FlowResult.Succeed<*> -> showMessage(it.body.toString())

                        is FlowResult.Throwable -> showMessage(it.asThrowableMessage())
                    }
                }
        }

        lifecycleScope.launch { //            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.sfImages.collect {
                when (it) {
                    is FlowResult.Default -> {}
                    is FlowResult.Failed -> showMessage(it.asFailedMessage())

                    is FlowResult.Loading -> showMessage(getString(R.string.loading))

                    is FlowResult.Succeed<*> -> showMessage(it.body.toString())

                    is FlowResult.Throwable -> showMessage(it.asThrowableMessage())
                }
            } //            }
        }

        viewModel.getSentencesLiveData.observe(this@MainActivity) {
            it?.let {
                when (it) {
                    is Result.Failed -> showMessage(it.asFailedMessage())
                    is Result.Succeed -> showMessage(it.body.toString())
                    is Result.Throwable -> showMessage(it.asThrowableMessage())
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