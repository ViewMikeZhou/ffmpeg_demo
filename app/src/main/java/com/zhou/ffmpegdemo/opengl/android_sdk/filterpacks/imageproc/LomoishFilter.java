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

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

import java.util.Date;
import java.util.Random;

public class LomoishFilter extends Filter {

    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize = 640;

    private Program mProgram;
    private Random mRandom;

    private int mWidth = 0;
    private int mHeight = 0;
    private int mTarget = FrameFormat.TARGET_UNSPECIFIED;

    private final String mLomoishShader =
            "precision mediump float;\n" +
            "uniform sampler2D tex_sampler_0;\n" +
            "uniform vec2 seed;\n" +
            "uniform float stepsizeX;\n" +
            "uniform float stepsizeY;\n" +
            "uniform float stepsize;\n" +
            "uniform vec2 scale;\n" +
            "uniform float inv_max_dist;\n" +
            "varying vec2 v_texcoord;\n" +
            "float rand(vec2 loc) {\n" +
            "  float theta1 = dot(loc, vec2(0.9898, 0.233));\n" +
            "  float theta2 = dot(loc, vec2(12.0, 78.0));\n" +
            "  float value = cos(theta1) * sin(theta2) + sin(theta1) * cos(theta2);\n" +
            // keep value of part1 in range: (2^-14 to 2^14).
            "  float temp = mod(197.0 * value, 1.0) + value;\n" +
            "  float part1 = mod(220.0 * temp, 1.0) + temp;\n" +
            "  float part2 = value * 0.5453;\n" +
            "  float part3 = cos(theta1 + theta2) * 0.43758;\n" +
            "  return fract(part1 + part2 + part3);\n" +
            "}\n" +
            "void main() {\n" +
            // sharpen
            "  vec3 nbr_color = vec3(0.0, 0.0, 0.0);\n" +
            "  vec2 coord;\n" +
            "  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n" +
            "  coord.x = v_texcoord.x - 0.5 * stepsizeX;\n" +
            "  coord.y = v_texcoord.y - stepsizeY;\n" +
            "  nbr_color += texture2D(tex_sampler_0, coord).rgb - color.rgb;\n" +
            "  coord.x = v_texcoord.x - stepsizeX;\n" +
            "  coord.y = v_texcoord.y + 0.5 * stepsizeY;\n" +
            "  nbr_color += texture2D(tex_sampler_0, coord).rgb - color.rgb;\n" +
            "  coord.x = v_texcoord.x + stepsizeX;\n" +
            "  coord.y = v_texcoord.y - 0.5 * stepsizeY;\n" +
            "  nbr_color += texture2D(tex_sampler_0, coord).rgb - color.rgb;\n" +
            "  coord.x = v_texcoord.x + stepsizeX;\n" +
            "  coord.y = v_texcoord.y + 0.5 * stepsizeY;\n" +
            "  nbr_color += texture2D(tex_sampler_0, coord).rgb - color.rgb;\n" +
            "  vec3 s_color = vec3(color.rgb + 0.3 * nbr_color);\n" +
            // cross process
            "  vec3 c_color = vec3(0.0, 0.0, 0.0);\n" +
            "  float value;\n" +
            "  if (s_color.r < 0.5) {\n" +
            "    value = s_color.r;\n" +
            "  } else {\n" +
            "    value = 1.0 - s_color.r;\n" +
            "  }\n" +
            "  float red = 4.0 * value * value * value;\n" +
            "  if (s_color.r < 0.5) {\n" +
            "    c_color.r = red;\n" +
            "  } else {\n" +
            "    c_color.r = 1.0 - red;\n" +
            "  }\n" +
            "  if (s_color.g < 0.5) {\n" +
            "    value = s_color.g;\n" +
            "  } else {\n" +
            "    value = 1.0 - s_color.g;\n" +
            "  }\n" +
            "  float green = 2.0 * value * value;\n" +
            "  if (s_color.g < 0.5) {\n" +
            "    c_color.g = green;\n" +
            "  } else {\n" +
            "    c_color.g = 1.0 - green;\n" +
            "  }\n" +
            "  c_color.b = s_color.b * 0.5 + 0.25;\n" +
            // blackwhite
            "  float dither = rand(v_texcoord + seed);\n" +
            "  vec3 xform = clamp((c_color.rgb - 0.15) * 1.53846, 0.0, 1.0);\n" +
            "  vec3 temp = clamp((color.rgb + stepsize - 0.15) * 1.53846, 0.0, 1.0);\n" +
            "  vec3 bw_color = clamp(xform + (temp - xform) * (dither - 0.5), 0.0, 1.0);\n" +
            // vignette
            "  coord = v_texcoord - vec2(0.5, 0.5);\n" +
            "  float dist = length(coord * scale);\n" +
            "  float lumen = 0.85 / (1.0 + exp((dist * inv_max_dist - 0.73) * 20.0)) + 0.15;\n" +
            "  gl_FragColor = vec4(bw_color * lumen, color.a);\n" +
            "}\n";

    public LomoishFilter(String name) {
        super(name);
        mRandom = new Random(new Date().getTime());
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
                ShaderProgram shaderProgram = new ShaderProgram(context, mLomoishShader);
                shaderProgram.setMaximumTileSize(mTileSize);
                mProgram = shaderProgram;
                break;

            default:
                throw new RuntimeException("Filter Sharpen does not support frames of " +
                    "target " + target + "!");
        }
        mTarget = target;
    }

    private void initParameters() {
        if (mProgram !=null) {
            float scale[] = new float[2];
            if (mWidth > mHeight) {
                scale[0] = 1f;
                scale[1] = ((float) mHeight) / mWidth;
            } else {
                scale[0] = ((float) mWidth) / mHeight;
                scale[1] = 1f;
            }
            float max_dist = ((float) Math.sqrt(scale[0] * scale[0] + scale[1] * scale[1])) * 0.5f;

            mProgram.setHostValue("scale", scale);
            mProgram.setHostValue("inv_max_dist", 1.0f / max_dist);

            mProgram.setHostValue("stepsize", 1.0f / 255.0f);
            mProgram.setHostValue("stepsizeX", 1.0f / mWidth);
            mProgram.setHostValue("stepsizeY", 1.0f / mHeight);

            float seed[] = { mRandom.nextFloat(), mRandom.nextFloat() };
            mProgram.setHostValue("seed", seed);
        }
    }

    @Override
    public void process(FilterContext context) {
        // Get input frame
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();

        // Create program if not created already
        if (mProgram == null || inputFormat.getTarget() != mTarget) {
            initProgram(context, inputFormat.getTarget());
        }

        // Check if the frame size has changed
        if (inputFormat.getWidth() != mWidth || inputFormat.getHeight() != mHeight) {
            mWidth = inputFormat.getWidth();
            mHeight = inputFormat.getHeight();
            initParameters();
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
}
