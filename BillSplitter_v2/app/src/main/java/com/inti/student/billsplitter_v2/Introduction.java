package com.inti.student.billsplitter_v2;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

public final class Introduction extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("BillSplit", "Select an option and press submit.",R.drawable.tutorial1_v2, Color.parseColor("#51e2b7")));
        addSlide(AppIntro2Fragment.newInstance("BillSplit", "Select an option.", R.drawable.tutorial2_v2, Color.parseColor("#8c50e3")));
        addSlide(AppIntro2Fragment.newInstance("BillSplit", "Insert all the required fields and press submit.", R.drawable.tutorial3_v2, Color.parseColor("#FFDB3137")));
        addSlide(AppIntro2Fragment.newInstance("BillSplit", "Long click the bill item in order to edit or delete.", R.drawable.tutorial4_v2, Color.parseColor("#00bcd4")));

        showStatusBar(false);
        showSkipButton(false);
        setFadeAnimation();
    }

    @Override
    public void onDonePressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onSlideChanged() {

    }
}
