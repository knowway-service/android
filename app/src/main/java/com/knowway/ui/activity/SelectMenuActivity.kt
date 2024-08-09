package com.knowway.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.knowway.R
import com.knowway.ui.fragment.SelectFooterFragment

/**
 * 메뉴 선택 페이지 Activity
 *
 * @author 이주현
 * @since 2024.08.01
 * @version 1.0
 */

class SelectMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_menu)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.footer_fragment_container, SelectFooterFragment())
                .commit()
        }

        val innerLayoutTop = findViewById<RelativeLayout>(R.id.inner_layout_top)
        val innerLayoutBottom = findViewById<RelativeLayout>(R.id.inner_layout_bottom)

        innerLayoutTop.setOnClickListener {
            navigateToDepartmentStoreSearch(0)
        }

        innerLayoutBottom.setOnClickListener {
            navigateToDepartmentStoreSearch(1)
        }
    }

    private fun navigateToDepartmentStoreSearch(value: Int) {
        val intent = Intent(this, DepartmentStoreSearchActivity::class.java).apply {
            putExtra("key", value)
        }
        startActivity(intent)
    }
}
