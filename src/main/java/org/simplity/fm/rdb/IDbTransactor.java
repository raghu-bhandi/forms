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

package org.simplity.fm.rdb;

/**
 * interface for a class that wants to do db operations under a transaction
 * processing, so that any exception before the successful completion of all
 * updates must result in a roll-back.
 * 
 * @author simplity.org
 *
 */
public interface IDbTransactor {

	/**
	 * method that is called-back with a handler. this method can use the
	 * handler to any number of read/write operations. Any exception is caught
	 * by the caller and the transaction is rolled back in case auto-commit is not used.
	 * Transaction is also rolled-back if you return false;
	 * 
	 * @param handler
	 * @return true if all OK. false in case you detect some condition because
	 *         of which the transaction is to be cancelled. ignored if
	 *         auto-commit is used
	 */
	public boolean transact(RdbDriver.TransHandler handler);

}
