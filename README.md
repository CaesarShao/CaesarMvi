# CaesarMvi
## Mvi架构模式开发

前言：随着谷歌推广各种设计模式，慢慢地我们好像将其当成了一种新技术来使用，觉得在新的项目必须要用上新的设计模式，就能使应用变得更加流畅和稳定。设计模式只是一种手段，不管是Mvi模式还是Mvc模式，都是来规矩我们的运行逻辑。
没有最好的设计模式，只有更适合项目业务逻辑的设计思维。

不过新推出的我们肯定要去试试了解。

<img src="/Users/caesar/Documents/mvi图片.png" alt="mvi图片" style="zoom:60%;" />

Mvi模式采用单向流动的设计思路，用户意图Intent通过View层传输到Model，数据处理完毕之后，再返回状态State到View层显示,由此一个循环结束.一个界面的所有东西,都是不断循环在一个闭环内,所以数据的流向都能方便的监测到,哪一环出现问题就能快速的定位到问题所在.

接下来我们来看一下实际应用

举例有一个简单的新闻界面,用户去看新闻,这个界面用户能看到的,是不是就是新闻的内容,还有加载弹窗的显示与消失,我们定义一个
NewState,里面定义几个接口,开始网络请求,网络请求结束,数据返回,失败信息返回.

```kotlin
sealed class NewState {
    object loadingNews : NewState()
    object loadingFinish : NewState()
    data class successReturn(val str: String) : NewState()
    data class failReturn(val str: String) : NewState()
}
```

然后我们再写一个用户的意图,也就是用户用手实际点击触摸的意图NewIntent,看新闻那就是触摸刷新.

```kotlin
sealed class NewIntent {
    object touchLoad: NewIntent()
}
```

我们再新建一个viewModel类：

```kotlin
class NewViewModel() : ViewModel() {
    val userIntent = Channel<NewIntent>()
    val state = MutableStateFlow<NewState>(NewState.loadingFinish)
    init {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is NewIntent.touchLoad -> {
                        loadNet()
                    }
                }
            }
        }
    }
    private fun loadNet() {
        state.value = NewState.loadingNews
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            val data = Random.nextInt(4)
            state.value = if (data == 0) {
                NewState.failReturn("网络请求失败,错误数据显示:"+data)
            } else {
                NewState.successReturn("网络请求成功,数据返回:"+data)
            }
            delay(100)//这边因为MutableStateFlow的特性加个延迟
            state.value = NewState.loadingFinish
        }
    }
}
```

其中Channel和MutableStateFlow，这两个对象可以用liveDate代替，只是一种传输工具。在viewmodel中，我们监听了用户的意图userIntent,当收到touch事件时，就去请求网络，然后将结果通过state返回给界面显示。最后，我们看一下首页的核心代码。

```kotlin
srlShow?.setOnRefreshListener {
            touchLoad()
        }
        viewModel?.viewModelScope?.launch {
            viewModel?.state?.collect {
                when (it) {
                    is NewState.loadingNews -> {
                        srlShow?.isRefreshing = true
                        tvShow?.text = "网络请求中.."
                    }
                    is NewState.loadingFinish -> {
                        srlShow?.isRefreshing = false
                    }
                    is NewState.successReturn -> {
                        tvShow?.text = it.str
                    }
                    is NewState.failReturn -> {
                        tvShow?.text = it.str
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun touchLoad() {
        viewModel?.viewModelScope?.launch {
            viewModel?.userIntent?.send(NewIntent.touchLoad)
        }
    }
```

获取viewmodel中的userIntent意图，监听state状态返回，根据不同的结果，显示不同的UI，因为所有的意图都会转换成state从一处返回，我们就可以统一管理，出现异常的话，也容易追踪排查。

当然，实际应用不会像demo那样简单，我们经常要监听整个app的消息，获取其他地方的消息，一个界面中，可能要监听多个state，意味着有多个闭环存在，这种情况应该怎么处理，这边给出一个简单地解决方案.

```kotlin
sealed class GlobalIntent {
    object touchLoad: GlobalIntent()
}
```

```kotlin
sealed class GlobalState {
    object loadingNews : GlobalState()
    object loadingFinish : GlobalState()
    data class successReturn(val str: String) : GlobalState()
    data class failReturn(val str: String) : GlobalState()
}
```

```kotlin
object GlobalReg {
    val userIntent = Channel<GlobalIntent>()
    val state = MutableStateFlow<GlobalState>(GlobalState.loadingFinish)
}
```

```kotlin
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            GlobalReg.userIntent.consumeAsFlow().collect {
                when (it) {
                    is GlobalIntent.touchLoad -> {
                        GlobalReg.state.value = GlobalState.loadingNews
                        delay(4000)
                        GlobalReg.state.value = GlobalState.successReturn("应用项目中返回的数据")
                        delay(100)
                        GlobalReg.state.value = GlobalState.loadingFinish
                    }
                }
            }
        }
    }
}
```

创建2个全局的意图和状态，其处理跟普通的界面的处理方式一样，然后我们有2个界面，都订阅了全局的state。

```kotlin
class MainActivity : AppCompatActivity() {
GlobalScope.launch {
            GlobalReg.state.collect {
                when (it) {
                    is GlobalState.loadingNews -> {
                        Log.i("caesar","MainActivity中接收到了loadingNews")
                    }
                    is GlobalState.loadingFinish -> {
                        Log.i("caesar","MainActivity中接收到了loadingFinish")
                    }
                    is GlobalState.successReturn -> {
                        Log.i("caesar","MainActivity中接收到了successReturn:"+it.str)
                    }
                    is GlobalState.failReturn -> {
                        Log.i("caesar","MainActivity中接收到了failReturn")
                    }
                    else -> {

                    }
                }
            }
        }
}
```

```kotlin
class GlobalActivity : AppCompatActivity() {
    var viewModel: GlobalViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global)
        viewModel = ViewModelProvider(this).get(GlobalViewModel::class.java)
        findViewById<Button>(R.id.btn_show).setOnClickListener {
            touchLoad()
        }
        viewModel?.viewModelScope?.launch {
            GlobalReg.state.collect {
                when (it) {
                    is GlobalState.loadingNews -> {
                        Log.i("caesar","GlobalActivity中接收到了loadingNews")
                    }
                    is GlobalState.loadingFinish -> {
                        Log.i("caesar","GlobalActivity中接收到了loadingFinish")
                    }
                    is GlobalState.successReturn -> {
                        Log.i("caesar","GlobalActivity中接收到了successReturn:"+it.str)
                    }
                    is GlobalState.failReturn -> {
                        Log.i("caesar","GlobalActivity中接收到了failReturn")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun touchLoad() {
        viewModel?.viewModelScope?.launch {
            GlobalReg.userIntent.send(GlobalIntent.touchLoad)
        }
    }
}
```

```
I/caesar: MainActivity中接收到了loadingNews
I/caesar: GlobalActivity中接收到了loadingNews
I/caesar: MainActivity中接收到了successReturn:应用项目中返回的数据
I/caesar: GlobalActivity中接收到了successReturn:应用项目中返回的数据
I/caesar: MainActivity中接收到了loadingFinish
I/caesar: GlobalActivity中接收到了loadingFinish
```

我们在其他的地方，就可以获取到全局的数据了.


