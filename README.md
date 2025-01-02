## 自动生成ViewModel层+Retrofit网络请求+DataStore存储

#### 一、项目地址：[Github](https://github.com/HeartHappy/ViewModelAutomationX)

- 框架初衷：项目中存在大量接口，反复的接口调用以及ViewModel层的数据绑定。写多了，感觉这些代码也是冗余代码。于是产生了生成ViewModel层以及网络请求的想法。

- 框架优势：通过提供的注解自动生成ViewModel层的LiveData、StateFlow，以及网络请求。节省您大量的开发时间

- 采用KSP在编译时的优势：
  - KAPT：由于要生成临时 Java 文件、依赖 Java 注解处理框架，整体处理流程相对繁琐，编译时间往往较长，尤其是在项目规模较大、注解繁多的情况下，额外的 Java 文件生成与转换会拖慢编译速度。即使又增量编译，每次编译依然能看到生成文件日志，即可看出工作原理以及缺陷。
  - KSP：由于它会将符号处理文件做标记，更改后的文件，会标记为脏文件。编译构建时，只处理脏文件。能够显著缩短编译时长，提升大型项目的编译性能。

- 欢迎使用issues提建议或bug





#### 二、集成

- 如果你的项目 Gradle 配置是在 `7.0 以下`，需要在 `build.gradle` 文件中加入

  ```groovy
  allprojects {
      repositories {
          //远程仓库：https://jitpack.io
          maven { url 'https://jitpack.io' }
      }
  }
  ```

- 如果你的 Gradle 配置是 `7.0 及以上`，则需要在 `settings.gradle` 文件中加入

  ```groovy
  dependencyResolutionManagement {
      repositories {
          //远程仓库：https://jitpack.io
          maven { url 'https://jitpack.io' }
      }
  }
  ```

- project.gradle文件中加入

  ```
  plugins{
      id "com.google.devtools.ksp" version "2.0.10-1.0.24"
      id "org.jetbrains.kotlin.jvm" version "2.0.10"
  }
  ```



- 在项目 app 模块下的 build.gradle 文件中加入远程依赖和ksp插件

```groovy
plugins {
    id 'com.google.devtools.ksp'
}


dependencies {
	//注解库
    implementation 'com.github.HeartHappy.ViewModelAutomationX:annotations:1.0.7'
    //扩展库
    implementation 'com.github.HeartHappy.ViewModelAutomationX:vma-ktx:1.0.7'
    //处理注解自动生成库
    ksp 'com.github.HeartHappy.ViewModelAutomationX:processor:1.0.7'
}
```

#### 三、注解使用

- ##### 注意：一个api接口对应一个ViewModel类

- ##### 你只需要在你的Retrofit 的API接口中添加注解@ViewModelAutomation注解和@BindLiveData或@BindStateFlow（示例代码如下）

```kotlin
@ViewModelAutomation("MainViewModel")
interface ApiService {

    @BindStateFlow
    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): ResLogin

    @BindStateFlow("getImages", "sfImages")
    @GET("getImages")
    suspend fun getImage(@Query("page") page: Int, @Query("size") size: Int): ResImages


    @BindLiveData
    @GET("sentences")
    suspend fun getSentences(): BaseData<ResSentences>
}
```

#### 四、生成ViewModel文件

##### 1、生成方式

- ##### （推荐）使用命令生成：

```groovy
./gradlew kspDebugKotlin   

或   

./gradlew kspReleaseKotlin
```

- ##### Android Studio——>Build——>Make Project

##### 2、生成日志

- ##### 如下图说明完成

![_20250102160141.png](https://s2.loli.net/2025/01/02/CctTHf6IEdpoM8B.png)

##### 3、生成目录

```
app/build/generated/ksp/*
```



#### 五：使用

##### 1、在你的Activity或Fragment中创建ViewModel

```kotlin
//RetrofitManage.apiService：是Retrofit的api代理接口
private val viewModel: MainViewModel by vma(RetrofitManage.apiService)
```

##### 2、Fragment共享Activity中的ViewModel实例

```kotlin
private val viewModel by vmaFromActivity<MainViewModel>()
```

##### 3、监听

- StateFlow监听

```
//StateFlow监听
lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
        viewModel.sfImages.collect {
            when (it) {
                is FlowResult.Default -> {}
                is FlowResult.Failed -> showMessage(it.asFailedMessage())
                is FlowResult.Loading -> showMessage(getString(R.string.loading))
                is FlowResult.Succeed<*> -> showMessage(it.body.toString())
                is FlowResult.Throwable -> showMessage(it.asThrowableMessage())
            }
        }
    }
}
```

- LiveData监听

```
//LiveData监听
viewModel.getSentencesLiveData.observe(this@MainActivity) {
    it?.let {
        when (it) {
            is Result.Failed -> showMessage(it.asFailedMessage())
            is Result.Succeed -> showMessage(it.body.toString())
            is Result.Throwable -> showMessage(it.asThrowableMessage())
        }
    }
}
```

#### 五、响应数据存储（可选）

- ##### 数据存储采用的DataStore

- ##### 你只需要在响应类中添加@DataStore和@DataWrite("from")注解，即可实现响应数据的自动存储（示例代码如下）

- ```
  @DataStore("filename")
  data class ResSentences(
  @DataWrite("from") var from: String = "", 
  @DataWrite("name") var name: String = "")
  
  ```
#### 六、[注解简介](https://github.com/HeartHappy/ViewModelAutomationX/ANNOTATION.md)