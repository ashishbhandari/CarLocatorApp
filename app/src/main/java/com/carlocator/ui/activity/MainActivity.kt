package com.carlocator.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.carlocator.R
import com.carlocator.ui.fragment.MainFragment
import com.carlocator.ui.fragment.MapUIFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.upper_container, MapUIFragment.newInstance()).commit()
            supportFragmentManager.beginTransaction().add(R.id.bottom_container, MainFragment.newInstance()).commit()
        }

    }

}
