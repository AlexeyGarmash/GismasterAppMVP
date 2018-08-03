package com.example.alex.gismasterappmvp;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class Utils {
    /**
     * Устанавливает изображение в {@link ImageView} с помощью {@link Glide} по ссылке (URL)
     * @param context контекст {@link android.app.Activity}
     * @param view {@link ImageView}, к которому ведется привязка изображения
     * @param width новая ширина изображения
     * @param height новая высота изображения
     * @param imgURL ссылка на изображение
     */
    public static void setImageByURL(Context context, ImageView view, int width, int height, String imgURL){
        Glide.with(context)
                .load(imgURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

    /**
     * Устанавливает изображение в {@link ImageView} с помощью {@link Glide} по id ресурса.
     * @param context контекст {@link android.app.Activity}
     * @param view {@link ImageView}, к которому ведется привязка изображения
     * @param width новая ширина изображения
     * @param height новая высота изображения
     * @param id идентификатор ресурса
     */
    public static void setImageByResource(ImageView view, int id, int width, int height, Context context){
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

    public static void setRecyclerViewAnimator(RecyclerView recyclerView, BaseItemAnimator animator,  int duration){
        recyclerView.setItemAnimator(animator);
        recyclerView.getItemAnimator().setAddDuration(duration);
        recyclerView.getItemAnimator().setRemoveDuration(duration);
        recyclerView.getItemAnimator().setMoveDuration(duration);
        recyclerView.getItemAnimator().setChangeDuration(duration);
    }



    /*public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }*/
}
