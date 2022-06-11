package com.example.whack_a_mole.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whack_a_mole.appComponent
import com.example.whack_a_mole.databinding.ActivityResultBinding
import com.example.whack_a_mole.game.GameActivity
import com.example.whack_a_mole.menu.MenuActivity
import javax.inject.Inject

class ResultActivity : AppCompatActivity() {
    private lateinit var resultComponent: ResultComponent
    private lateinit var binding: ActivityResultBinding

    @Inject
    lateinit var resultViewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        resultComponent = this.appComponent.resultComponent().create()
        resultComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.extras?.getInt(RESULT_BUNDLE_KEY)
        if (result != null) {
            resultViewModel.updateRecord(result)
        }

        setupViews()
    }

    private fun setupViews() {
        binding.buttonPlayAgain.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            finish()
        }

        binding.buttonMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            finish()
        }

        val result = intent.extras?.getInt(RESULT_BUNDLE_KEY)
        binding.textViewResult.text = "Score:\n" + result?.toString()

        resultViewModel.getRecord().observe(this) { record ->
            binding.textViewPrevRecord.text = "Current record:\n$record"
        }
    }

    companion object {
        const val RESULT_BUNDLE_KEY = "result"
    }
}