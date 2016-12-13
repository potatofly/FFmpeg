# FFmpeg for android .so生成并测试
  编译可用于jni调用的so库 脚本见 build_android.sh.<br>

# 环境
  ubuntu 12.04LTS x86_64<br>
  android-ndk-r8b<br>
  ffmpeg-3.2.2<br>
  android studio 2.1.2<br>

# 编译.so库
##Step 1
安装android linux NDK以及SDK，并配置环境变量；<br>
从[ffmpeg官网](http://ffmpeg.org/)下载ffmpeg源码包;也可以直接下载我项目中的ffmpeg压缩包ffmpeg-3.2.2.tar.bz2<br>

##Step 2
修改ffmpeg/configure文件<br>
将
```
SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'
SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR)$(SLIBNAME)'
```
修改为：<br>
```
SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'
```
这样编译出来的so命名才符合android的使用。
##Step 3
本项目提供编译arm平台库的sh文件 “build_android.sh”<br>
下面以build_android.sh为例进行说明：<br>
将ffmpeg-3.2.2.tar.bz2解压得到文件夹ffmpeg-3.2，将build_android.sh复制到ffmpeg-3.2目录下，并修改build_android.sh中的 TMPDIR、NDK、SYSROOT、TOOLCHAIN、PREFIX变量为自己的具体情况，具体如下：<br>
#####1.指定临时目录
```
export TMPDIR=/home/administrator/FFmpeg/tmpdir
```
指定一个临时目录，可以是任何路径，但必须保证存在，ffmpeg编译要用，否则会报错；<br>
#####2.指定NDK路径
```
NDK=/home/administrator/soft/android-ndk-r8b
```
#####3.指定使用NDK Platform版本
```
SYSROOT=$NDK/platforms/android-9/arch-arm/ 
```
安卓SDK是向下兼容的，但是NDK却是向上兼容的<br>
所以这里指定的ndk platform的路径，一定要`选择比你的目标机器使用的版本低的`，比如你的手机是android-15版本，那么就选择低于15的<br>
#####4.指定编译工具链
```
TOOLCHAIN=/home/administrator/soft/android-ndk-r8b/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86
```
#####5.指定编译后的安装目录
```
PREFIX=/home/administrator/FFmpeg/ffmpeg_install/arm/
```
这个目录是ffmpeg编译后的so的输出目录，会有一个include和lib文件夹生成在这里，这也是我们之后要在android apk中使用的.<br>
<br>
#####build_android.sh示例
可以修改该文件来控制ffmpeg的编译config来达到自己想要的库文件，我这里为了得到动态链接库，--enable-shared，并--disable-static，我开放了所有的编解码器，如果有不需要的，可以通过--disable-coder和--disable-decoder来指定，具体查看ffmpeg文档
```bash
#!/bin/bash
export TMPDIR=/home/administrator/FFmpeg/tmpdir
NDK=/home/administrator/soft/android-ndk-r8b
SYSROOT=$NDK/platforms/android-9/arch-arm/ 
TOOLCHAIN=/home/administrator/soft/android-ndk-r8b/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86

CPU=arm
PREFIX=/home/administrator/FFmpeg/ffmpeg_install/arm/
ADDI_CFLAGS="-marm"

function build_one
{
./configure \
--prefix=$PREFIX \
--enable-shared \
--disable-static \
--disable-doc \
--disable-ffmpeg \
--disable-ffplay \
--disable-ffprobe \
--disable-ffserver \
--disable-doc \
--disable-symver \
--disable-yasm \
--enable-small \
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--target-os=linux \
--arch=arm \
--enable-cross-compile \
--sysroot=$SYSROOT \
--extra-libs=-lgcc \
--extra-cflags="-Os -fpic $ADDI_CFLAGS" \
--extra-ldflags="$ADDI_LDFLAGS" \
$ADDITIONAL_CONFIGURE_FLAG
make clean
make
make install
}

build_one
```
##Step 4
```
执行编译脚本
cd ffmpeg-3.2
./build_android.sh
```

##Step 5
编译成功会在 $PREFIX 目录下生成 include和lib两个文件夹，将lib文件夹中的 pkgconfig 目录和so的链接文件删除，只保留so文件，然后将include 和lib两个目录一起copy到你的apk jni下去编译，测试生成的.so库。

# 使用编译生成的.so库

##Step 1
新建一个android studio工程，例子很简单一个主Activity，布局四个按钮加一个ScrollView用来显示测试信息。

##Step 2
as切换到project界面，在src/main/目录下新建jni目录，将上述编译成功的ffmpeg_install/arm/目录下的include目录和lib下的所有.so复制到jni/目录下。<br>
新建Android.mk，用来 编译适合android使用的.so库的makefile文件。新建ffmpegdemo.c用来实现jni接口中的本地c语言函数。

准备调用C语言函数。使用JNI调用C语言代码有两点需要做的步骤：

· 声明C语言函数对应的Java函数

· 声明要加载的类库

需要注意，C语言函数的声明要加上“native”关键字；加载类库的时候需要使用“System.loadLibrary()”方法。

更改主函数MainActivity.java
//加载.so库
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("ffmpegdemo");
    }
 //JNI 本地方法定义
    public native String avformatinfo();
    public native String avcodecinfo();
    public native String avfilterinfo();
    public native String configurationinfo();

根据Java对于C语言接口的定义，生成相应的接口函数声明。这一步需要用到JDK中的“javah”命令。命令行切换到src/main/java文件夹下，输入如下命令：
javah com.android.potatofly.ffmpegdemo.MainActivity
在src/main/java目录下会生成头文件,com_android_potatofly_ffmpegdemo_MainActivity.h
将头文件移到jni/目录下
在ffmpegdemo.c中实现生成的头文件中的方法，编写Android.mk，具体见项目代码

##Step 3
运行ndk-build
编写完Android的Makefile文件之后，就可以运行ndk-build编译生成可以通过JNI调用的.so类库了。
生成的.so文件在 src/main/libs/armeabi下
ndk-build本身是一个脚本，位于NDK根目录下。切换到Android程序src/main/jni目录中，直接执行脚本ndk-build就可以了。

##Step 4
修改build.gradle文件:
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "24.0.1"

    defaultConfig {
        applicationId "com.android.potatofly.ffmpegdemo"
        minSdkVersion 15
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    sourceSets.main{
        jni.srcDirs=[]
        jniLibs.srcDirs =['src/main/libs']    //使用本地编译成功的.so库
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.2.0'
}

