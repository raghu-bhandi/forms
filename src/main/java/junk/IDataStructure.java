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

package junk;

import org.simplity.fm.data.Field;

/**
 * represents data structure that holds data from a service request or for a
 * service response.
 * 
 * @author simplity.org
 *
 */
public interface IDataStructure {
	
	/**
	 * @return a form data instance based on this data structure
	 */
	public IFormData newFormData();

	/**
	 * each data structure in a project has unique name that is used as id.
	 * 
	 * @return unique name/id of this data structure
	 */
	public String getName();

	/**
	 * 
	 * @return non-null array of field names
	 */
	public String[] getFieldNames();

	/**
	 * 
	 * @return non-null array of grid names
	 */
	public String[] getGridNames();
	/**
	 * 
	 * @param fieldName
	 * @return null if this is not a field. DataElement provides meta-data about
	 *         the field
	 */
	public Field getFieldType(String fieldName);
	
	/**
	 * 
	 * @param gridName
	 * @return sub0structure used  by this data grid. null if this is not the name of a grid
	 */
	public IDataStructure getGridStructure(String gridName);
}
