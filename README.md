# bizsocket

[![license](https://img.shields.io/hexpm/l/plug.svg)](https://raw.githubusercontent.com/baidao/bizsocket/master/LICENSE)

[ ![Download](https://api.bintray.com/packages/typ0520/maven/com.dx168.patchsdk%3Apatchsdk/images/download.svg) ](https://bintray.com/typ0520/maven/com.dx168.bizsocket%3Abizsocket-rx/_latestVersion)

异步socket，对一些业务场景做了支持,支持一对一请求、通知、串行请求合并、包分片处理、缓存、拦截器
对rxjava做了支持，提供类似于retrofit的支持

## 使用方式
- 1、添加依赖
````gradle
 buildscript {
    repositories {
       jcenter()
    }
 }
 dependencies {
    compile 'com.dx168.bizsocket:bizsocket-rx'
 }
````

