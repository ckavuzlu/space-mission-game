package com.example.spacemission.ui.gamepage

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.spacemission.R
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.database.entities.Game
import com.example.spacemission.databinding.FragmentGamePageBinding
import com.example.spacemission.model.GameResult
import com.example.spacemission.model.UIState
import com.example.spacemission.ui.MainActivity
import com.example.spacemission.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat


@AndroidEntryPoint
class GamePageFragment : Fragment() {

    private lateinit var gamePageBinding: FragmentGamePageBinding
    private val gamePageViewModel: GamePageViewModel by viewModels()
    val dec = DecimalFormat("#")
    var spaceStationList: List<SpaceStation> = mutableListOf()
    var currentGame: Game? = null
    var shownIndex = 0
    var isAllLoaded = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gamePageBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_page, container, false)
        return gamePageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingState()

        initGameInfoLiveData()
        initSpaceStationListLiveData()
        initGameDoneListenerLiveData()
        initCountTimeTimerLiveData()
        initUIStateLiveData()
        initErrorLiveData()

        initUIButtons()
    }

    fun initSearchView() {
        val nameList = spaceStationList.map { it.name }
        val stationAdapter: ArrayAdapter<String> = ArrayAdapter(
            activity as MainActivity,
            R.layout.support_simple_spinner_dropdown_item,
            nameList
        )
        gamePageBinding.searchView.setAdapter(stationAdapter)

        gamePageBinding.searchView.validator = object : AutoCompleteTextView.Validator {
            override fun isValid(text: CharSequence?): Boolean {
                if (nameList.contains(text.toString())) {
                    goNextStation(nameList.indexOf(text.toString()))

                    gamePageBinding.searchView.setText("")
                    return true
                }
                return false
            }

            override fun fixText(invalidText: CharSequence?): CharSequence {
                Toast.makeText(activity, R.string.can_not_find_station, Toast.LENGTH_SHORT).show()
                return ""

            }

        }

        gamePageBinding.searchView.setOnItemClickListener { _, _, _, _ ->
            gamePageBinding.searchView.clearFocus()
        }

        gamePageBinding.searchView.setOnEditorActionListener(OnEditorActionListener { _, _, _ ->
            gamePageBinding.searchView.clearFocus()
            true
        })

    }

    fun initCountTimeTimerLiveData() {
        gamePageViewModel.countDownTimer.observe(viewLifecycleOwner, Observer {
            gamePageBinding.txtCountDown.text = it.dec().toString()
        })
    }

    fun initGameInfoLiveData() {
        gamePageViewModel.gameLiveData.observe(viewLifecycleOwner, Observer {
            updateUI(it)
            currentGame = it
            checkState()

        })
    }

    fun initSpaceStationListLiveData() {
        gamePageViewModel.spaceStationListLiveData.observe(viewLifecycleOwner, Observer {
            spaceStationList = it
            showNextStation(shownIndex)
            initSearchView()
            checkState()
        })
    }

    fun updateUI(game: Game) {

        gamePageBinding.DS.text =
            activity?.getString(R.string.amout_of_DS, dec.format(game.durability).toInt())
        gamePageBinding.EUS.text =
            activity?.getString(R.string.amout_of_EUS, dec.format(game.speed).toInt())
        gamePageBinding.UGS.text =
            activity?.getString(R.string.amout_of_UGS, dec.format(game.storage).toInt())
        gamePageBinding.txtSpaceshipName.text = game.spaceshipName
        gamePageBinding.txtDamageCapacity.text = dec.format(game.damageCapacity)
        gamePageBinding.txtCurrentLocation.text = game.currentLocation
    }

    fun showNextStation(id: Int) {

        gamePageBinding.cardSpaceStation.txtPlanetName.text = spaceStationList[id].name
        gamePageBinding.cardSpaceStation.txtNeedAndCapacity.text =
            activity?.getString(
                R.string.need_capacity,
                spaceStationList[id].need,
                spaceStationList[id].capacity
            )
        gamePageBinding.cardSpaceStation.txtAmountOfTime.text =
            activity?.getString(R.string.distance, spaceStationList[id].distanceFromCurrent)
        if (spaceStationList[id].isFavorite) {
            gamePageBinding.cardSpaceStation.imgFavoriteIcon.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            gamePageBinding.cardSpaceStation.imgFavoriteIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
        var currentLocationName = currentGame?.currentLocation ?: ""
        if (currentLocationName == spaceStationList[id].name || currentLocationName == "" || spaceStationList[id].need == 0) {
            gamePageBinding.cardSpaceStation.btnTravel.visibility = View.INVISIBLE
        } else {
            gamePageBinding.cardSpaceStation.btnTravel.visibility = View.VISIBLE
        }
    }

    fun initUIButtons() {
        gamePageBinding.btnNextStation.setOnClickListener {
            goNextStation(shownIndex + 1)
        }
        gamePageBinding.btnPreviousStation.setOnClickListener {
            goNextStation(shownIndex - 1)
        }
        gamePageBinding.cardSpaceStation.imgFavoriteIcon.setOnClickListener {
            gamePageViewModel.changeFavStatus(spaceStationList[shownIndex])
            if (!spaceStationList[shownIndex].isFavorite) {
                gamePageBinding.cardSpaceStation.imgFavoriteIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
            } else {
                gamePageBinding.cardSpaceStation.imgFavoriteIcon.setImageResource(R.drawable.ic_baseline_star_24)
            }
        }
        gamePageBinding.cardSpaceStation.btnTravel.setOnClickListener {
            currentGame?.let { currentGame -> gamePageViewModel.travel(currentGame, spaceStationList[shownIndex]) }
        }
    }

    fun goNextStation(index: Int) {
        if (index < 0) {
            showNextStation(spaceStationList.lastIndex)
            shownIndex = spaceStationList.lastIndex
            return
        }
        if (index >= spaceStationList.size) {
            showNextStation(0)
            shownIndex = 0
            return
        }
        showNextStation(index)
        shownIndex = index

    }

    fun initGameDoneListenerLiveData() {
        gamePageViewModel.gameDone.observe(viewLifecycleOwner, Observer {
            val alertDialog = AlertDialog.Builder(this@GamePageFragment.context)

            when (it) {
                GameResult.WIN -> {
                    alertDialog.setTitle(R.string.you_win)
                    alertDialog.setMessage(R.string.win_message)
                }
                GameResult.LOSE -> {
                    alertDialog.setTitle(R.string.you_lose)
                    alertDialog.setMessage(R.string.lose_message)
                }
            }

            alertDialog.setPositiveButton(R.string.new_game) { dialog, _ ->
                dialog.dismiss()
                gamePageViewModel.newGame()
                (activity as MainActivity).mainActivityBinding.bottomNavigationView.visibility =
                    View.GONE
                (activity as MainActivity).replaceFragment(Constants.CONFIG_FRAGMENT)
            }
            alertDialog.setCancelable(false)
            alertDialog.create().show()
        })
    }

    fun initUIStateLiveData() {
        gamePageViewModel.uiStateLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                UIState.LOADING -> {
                    loadingState()
                }
                UIState.LIVE -> {
                    liveState()
                }
            }
        })
    }

    fun liveState() {
        gamePageBinding.cardSpaceStation.progressMain.visibility = View.GONE
        gamePageBinding.btnNextStation.isClickable = true
        gamePageBinding.btnPreviousStation.isClickable = true
        gamePageBinding.cardSpaceStation.btnTravel.isClickable = true
    }

    fun loadingState() {
        gamePageBinding.cardSpaceStation.progressMain.visibility = View.VISIBLE
        gamePageBinding.btnNextStation.isClickable = false
        gamePageBinding.btnPreviousStation.isClickable = false
        gamePageBinding.cardSpaceStation.btnTravel.isClickable = false
    }

    fun initErrorLiveData() {
        gamePageViewModel.errorLiveData.observe(viewLifecycleOwner, Observer {
            Toast.makeText(this@GamePageFragment.context, it.message, Toast.LENGTH_SHORT).show()
        })
    }

    fun checkState() {
        if (isAllLoaded == 0) {
            isAllLoaded++
        } else {
            gamePageViewModel.checkState()
            isAllLoaded = 0
        }
    }


}

