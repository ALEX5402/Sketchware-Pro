package com.besome.sketch.editor.event;

import a.a.a.Os;
import a.a.a.Ps;
import a.a.a.wB;
import a.a.a.xB;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollapsibleEventLayout extends FrameLayout {
    public Context a;
    public View b;
    public View c;
    public TextView d;
    public TextView e;
    public LinearLayout f;
    public LinearLayout g;
    public View h;
    public TextView i;
    public AnimatorSet j;
    public AnimatorSet k;
    public AnimatorSet l;
    public AnimatorSet m;
    public CollapsibleButton n;
    public CollapsibleButton o;
    public CollapsibleButton p;

    public CollapsibleEventLayout(Context context) {
        super(context);
        a(context);
    }

    @SuppressLint("ResourceType")
    public final void a(Context context) {
        this.a = context;
        wB.a(context, this, 2131427432);
        this.g = (LinearLayout) findViewById(2131231612);
        this.f = (LinearLayout) findViewById(2131231479);
        this.h = findViewById(2131230922);
        this.b = findViewById(2131230927);
        this.c = findViewById(2131230923);
        this.i = (TextView) findViewById(2131232287);
        this.d = (TextView) findViewById(2131230929);
        this.d.setText(xB.b().a(getContext(), 2131624980));
        this.e = (TextView) findViewById(2131230925);
        this.e.setText(xB.b().a(getContext(), 2131624974));
        this.h.setVisibility(4);
        this.f.setVisibility(8);
        this.i.setText(xB.b().a(getContext(), 2131624941));
        this.n = a(0, 2131165834, xB.b().a(context, 2131625027));
        this.o = a(1, 2131165524, xB.b().a(context, 2131624986));
        this.p = a(2, 2131165700, xB.b().a(context, 2131625576));
        this.p.setVisibility(8);
        this.g.addView(this.n);
        this.g.addView(this.o);
        this.g.addView(this.p);
        this.j = (AnimatorSet) AnimatorInflater.loadAnimator(context, 2130837513);
        this.k = (AnimatorSet) AnimatorInflater.loadAnimator(context, 2130837514);
        this.l = (AnimatorSet) AnimatorInflater.loadAnimator(context, 2130837507);
        this.m = (AnimatorSet) AnimatorInflater.loadAnimator(context, 2130837508);
    }

    @SuppressLint("WrongConstant")
    public void b() {
        this.o.setVisibility(8);
    }

    @SuppressLint("WrongConstant")
    public void c() {
        this.p.setVisibility(8);
    }

    public void d() {
        this.k.setTarget(this.g);
        this.j.setTarget(this.h);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(this.k).with(this.j);
        animatorSet.addListener(new Os(this));
        animatorSet.start();
    }

    @SuppressLint("WrongConstant")
    public void e() {
        this.o.setVisibility(0);
    }

    @SuppressLint("WrongConstant")
    public void f() {
        this.p.setVisibility(0);
    }

    public void setButtonOnClickListener(View.OnClickListener onClickListener) {
        this.b.setOnClickListener(onClickListener);
        this.c.setOnClickListener(onClickListener);
        this.n.setOnClickListener(onClickListener);
        this.o.setOnClickListener(onClickListener);
        this.p.setOnClickListener(onClickListener);
    }

    public final CollapsibleButton a(int i2, int i3, String str) {
        CollapsibleButton collapsibleButton = new CollapsibleButton(this.a);
        collapsibleButton.b = i2;
        collapsibleButton.d.setImageResource(i3);
        collapsibleButton.e.setText(str);
        return collapsibleButton;
    }

    public void a() {
        this.l.setTarget(this.g);
        this.m.setTarget(this.h);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(this.l).with(this.m);
        animatorSet.addListener(new Ps(this));
        animatorSet.start();
    }
}
