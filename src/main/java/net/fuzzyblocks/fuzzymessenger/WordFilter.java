/*
 * Copyright (c) 2012 - 2014 Chris Darnell (cedeel).
 * All rights reserved.
 * 
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fuzzyblocks.fuzzymessenger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * @author cedeel
 */
public class WordFilter {

    private Set<String> badWords;
    private List<String> replacementWords;
    private Random rand;

    public WordFilter(File badWordFile, File replacementFile) {
        try {
            badWords = new HashSet<String>(Files.readLines(badWordFile, Charsets.UTF_8));
        } catch (IOException ex) {
            FuzzyMessenger.logServer(Level.WARNING, "badwords.txt not found.");
        }

        try {
            replacementWords = Files.readLines(replacementFile, Charsets.UTF_8);
        } catch (IOException e) {
            FuzzyMessenger.logServer(Level.WARNING, "replacements.txt not found.");
        }

        rand = new Random();
    }

    public String filter(String input) {
        String out = input;
        for (String word : badWords) {
            String replaced = replacementWords.get(rand.nextInt(replacementWords.size()));
            out = out.replaceAll(word, replaced);
        }
        return out;
    }

    public boolean isBadWord(String input) {
        return badWords.contains(input);
    }
}
