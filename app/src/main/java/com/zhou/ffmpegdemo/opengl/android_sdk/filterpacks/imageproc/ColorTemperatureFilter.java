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

public class ColorTemperatureFilter extends Filter {

    @GenerateFieldPort(name = "scale", hasDefault = true)
    private float mScale = 0.5f;

    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize = 640;

    private Program mProgram;
    private int mTarget = FrameFormat.TARGET_UNSPECIFIED;

    private final String mColorTemperatureShader =
            "precision mediump float;\n" +
            "uniform sampler2D tex_sampler_0;\n" +
            "uniform float scale;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n" +
            "  vec3 new_color = color.rgb;\n" +
            "  new_color.r = color.r + color.r * ( 1.0 - color.r) * scale;\n" +
            "  new_color.b = color.b - color.b * ( 1.0 - color.b) * scale;\n" +
            "  if (scale > 0.0) { \n" +
            "    new_color.g = color.g + color.g * ( 1.0 - color.g) * scale * 0.25;\n" +
            "  }\n" +
            "  float max_value = max(new_color.r, max(new_color.g, new_color.b));\n" +
            "  if (max_value > 1.0) { \n" +
            "     new_color /= max_value;\n" +
            "  } \n" +
            "  gl_FragColor = vec4(new_color, color.a);\n" +
            "}\n";

    public ColorTemperatureFilter(String name) {
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
                ShaderProgram shaderProgram = new ShaderProgram(context, mColorTemperatureShader);
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

        // Create program if not created already
        if (mProgram == null || inputFormat.getTarget() != mTarget) {
            initProgram(context, inputFormat.getTarget());
            updateParameters();
        }

        // Create output frame
        Frame output = context.getFrameManager().newFrame(inputFormat);

        // Process
        mProgram.process(input, output);

        // Push output
        pushOutput("image", output);

        // Release pushed frame
        output.release();
    }

    private void updateParameters() {
        mProgram.setHostValue("scale", 2.0f * mScale - 1.0f);
    }

    @Override
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (mProgram != null) {
            updateParameters();
        }
    }
}
