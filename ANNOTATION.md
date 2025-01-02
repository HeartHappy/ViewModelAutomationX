##### 1、@ViewModelAutomation

|   参数    |  类型   |        描述         | 必须 | 默认值 |
|------------|--------| ------------------- | :--: | :----------------------: |
| fileName  | String | 生成ViewModel的类名 |  是  |   无   |
| enableLog | Boolean |    输出生成日志     |  否  | false  |

##### 2、@BindLiveData

| 参数           | 类型     | 描述    | 必须 | 默认值            |
|------------|--------|-------------------|:--:|:----------------------:|
| methodName   | String | 方法重命名 | 否  | Retrofit接口的函数名 |
| propertyName | String | 属性重命名 | 否  | ld+methodName  |

##### 3、@BindStateFlow

| 参数         | 类型   | 描述       | 必须 |          默认值           |
|------------| ------ | ------------------- | :--: |:----------------------:|
| methodName   | String | 方法重命名 | 否   |     Retrofit接口的函数名     |
| propertyName | String | 属性重命名 | 否   |     sf+methodName      |

##### 4、@DataStore

| 参数      | 类型    | 描述                | 必须 | 默认值 |
|------------| ------- | ------------------- | :--: | :----------------------: |
| name      | String  | 生成DataStore文件名 | 是   | 无     |
| enableLog | Boolean | 输出生成日志        | 否   | false  |

##### 5、@DataWrite

| 参数 | 类型   | 描述          | 必须 | 默认值 |
|------------| ------ |-------------------| :--: | :----------------------: |
| key  | String | 插入数据的key | 是   | 无     |

