/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.zhou.ffmpegdemo.opengl.android_sdk.filterpacks.text;


import android.util.Log;

import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Filter;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FilterContext;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Frame;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FrameFormat;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.format.ObjectFormat;

/**
 * @hide
 */
public class StringLogger extends Filter {

    public StringLogger(String name) {
        super(name);
    }

    @Override
    public void setupPorts() {
        addMaskedInputPort("string", ObjectFormat.fromClass(Object.class,
                                                            FrameFormat.TARGET_SIMPLE));
    }

    @Override
    public void process(FilterContext env) {
        Frame input = pullInput("string");
        String inputString = input.getObjectValue().toString();
        Log.i("StringLogger", inputString);
    }

}
