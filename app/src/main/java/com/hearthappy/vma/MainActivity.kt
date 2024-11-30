package com.hearthappy.vma

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import com.hearthappy.viewmodelautomationx.R
import com.hearthappy.viewmodelautomationx.databinding.ActivityMainBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.datastore.UserInfoKeys
import com.hearthappy.vma.generate.datastore.userInfoDataStore
//import com.hearthappy.vma.generate.viewmodel.MainViewModel
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

//    private val viewModel: MainViewModel by vma { RetrofitManage.apiService }

    private val fragments= listOf(MainFragment(),MainFragment())

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

            viewPager.adapter=object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getCount(): Int {
                    return fragments.size
                }

                override fun getItem(position: Int): Fragment {
                    return fragments[position]
                }

                override fun getPageTitle(position: Int): CharSequence {
                    return ""
                }
            }
            lifecycleScope.launch {
                Log.d(TAG, "onCreate: ")
                RetrofitManage.apiService.getImage(1,10)
            }
//            initListener()
        }


//        initViewModelListener()



        //获取dataStore数据

//        viewModel.getToken("2d173b7b44b0e3a798b38d29c3d6b18f8", "M2012K11AC")
    }

    /*private fun ActivityMainBinding.initListener() {
            btnLogin.setOnClickListener {
                viewModel.login(LoginBody("1151087058@qq.com", "123456"))
            }

            btnGetImages.setOnClickListener {
                viewModel.getImages(1, 10)
            }
            btnGetStorageData.setOnClickListener {
                lifecycleScope.launch {
    //                    val token = userInfoDataStore.read(UserInfoKeys.TOKEN)
                    userInfoDataStore.readMultiple(UserInfoKeys.TOKEN) {
                        withContext(Dispatchers.Main) {
                            it.forEach { f ->
                                showMessage("token:${f}")
                            }
                        }
                    }
                }
            }
    }

    private fun initViewModelListener() {
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
    }*/

    private fun showMessage(message: String?) {
        viewBinding.tvResult.text = message
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}