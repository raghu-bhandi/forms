/*
 * Copyright (c) 2019 simplity.org
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.simplity.fm.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author simplity.org
 *
 */
public class FileSystemStore extends DataStore {
	private static final String FOLDER = "c:/forms/";
	private static final String EXTN = ".json";
	static {
		File f = new File(FOLDER);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	@Override
	public boolean retrieve(String id, IoConsumer<Reader> consumer) throws IOException {
		File f = new File(FOLDER + id + EXTN);
		if (!f.exists()) {
			return false;
		}
		try (Reader reader = new FileReader(f)) {
			consumer.accept(reader);
		}
		return true;
	}

	@Override
	public void Store(String id, IoConsumer<Writer> consumer) throws IOException {
		File f = new File(FOLDER + id + EXTN);
		if (!f.exists()) {
			f.createNewFile();
		}
		try (Writer writer = new FileWriter(f)) {
			consumer.accept(writer);
		}
	}
}
