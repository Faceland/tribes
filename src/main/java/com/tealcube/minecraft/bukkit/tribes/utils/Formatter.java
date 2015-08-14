/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.tribes.utils;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.AbstractQuestion;

import java.util.ArrayList;
import java.util.List;

public final class Formatter {

    private static final String Q_QUESTION = "<aqua>%question%";
    private static final String Q_OPTION = "<aqua>| %option% <white> %description%";

    private Formatter() {
        // do nothing
    }

    public static List<String> format(AbstractQuestion question) {
        List<String> strings = new ArrayList<>();
        strings.add(
                TextUtils.color(TextUtils.args(Q_QUESTION, new String[][]{{"%question%", question.getQuestion()}})));
        for (Option o : question.getOptions()) {
            strings.add(TextUtils.color(TextUtils.args(Q_OPTION, new String[][]{{"%option%", o.getCommand()},
                    {"%description%", o.getDescription()}})));
        }
        return strings;
    }

}
