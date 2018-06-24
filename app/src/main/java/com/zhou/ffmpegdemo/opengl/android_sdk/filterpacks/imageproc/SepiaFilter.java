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

package com.zhou.ffmpegdemo.opengl.android_sdk.filterpacks.imageproc;


import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Filter;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FilterContext;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Frame;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.FrameFormat;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.GenerateFieldPort;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.Program;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.core.ShaderProgram;
import com.zhou.ffmpegdemo.opengl.android_sdk.filterfw.format.ImageFormat;

public class SepiaFilter extends Filter {

    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize = 640;

    private Program mProgram;
    private int mTarget = FrameFormat.TARGET_UNSPECIFIED;

    private final String mSepiaShader =
            "precision mediump float;\n" +
            "uniform sampler2D tex_sampler_0;\n" +
            "uniform mat3 matrix;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n" +
            "  vec3 new_color = min(matrix * color.rgb, 1.0);\n" +
            "  gl_FragColor = vec4(new_color.rgb, color.a);\n" +
            "}\n";

    public SepiaFilter(String name) {
      super(name);
    }

    @Override
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(ImageFormat.COLORSPACE_RGBA));
        addOutputBasedOnInput("image", "image");
    }

    @Override
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    public void initProgram(FilterContext context, int target) {
        switch (target) {
            case FrameFormat.TARGET_GPU:
                ShaderProgram shaderProgram = new ShaderProgram(context, mSepiaShader);
                shaderProgram.setMaximumTileSize(mTileSize);
                mProgram = shaderProgram;
                break;

            default:
                throw new RuntimeException("Filter Sharpen does not support frames of " +
                    "target " + target + "!");
        }
        mTarget = target;
    }

   @Override
    public void process(FilterContext context) {
        // Get input frame
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();

        // Create output frame
        Frame output = context.getFrameManager().newFrame(inputFormat);

        // Create program if not created already
        if (mProgram == null || inputFormat.getTarget() != mTarget) {
            initProgram(context, inputFormat.getTarget());
            initParameters();
        }

        // Process
        mProgram.process(input, output);

        // Push output
        pushOutput("image", output);

        // Release pushed frame
        output.release();
    }

    private void initParameters() {
        float weights[] = { 805.0f / 2048.0f, 715.0f / 2048.0f, 557.0f / 2048.0f,
                            1575.0f / 2048.0f, 1405.0f / 2048.0f, 1097.0f / 2048.0f,
                            387.0f / 2048.0f, 344.0f / 2048.0f, 268.0f / 2048.0f };
        mProgram.setHostValue("matrix", weights);
    }
}
