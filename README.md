# WinkModule
facial recognition for Android

## description
このライブラリは、顔の状態を認識してコールバックするライブラリです。

## 取得できる動作
* 目を閉じる
* 目を長く閉じる
* 左目を閉じる
* 右目を閉じる

## Gradle

repositories {
maven { url 'http://raw.github.com/coe/WinkModule/master/repository/' }
}
dependencies {
compile 'jp.coe.winkfragment:winkfragment:1.1.2'
}

## How to use
- `implements WinkFragment.OnFragmentInteractionListener ` をあなたのActivityに追加
- `jp.coe.winkfragment.WinkFragment`をあなたのActivityレイアウトに追加

## more
付属のサンプルアプリも確認してください。

# sponsored by  [R-Learning](http://www.r-learning.co.jp "R-Learning")