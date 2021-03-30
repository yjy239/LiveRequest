# LiveRequest
基于Retrofit和LiveData设计的数据响应式网络请求。

Compose UI 逐渐兴起全新的Android绘制编程。将会使得Android开发，更加注重ui的组合。而数据方面将会向着React方式，数据主动发起信号，让ui响应式渲染画面。

那么MMVM方式将会成为主流，而Retrofit 方式无法满足MVVM方式请求。


因此我重构了Retrofit的CallAdapter，使得Retrofit融合liveData做到，网络数据来了，自动回流给Livedata的监听。UI只需要关注暴露出来的LiveData对象即可。


# 如何使用：

- 1.首先 创建一个Retrofit ，并设置一个`LiveRequestCallAdapterFactory`对象：

```
new Retrofit.Builder()
                .baseUrl(getBaseUrl(serviceClass))
                .addConverterFactory(CustomConvertFactory.create())
                //非标准的HttpResult结构采用普通的gson处理
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(new LiveRequestCallAdapterFactory())
                .client(client)
                .build();
```

- 2.创建如下Retrofit格式请求：

```
fun getNetata(): LiveRequest<HttpResultNew<Netata>>
```


- 3. 构建一个请求监听：
```kotlin
private val model: LiveModel<ApiService> = LiveModel.Builder().create(TotalViewModelStoreOwner,
            ApiService::class.java)
            
val virtualAddress = model.apiService.getNetata()
            
virtualAddress?.observe(fragment, Observer {
        })
```

注意LiveModel中`create`方法设置是ViewModel监听的生命周期。当监听的生命周期销毁，对应LiveModel下所有的LiveRequest将会取消行为，并不会返回数据回流操作


- 4.发出请求：

```kotlin
virtualAddress?.request()
```

4个步骤即可完成网络请求监听。

有了Retrofit和LiveData的桥梁，就能通过Compose UI的LiveData和State关联起来。
