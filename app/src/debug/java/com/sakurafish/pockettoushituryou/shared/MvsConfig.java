package com.sakurafish.pockettoushituryou.shared;

/**
 * This class is implemented for MaterialSearchView <br>
 * https://github.com/Mauker1/MaterialSearchView
 */
public class MvsConfig {
    // br.com.mauker.MsvAuthorityの初期化時にMainApplicationクラスのContextオブジェクトから
    // getString(R.string.mvs_authority)のようにしようとするとNullPointerExceptionになってしまうため
    // このようなクラスを作成した
    public static final String mvsAnthority = "br.com.mauker.materialsearchview.searchhistorydatabase.debug";
}
