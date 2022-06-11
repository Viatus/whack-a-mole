package com.example.whack_a_mole.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.whack_a_mole.R
import com.example.whack_a_mole.animation.ProgressBarAnimation
import com.example.whack_a_mole.appComponent
import com.example.whack_a_mole.databinding.ActivityGameBinding
import com.example.whack_a_mole.result.ResultActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

const val SCORE_TEXTVIEW_KEY = "score_textview"
const val TIMER_TEXTVIEW_KEY = "timer_textview"

const val HOLE_PROPORTION = 0.24
const val SINGLE_MARGIN_PROPORTION = 0.07

class GameActivity : AppCompatActivity() {
    private lateinit var gameComponent: GameComponent
    private lateinit var binding: ActivityGameBinding

    @Inject
    lateinit var gameViewModel: GameViewModel

    private val imageViewHoleList = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        gameComponent = this.appComponent.gameComponent().create()
        gameComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.constraintLayoutHoles.post {
            setupGrid()
        }

        binding.progressBarTimer.max = gameViewModel.countDownInit * 100
        binding.progressBarTimer.progress = gameViewModel.countDownInit * 100

        gameViewModel.getBeatenMoles().observe(this) { currentMolesBeaten ->
            binding.textviewScore.text = currentMolesBeaten.toString()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.countDownTimerFlow().collect { time ->
                    if (time == -1) {
                        val intent = Intent(this@GameActivity, ResultActivity::class.java)
                        intent.putExtra(
                            ResultActivity.RESULT_BUNDLE_KEY,
                            binding.textviewScore.text.toString().toInt()
                        )
                        startActivity(intent)
                        finish()
                        this.cancel()
                    } else {
                        binding.textviewTimer.text = time.toString()
                        val animation = binding.progressBarTimer.let {
                            ProgressBarAnimation(
                                it,
                                it.progress.toFloat(),
                                time.toFloat() * 100
                            )
                        }
                        animation.duration = 1000
                        binding.textviewTimer.startAnimation(animation)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.moleFlow().collect { holeWithMoleIndex ->
                    binding.imageViewMole.x =
                        binding.constraintLayoutHoles.x + imageViewHoleList[holeWithMoleIndex].x + imageViewHoleList[holeWithMoleIndex].width * 0.5f - binding.imageViewMole.width * 0.49f
                    binding.imageViewMole.y =
                        binding.constraintLayoutHoles.y + imageViewHoleList[holeWithMoleIndex].y

                    val animatorSlideUp = ObjectAnimator.ofFloat(
                        binding.imageViewMole,
                        View.TRANSLATION_Y,
                        binding.imageViewMole.y - (binding.imageViewMole.height * 0.75f)
                    )
                    animatorSlideUp.duration = 250L

                    val animatorSlideDown = ObjectAnimator.ofFloat(
                        binding.imageViewMole,
                        View.TRANSLATION_Y,
                        binding.imageViewMole.y
                    )
                    animatorSlideDown.duration = 250L
                    val animationSet = AnimatorSet()
                    animationSet.playSequentially(animatorSlideUp, animatorSlideDown)
                    animationSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.imageViewMole.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                        }
                    })
                    animationSet.start()
                }
            }
        }
    }

    private fun setupGrid() {
        val layoutWidth = binding.constraintLayoutHoles.width
        val layoutHeight = binding.constraintLayoutHoles.height

        binding.imageViewMole.layoutParams.width = (layoutWidth * HOLE_PROPORTION / 3 * 2).toInt()
        binding.imageViewMole.layoutParams.height =
            (layoutHeight * HOLE_PROPORTION / 3 * 2).toInt()
        binding.imageViewMole?.setOnTouchListener { view, motionEvent ->
            moleClicked(motionEvent.rawX, motionEvent.rawY)
            true
        }

        for (i in 0 until 9) {
            val imageViewHole = ImageView(this)
            imageViewHole.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.hole_alpha,
                    null
                )
            )

            imageViewHole.id = View.generateViewId()

            imageViewHole.scaleType = ImageView.ScaleType.CENTER_INSIDE

            val layoutParams = ConstraintLayout.LayoutParams(
                (HOLE_PROPORTION * layoutWidth).toInt(),
                (HOLE_PROPORTION * layoutHeight).toInt()
            )

            binding.constraintLayoutHoles.addView(imageViewHole, layoutParams)
            imageViewHoleList.add(imageViewHole)
        }

        val set = ConstraintSet()
        set.clone(binding.constraintLayoutHoles)

        for (i in 0 until 9) {
            when (i % 3) {
                0 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START,
                        0
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.END,
                        imageViewHoleList[i + 1].id,
                        ConstraintSet.START,
                        Random.nextInt((layoutWidth * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                }
                1 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.START,
                        imageViewHoleList[i - 1].id,
                        ConstraintSet.END,
                        Random.nextInt((layoutWidth * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.END,
                        imageViewHoleList[i + 1].id,
                        ConstraintSet.START,
                        Random.nextInt((layoutWidth * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                }
                2 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.START,
                        imageViewHoleList[i - 1].id,
                        ConstraintSet.END,
                        Random.nextInt((layoutWidth * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.END,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.END,
                        0
                    )
                }
            }
            when (i / 3) {
                0 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP,
                        0
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.BOTTOM,
                        imageViewHoleList[i + 3].id,
                        ConstraintSet.TOP,
                        Random.nextInt((layoutHeight * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                }
                1 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.TOP,
                        imageViewHoleList[i - 3].id,
                        ConstraintSet.BOTTOM,
                        Random.nextInt((layoutHeight * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.BOTTOM,
                        imageViewHoleList[i + 3].id,
                        ConstraintSet.TOP,
                        Random.nextInt((layoutHeight * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                }
                2 -> {
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.TOP,
                        imageViewHoleList[i - 3].id,
                        ConstraintSet.BOTTOM,
                        Random.nextInt((layoutHeight * SINGLE_MARGIN_PROPORTION).toInt() + 1)
                    )
                    set.connect(
                        imageViewHoleList[i].id,
                        ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM,
                        0
                    )
                }
            }
        }

        set.applyTo(binding.constraintLayoutHoles)
    }

    private fun moleClicked(touchX: Float, touchY: Float) {
        if (!gameViewModel.hitMole()) {
            return
        }

        binding.imageViewWham.x = touchX - binding.imageViewWham.width / 2.0f
        binding.imageViewWham.y = touchY - binding.imageViewWham.height

        val animatorAppearX =
            ObjectAnimator.ofFloat(binding.imageViewWham, View.SCALE_X, 0.0f, 1.0f)
        animatorAppearX.duration = 100L
        val animatorAppearY =
            ObjectAnimator.ofFloat(binding.imageViewWham, View.SCALE_Y, 0.0f, 1.0f)
        animatorAppearY.duration = 100L
        val animatorDisappearX =
            ObjectAnimator.ofFloat(binding.imageViewWham, View.SCALE_X, 1.0f, 0.0f)
        animatorDisappearX.duration = 100L
        val animatorDisappearY =
            ObjectAnimator.ofFloat(binding.imageViewWham, View.SCALE_Y, 1.0f, 0.0f)
        animatorDisappearY.duration = 100L

        val appearSet = AnimatorSet()
        appearSet.playTogether(animatorAppearX, animatorAppearY)
        val disappearSet = AnimatorSet()
        disappearSet.playTogether(animatorDisappearX, animatorDisappearY)

        val fullSet = AnimatorSet()
        fullSet.playSequentially(appearSet, disappearSet)

        fullSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                binding.imageViewWham.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                binding.imageViewWham.visibility = View.INVISIBLE
            }
        })

        fullSet.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SCORE_TEXTVIEW_KEY, binding.textviewScore.text.toString())
        outState.putString(TIMER_TEXTVIEW_KEY, binding.textviewTimer.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.textviewScore.text = savedInstanceState.getString(SCORE_TEXTVIEW_KEY)
        binding.textviewTimer.text = savedInstanceState.getString(TIMER_TEXTVIEW_KEY)
    }
}