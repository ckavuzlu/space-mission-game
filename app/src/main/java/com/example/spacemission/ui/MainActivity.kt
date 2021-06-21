package com.example.spacemission.ui

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spacemission.R
import com.example.spacemission.databinding.ActivityMainBinding
import com.example.spacemission.ui.configuration.ConfigurationPageFragment
import com.example.spacemission.ui.favorite.FavoriteListFragment
import com.example.spacemission.ui.gamepage.GamePageFragment
import com.example.spacemission.util.Constants.CONFIG_FRAGMENT
import com.example.spacemission.util.Constants.FAV_LIST_FRAGMENT
import com.example.spacemission.util.Constants.FRAGMENT_STATE
import com.example.spacemission.util.Constants.GAME_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var fragmentSavedState: HashMap<String, Fragment.SavedState?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mainActivityBinding.root)
        if (savedInstanceState == null) {
            fragmentSavedState = HashMap()
            replaceFragment(CONFIG_FRAGMENT)
        } else {
            fragmentSavedState =
                savedInstanceState.getSerializable(FRAGMENT_STATE) as HashMap<String, Fragment.SavedState?>
        }
        initButtonNavigationView()
    }

    fun initButtonNavigationView() {
        mainActivityBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.game -> {
                    replaceFragment(GAME_FRAGMENT)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.favorites -> {
                    replaceFragment(FAV_LIST_FRAGMENT)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }

            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(FRAGMENT_STATE, fragmentSavedState)
        super.onSaveInstanceState(outState)
    }

    fun replaceFragment(fragmentTag: String) {
        val newFrag: Fragment? = when (fragmentTag) {
            GAME_FRAGMENT -> GamePageFragment()
            FAV_LIST_FRAGMENT -> FavoriteListFragment()
            CONFIG_FRAGMENT -> ConfigurationPageFragment()
            else -> null
        }

        val currentFragment = supportFragmentManager
            .findFragmentById(R.id.frame_layout_container)


        if (!currentFragment?.tag.equals(fragmentTag)) {
            currentFragment?.let { frag ->
                fragmentSavedState.put(
                    frag.tag!!,
                    supportFragmentManager.saveFragmentInstanceState(frag)
                )
            }
            newFrag?.setInitialSavedState(fragmentSavedState[fragmentTag])
            newFrag?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_container, it, fragmentTag)
                    .commit()
            }
        }


    }


}
