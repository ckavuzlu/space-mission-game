package com.example.spacemission.ui.configuration

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.spacemission.R
import com.example.spacemission.databinding.FragmentConfigurationPageBinding
import com.example.spacemission.util.Constants.COLOR_TEXT
import com.example.spacemission.ui.MainActivity
import com.example.spacemission.util.Constants.GAME_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfigurationPageFragment : Fragment() {

    private lateinit var configurationPageBinding: FragmentConfigurationPageBinding
    private val configurationViewModel: ConfigurationViewModel by viewModels()
    lateinit var mToast: Toast

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configurationPageBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_configuration_page,
            container,
            false
        )

        return configurationPageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurationPageBinding.spaceVM = configurationViewModel
        mToast = Toast(activity)

        initGoOnButton()
        initSkillsLiveDataListener()
        initErrorMessageLiveDataListener()
        initActiveGame()
    }

    fun initSkillsLiveDataListener() {
        configurationViewModel.durabilityLiveData.observe(viewLifecycleOwner, Observer {
            checkSlideState(configurationViewModel.checkRemainingSkill())

        })
        configurationViewModel.speedLiveData.observe(viewLifecycleOwner, Observer {
            checkSlideState(configurationViewModel.checkRemainingSkill())

        })
        configurationViewModel.storageLiveData.observe(viewLifecycleOwner, Observer {
            checkSlideState(configurationViewModel.checkRemainingSkill())

        })
    }

    fun checkSlideState(remaingSkill: Int) {
        configurationPageBinding.textRemainingPoints.text = remaingSkill.toString()
        if (remaingSkill < 0) {
            configurationPageBinding.textRemainingPoints.error = activity?.getString(R.string.error)
            configurationPageBinding.textRemainingPoints.setTextColor(Color.RED)
        } else {
            configurationPageBinding.textRemainingPoints.error = null
            configurationPageBinding.textRemainingPoints.setTextColor(COLOR_TEXT)
        }

    }

    fun initGoOnButton() {
        configurationPageBinding.btnGoOn.setOnClickListener {
            if (configurationViewModel.checkCredentials()) {
                (activity as MainActivity).mainActivityBinding.bottomNavigationView.visibility = View.VISIBLE
                (activity as MainActivity).replaceFragment(GAME_FRAGMENT)
            }
        }
    }

    fun initErrorMessageLiveDataListener() {
        configurationViewModel.errorMessageLiveData.observe(viewLifecycleOwner, Observer {
                mToast.cancel()
                mToast.setText(it)
                mToast.duration = Toast.LENGTH_SHORT
                mToast.show()

        })
    }

    fun initActiveGame() {
        configurationViewModel.activeGame.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(this@ConfigurationPageFragment.context)
                .setTitle(R.string.already_has_game)
                .setMessage(R.string.do_you_want_to_continue)
                .setNegativeButton(R.string.new_game) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.go_on) { dialog, _ ->
                    (activity as MainActivity).replaceFragment(GAME_FRAGMENT)
                    dialog.dismiss()
                }.create().show()
        })
    }


}