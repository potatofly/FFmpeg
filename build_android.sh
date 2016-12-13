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
