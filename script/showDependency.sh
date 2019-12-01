#!/bin/sh

# プロジェクト直下のディレクトリで実行することを想定している
# ライブラリの依存関係を表示してdepends.txtという名前のファイルに出力する

./gradlew app:dependencies > depends.txt
