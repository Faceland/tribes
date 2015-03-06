/*
 * This file is part of Tribes, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package me.topplethenun.tribes.utils;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
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
