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
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.GenerateFieldPort;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.GenerateFinalPort;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.MutableFrameFormat;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.format.PrimitiveFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 */
public class InputStreamSource extends Filter {

    @GenerateFinalPort(name = "target")
    private String mTarget;

    @GenerateFieldPort(name = "stream")
    private InputStream mInputStream;

    @GenerateFinalPort(name = "format", hasDefault = true)
    private MutableFrameFormat mOutputFormat = null;

    public InputStreamSource(String name) {
        super(name);
    }

    @Override
    public void setupPorts() {
        int target = FrameFormat.readTargetString(mTarget);
        if (mOutputFormat == null) {
            mOutputFormat = PrimitiveFormat.createByteFormat(target);
        }
        addOutputPort("data", mOutputFormat);
    }

    @Override
    public void process(FilterContext context) {
        int fileSize = 0;
        ByteBuffer byteBuffer = null;

        // Read the file
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = mInputStream.read(buffer)) > 0) {
                byteStream.write(buffer, 0, bytesRead);
                fileSize += bytesRead;
            }
            byteBuffer = ByteBuffer.wrap(byteStream.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException(
                "InputStreamSource: Could not read stream: " + exception.getMessage() + "!");
        }

        // Put it into a frame
        mOutputFormat.setDimensions(fileSize);
        Frame output = context.getFrameManager().newFrame(mOutputFormat);
        output.setData(byteBuffer);

        // Push output
        pushOutput("data", output);

        // Release pushed frame
        output.release();

        // Close output port as we are done here
        closeOutputPort("data");
    }
}
