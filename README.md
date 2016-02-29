# WinkModule
facial recognition for Android

## description
このライブラリは、顔の状態を認識してコールバックするライブラリです。

## 取得できる動作
* 目を閉じる
* 目を長く閉じる

## Gradle

repositories {
maven { url 'http://raw.github.com/coe/WinkModule/master/repository/' }
}
dependencies {
compile 'jp.coe.winkfragment:winkfragment:1.2.1'
}

## How to use
- `implements WinkFragment.OnFragmentInteractionListener ` をあなたのActivityに追加
- `jp.coe.winkfragment.WinkFragment`をあなたのActivityレイアウトに追加

## Apps
 [手を使わず読書 WinkBook](https://play.google.com/store/apps/details?id=jp.coe.winkbook)

[自撮りも簡単！ウィンクカメラ Wink Camera](https://play.google.com/store/apps/details?id=jp.coe.winkcamera)

## more
付属のサンプルアプリも確認してください。

# sponsored by  [R-Learning](http://www.r-learning.co.jp "R-Learning")