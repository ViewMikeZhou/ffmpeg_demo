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


package com.zhou.ffmpegdemo.opengl.android_sdk.filterpacks.base;


import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Filter;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FilterContext;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Frame;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FrameFormat;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.GenerateFinalPort;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.MutableFrameFormat;

/**
 *
 */
public class RetargetFilter extends Filter {

    @GenerateFinalPort(name = "target", hasDefault = false)
    private String mTargetString;

    private MutableFrameFormat mOutputFormat;
    private int mTarget = -1;

    public RetargetFilter(String name) {
        super(name);
    }

    @Override
    public void setupPorts() {
        // Setup target
        mTarget = FrameFormat.readTargetString(mTargetString);

        // Add ports
        addInputPort("frame");
        addOutputBasedOnInput("frame", "frame");
    }

    @Override
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat retargeted = inputFormat.mutableCopy();
        retargeted.setTarget(mTarget);
        return retargeted;
    }

    @Override
    public void process(FilterContext context) {
        // Get input frame
        Frame input = pullInput("frame");

        // Create output frame
        Frame output = context.getFrameManager().duplicateFrameToTarget(input, mTarget);

        // Push output
        pushOutput("frame", output);

        // Release pushed frame
        output.release();
    }

}
