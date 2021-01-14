# UniversalEdit [![](https://jitpack.io/v/Aronchenf/UniversalEdit.svg)](https://jitpack.io/#Aronchenf/UniversalEdit)
一个自定义EditText，功能包括有一键清除、密码显示、错误校验等。<br/>

## 如何使用
在项目根目录的build.gradle添加：
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在项目的build.gradle添加如下依赖：
```
 implementation 'com.github.Aronchenf:UniversalEdit:1.0'
```

### 在xml中引用

```xml
    <com.edit.universaledit.CommonEditText
        android:id="@+id/edit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="@string/app_name"
        app:drawableStart="@drawable/ic_delete_icon"
        app:encryptImageVisibility="visible"
        app:drawablePadding="20dp"
        app:hintColor="@color/colorPrimaryDark"/>
```
&nbsp;
## 说明
这个库是经过查找资料以及自己的一点不成熟想法开发的，小白第一次编写，代码水平不高，如有问题，欢迎大佬指教。
