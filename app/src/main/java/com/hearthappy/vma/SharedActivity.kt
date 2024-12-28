package com.hearthappy.vma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hearthappy.viewmodelautomationx.R
import com.hearthappy.viewmodelautomationx.databinding.ActivitySharedBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.viewmodel.MainViewModel
import com.hearthappy.vma_ktx.factory.vma
import com.hearthappy.vma_ktx.network.Result

/**
 * Created Date: 2024/12/27
 * @author ChenRui
 * ClassDescription：测试Fragment共享Activity的ViewModel实例
 */
class SharedActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySharedBinding


    private val viewModel: MainViewModel by vma(RetrofitManage.apiService)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySharedBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewBinding.initView()

        viewModel.getSentencesLiveData.observe(this@SharedActivity) {
            it?.let {
                when (it) {
                    is Result.Failed -> {}
                    is Result.Succeed -> {}
                    is Result.Throwable -> {}
                }
            }
        }
    }

    private fun ActivitySharedBinding.initView() {
        val fragments = listOf(MainFragment(), MainFragment())
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return "TAB-$position"
            }
        }
        tabs.setupWithViewPager(viewPager)
    }
}