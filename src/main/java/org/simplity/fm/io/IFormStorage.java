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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Consumer;

/**
 * @author simplity.org
 *
 */
public interface IFormStorage {

	/**
	 * retrieve data and provide it to the consumer
	 * 
	 * @param id
	 *            non-null unique id of the document
	 * @param consumer
	 *            to which reader is provided for reading the content
	 * @return true if all ok. false if file is not located
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public abstract boolean retrieve(String id, Consumer<Reader> consumer) throws IOException;

	/**
	 * trash the document
	 * 
	 * @param id
	 *            unique id of the document
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public abstract void trash(String id) throws IOException;

	/**
	 * store the data from the
	 * 
	 * @param id
	 *            unique id of the document to be stored
	 * @param consumer
	 *            to which a writer is supplied to write the data to
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public abstract void store(String id, Consumer<Writer> consumer) throws IOException;
}
