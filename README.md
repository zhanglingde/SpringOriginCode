Spring 源码学习

Spring 版本：5.2.9
gradle 目录：D:\ProgramFiles\gradle-5.6.4\.gradle



### @RequestBody 参数转换

添加依赖
```gradle
// @RequestBody 请求参数解析转换成对象需要使用 json 工具，这些依赖版本和 Spring 中使用 jackson 版本有关
implementation group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.9.0'
implementation group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.9.0'
implementation group: 'com.fasterxml.jackson.core', name: 'jackson-annotations', version: '2.9.0'
```
添加依赖后会注入 MappingJackson2MessageConverter 转换器，转换 application/json 格式的请求参数
> build.gradle 主模块中依赖和子模块中依赖的关系 ？