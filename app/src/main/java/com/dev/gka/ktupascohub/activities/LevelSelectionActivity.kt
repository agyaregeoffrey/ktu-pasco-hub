package com.dev.gka.ktupascohub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.rep.RepActivity
import com.dev.gka.ktupascohub.databinding.ActivityLevelSelectionBinding
import com.dev.gka.ktupascohub.utilities.PrefManager
import timber.log.Timber

class LevelSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLevelSelectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_level_selection)
        val levels: MutableList<String> = mutableListOf("300", "200", "100")

        val levelsAdapter = ArrayAdapter(this, R.layout.drop_down_list_item, levels)
        binding.levelDropdown.setAdapter(levelsAdapter)

        val pref = PrefManager.getInstance(applicationContext)
        val isRep = pref.hasRepSignedIn()
        val isStudent = pref.hasStudentSignedIn()

        binding.buttonFinish.setOnClickListener {
            if (binding.levelSelection.editText?.text?.isEmpty() == true) {
                binding.levelSelection.error = getString(R.string.field_cannot_be_empty)
            } else {
                val level = binding.levelSelection.editText?.text.toString()
                pref.studentLevel(level)

                if (isRep) {
                    Timber.d("IsRep $isRep")
                    startActivity(Intent(this, RepActivity::class.java))
                    finish()
                } else if (isStudent) {
                    Timber.d("IsStudent $isStudent")
                    startActivity(Intent(this, StudentActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        val prefs = PrefManager.getInstance(this)
        val studentLevel = prefs.getStudentLevel()
        val rep = prefs.hasRepSignedIn()
        val stud = prefs.hasStudentSignedIn()
        if (studentLevel != null) {
            if (rep) {
                startActivity(Intent(this, RepActivity::class.java))
                finish()
            } else if (stud) {
                startActivity(Intent(this, StudentActivity::class.java))
                finish()
            }
        }
        super.onStart()
    }
}