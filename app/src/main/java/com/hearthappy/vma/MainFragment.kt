package com.hearthappy.vma

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hearthappy.viewmodelautomationx.databinding.FragmentMainBinding
import com.hearthappy.vma.api.RetrofitManage
import com.hearthappy.vma.generate.viewmodel.MainViewModel
import com.hearthappy.vma_ktx.factory.vma
import com.hearthappy.vma_ktx.factory.vmaFromActivity
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import com.hearthappy.vma_ktx.network.asFailedMessage
import com.hearthappy.vma_ktx.network.asThrowableMessage
import kotlinx.coroutines.launch


/**
 * @Author ChenRui
 * @Email  1096885636@qq.com
 * @Date  2024/11/7 17:37
 * @description Fragment使用示例
 */
class MainFragment : Fragment() {
    private val viewModel: MainViewModel by vma(RetrofitManage.apiService)
    private val viewModel2 by vmaFromActivity<MainViewModel>()

    //implementation libs.androidx.fragment.ktx
    //    private val viewModel3: MainViewModel by activityViewModels<MainViewModel>()


    private lateinit var viewBinding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentMainBinding.inflate(LayoutInflater.from(context), container, false)
        return viewBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelListener()
        viewModel2.getImages(1, 10)
        viewBinding.apply {
            Log.d(TAG, "onCreate 内存地址: 1：${System.identityHashCode(viewModel)}，2：${System.identityHashCode(viewModel2)}")
            tvResult.text = String.format("创建内存地址:%s\n共享内存地址:%s", System.identityHashCode(viewModel), System.identityHashCode(viewModel2))
        }
    }

    private fun initViewModelListener() {
        lifecycleScope.launch {
            viewModel.sfLogin //                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {
                    when (it) {
                        is FlowResult.Default -> {}
                        is FlowResult.Failed -> showMessage(it.asFailedMessage())

                        is FlowResult.Loading -> showMessage("加载")

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

                    is FlowResult.Loading -> showMessage("加载")

                    is FlowResult.Succeed<*> -> showMessage(it.body.toString())

                    is FlowResult.Throwable -> showMessage(it.asThrowableMessage())
                }
            } //            }
        }

        viewModel.ldGetSentences.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Failed -> {}
                    is Result.Succeed -> {}
                    is Result.Throwable -> {}
                }
            }
        }
    }

    private fun showMessage(message: String?) {
        viewBinding.tvResult.text = message
    }

    companion object {
        private const val TAG = "MainFragment"
    }

}