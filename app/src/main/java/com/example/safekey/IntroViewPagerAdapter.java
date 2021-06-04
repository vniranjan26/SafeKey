package com.example.safekey;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;
public class IntroViewPagerAdapter extends PagerAdapter {

    Context mContext ;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);

        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);
        LottieAnimationView lottieAnimationView1 = layoutScreen.findViewById(R.id.imag1);
        LottieAnimationView lottieAnimationView2 = layoutScreen.findViewById(R.id.imag2);
        LottieAnimationView lottieAnimationView3 = layoutScreen.findViewById(R.id.imag3);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        switch (position)
        {
            case 0:
                lottieAnimationView3.setVisibility(View.GONE);
                lottieAnimationView2.setVisibility(View.GONE);
                lottieAnimationView1.setVisibility(View.VISIBLE);
                break;
            case 1:
                lottieAnimationView1.setVisibility(View.GONE);
                lottieAnimationView3.setVisibility(View.GONE);
                lottieAnimationView2.setVisibility(View.VISIBLE);
                break;
            case 2:
                lottieAnimationView1.setVisibility(View.GONE);
                lottieAnimationView2.setVisibility(View.GONE);
                lottieAnimationView3.setVisibility(View.VISIBLE);
                break;
        }

        container.addView(layoutScreen);

        return layoutScreen;





    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
