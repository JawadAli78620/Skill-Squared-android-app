package com.example.skillsquared2.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ApplicationHandler {

    public static void intent(final Class<? extends Activity> ActivityToOpen) {
       /* Context context = SkillSquared2.getAppContext();
        Intent intent = new Intent(context, ActivityToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
    }
}
