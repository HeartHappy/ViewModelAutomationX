# 自动生成ViewModel层+Retrofit网络请求+DataStore存储

[![](https://jitpack.io/v/HeartHappy/ViewModelAutomationX.svg)](https://jitpack.io/#HeartHappy/ViewModelAutomationX)

### 一、项目地址：[Github](https://github.com/HeartHappy/ViewModelAutomationX)

#### 1、框架初衷：项目中存在大量接口，反复的接口调用以及ViewModel层的数据绑定。写多了，感觉这些是过于频繁的重复代码。于是产生了生成ViewModel层以及网络请求的想法。

#### 2、框架优势：通过提供的注解自动生成ViewModel层的LiveData、StateFlow，以及网络请求。节省您大量的开发时间和编译时间

#### 3、KSP优势：

   ###### 		性能优势：KSP旨在提高处理注解的速度。相比KAPT（Kotlin Annotation Processing Tool），KSP可以在更早的编译阶段工作，因此可以减少编译时间。

   ###### 		增量支持：只处理更改后的源文件，不必重新处理所有文件。能够显著缩短编译时长，提升大型项目的编译性能。

#### 4、欢迎使用issues提建议或bug

 
### 二、示例演示

<img src="https://github.com/HeartHappy/ViewModelAutomationX/blob/master/demo.gif" alt="示例演示" height="700px">



### 三、集成

#### 1、如果你的项目 Gradle 配置是在 `7.0 以下`，需要在 `build.gradle` 文件中加入

   ```groovy
   allprojects {
       repositories {
           mavenCentral()
       }
   }
   ```

#### 2、如果你的 Gradle 配置是 `7.0 及以上`，则需要在 `settings.gradle` 文件中加入

   ```groovy
   dependencyResolutionManagement {
       repositories {
          mavenCentral()
       }
   }
   ```

#### 3、在build.gradle（Project）文件中加入

   ```groovy
   plugins{
       id "com.google.devtools.ksp" version "2.0.10-1.0.24"
       id "org.jetbrains.kotlin.jvm" version "2.0.10"
   }
   ```

#### 4、在 build.gradle（app）  文件中加入远程依赖和ksp插件

```groovy
//ksp插件
plugins {
    id 'com.google.devtools.ksp'
}
android{
    //JAVA和jvm设置为11或更高
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
}

dependencies {
    //扩展库
    implementation 'io.github.hearthappy:vma-ktx:1.0.4'
    //处理注解自动生成库
    ksp 'io.github.hearthappy:vma-processor:1.0.4'
}
```

### 四、注解使用

#### 你只需要在你的Retrofit 的API接口中添加注解@ViewModelAutomation注解和@BindLiveData或@BindStateFlow（示例代码如下）

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

### 五、生成ViewModel文件

#### 1、生成方式

- ##### （推荐）使用命令生成：

```groovy
./gradlew kspDebugKotlin   
```


或

```groovy
./gradlew kspReleaseKotlin
```



- ##### Android Studio——>Build——>Make Project

#### 2、生成日志

- ##### 如下图说明完成

![_20250102160141.png](https://s2.loli.net/2025/01/02/CctTHf6IEdpoM8B.png)

#### 3、生成结果（如下图）

<a href="https://s2.loli.net/2025/01/03/EqeGo9jXaDUvzIi.png" target="_blank">
    <img src="https://s2.loli.net/2025/01/03/EqeGo9jXaDUvzIi.png" alt="生成结果" width="400px" />
</a>



#### 4、生成目录

```kotlin
app/build/generated/ksp/*
```



### 六：使用

#### 1、在你的Activity或Fragment中创建ViewModel

```kotlin
//RetrofitManage.apiService：是Retrofit的api代理接口
private val viewModel: MainViewModel by vma(RetrofitManage.apiService)
```

#### 2、Fragment共享Activity中的ViewModel实例

```kotlin
private val viewModel by vmaFromActivity<MainViewModel>()
```

#### 3、监听

##### StateFlow监听

```kotlin
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

   
##### LiveData监听

```kotlin
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



### 七、响应数据存储（可选）

#### 你只需要在响应类中添加@DataStore和@DataWrite("from")注解，即可实现响应数据的自动存储（示例代码如下）

```kotlin
@DataStore("filename")
data class ResSentences(
@DataWrite("from") 
var from: String = "", 
@DataWrite("name") 
var name: String = "")

```

#### 注意：数据存储采用的DataStore，如果你需要使用DataStore响应存储。你需要在你的项目集成build.gradle(app)

```groovy
implementation 'androidx.datastore:datastore-preferences:1.0.0'
implementation 'androidx.datastore:datastore-preferences-core:1.0.0'
```
### 八：代码混淆

```
-keep public class * extends androidx.lifecycle.AndroidViewModel
-keep class com.hearthappy.vma_ktx.factory.** { *; }
-keep class com.hearthappy.vma_ktx.network.** { *; }
-keep class com.hearthappy.vma.model.response.** {*;}
-keepattributes Signature
-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull <methods>;
}
```

### 九、[注解简介](https://github.com/HeartHappy/ViewModelAutomationX/blob/master/ANNOTATION.md)
