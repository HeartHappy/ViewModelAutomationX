package com.hearthappy.vma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hearthappy.viewmodelautomation.R
import com.hearthappy.viewmodelautomation.databinding.ActivityMainBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.datastore.UserInfoKeys
import com.hearthappy.vma.generate.datastore.userInfoDataStore
import com.hearthappy.vma.generate.viewmodel.MainTestViewModel
import com.hearthappy.vma.model.read
import com.hearthappy.vma.model.readMultiple
import com.hearthappy.vma.model.request.LoginBody
import com.hearthappy.vma_ktx.factory.vma
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import com.hearthappy.vma_ktx.network.asFailedMessage
import com.hearthappy.vma_ktx.network.asThrowableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainTestViewModel by vma { RetrofitManage.apiService }


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
        lifecycleScope.launch {
            viewModel.loginStateFlow
//                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {
                    when (it) {
                        is FlowResult.Default    -> {}
                        is FlowResult.Failed     -> showMessage(it.asFailedMessage())

                        is FlowResult.Loading    -> showMessage("加载")

                        is FlowResult.Succeed<*> -> showMessage(it.body.toString())

                        is FlowResult.Throwable  -> showMessage(it.asThrowableMessage())
                    }
                }
        }

        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.sfImages.collect {
                when (it) {
                    is FlowResult.Default    -> {}
                    is FlowResult.Failed     -> showMessage(it.asFailedMessage())

                    is FlowResult.Loading    -> showMessage("加载")

                    is FlowResult.Succeed<*> -> showMessage(it.body.toString())

                    is FlowResult.Throwable  -> showMessage(it.asThrowableMessage())
                }
            }
//            }

        }

        viewModel.getTokenLiveData.observe(this@MainActivity) {
            it?.let {
                when (it) {
                    is Result.Failed    -> {}
                    is Result.Succeed   -> {}
                    is Result.Throwable -> {}
                }
            }
        }

        viewBinding.apply {
            btnLogin.setOnClickListener {
                viewModel.login(LoginBody("1151087058@qq.com", "123456"))
            }

            btnGetImages.setOnClickListener {
                viewModel.getImages(1, 10)
            }
            btnGetStorageData.setOnClickListener {
                lifecycleScope.launch {
                    val token = userInfoDataStore.read(UserInfoKeys.TOKEN)
                    userInfoDataStore.readMultiple(UserInfoKeys.TOKEN) {
                        withContext(Dispatchers.Main) {
                            it.forEach { _ ->
                                showMessage("token:${token}")
                            }
                        }
                    }

                }
            }
        }

        //获取dataStore数据

//        viewModel.getToken("2d173b7b44b0e3a798b38d29c3d6b18f8", "M2012K11AC")
    }

    fun showMessage(message: String?) {
        viewBinding.tvResult.text = message
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}