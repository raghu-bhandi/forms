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

import java.util.List;
import java.util.Map;

import org.simplity.fm.Message;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * represents data that is exchanged between client and server
 * 
 * @author simplity.org
 *
 */
public interface IFormData {

	/**
	 * 
	 * @return unique key of for this form data.
	 */
	public String getKey();

	/**
	 * 
	 * @return non-null structure of the data
	 */
	public IDataStructure getStructure();

	/**
	 * de-serialize text into data. used when the data is known to be valid, and
	 * need not be validated. Typically when this is de-serialized from
	 * persistence layer.
	 * 
	 * @param data
	 *            text that is the result of serialize() of this DataStructure.
	 * 
	 */
	public void deserialize(String data);

	/**
	 * de-serialize with validations
	 * 
	 * @param data
	 *            coming from a client
	 * @param errors
	 *            list to which, any validation errors are added
	 * @return true if allOk. No errors are added to the list. False in case one
	 *         or more validation errors are added to the list
	 */
	public boolean deserialize(String data, List<Message> errors);

	/**
	 * 
	 * @return a string that contains all the data from this data-structure.
	 *         This string can be used to transmit all data across
	 *         layers/network and can be de-serialized back to this data
	 *         structure
	 */
	public String serialize();

	/**
	 * 
	 * @param gridName
	 * @return data grid, null if no grid with this name
	 */
	public IDataGrid getGrid(String gridName);

	/**
	 * 
	 * @param fieldName
	 * @return null if there is no such field.
	 */
	public String getFieldValue(String fieldName);

	/**
	 * 
	 * @param fieldName
	 * @return value of the field as a number. 0 if it is not a number, or it s
	 *         not a field
	 */
	public long getLongValue(String fieldName);

	/**
	 * value is set if field name is valid for this structure
	 * 
	 * @param fieldName
	 *            name of the field
	 * @param value
	 *            will be converted to long if required
	 * @return true if value was indeed set. false if field is not defined
	 */
	public boolean setValue(String fieldName, String value);

	/**
	 * value is set if field name is valid for this structure
	 * 
	 * @param fieldName
	 *            name of the field
	 * @param value
	 *            will be converted to text if required
	 * @return true if value was indeed set. false if field is not defined
	 */
	public boolean setLongValue(String fieldName, long value);

	/**
	 * load from a JSON node with no validation. To be called when loading from
	 * a dependable source
	 * 
	 * @param jsonNode
	 */
	public void load(JsonNode jsonNode);

	/**
	 * load from a JSON node that is not dependable. Like input from a client
	 * 
	 * @param values
	 *            non-null
	 * @param errors
	 *            non-null
	 * @return true if all OK. false if at least one error message is added to
	 *         the errors list
	 */
	public boolean validateAndLoad(Map<String, String> values, List<Message> errors);

	/**
	 * load from a JSON node that is not dependable. Like input from a client
	 * 
	 * @param jsonNode
	 *            non-null
	 * @param errors
	 *            non-null
	 * @return true if all OK. false if at least one error message is added to
	 *         the errors list
	 */
	public boolean validateAndLoad(JsonNode jsonNode, List<Message> errors);

	/**
	 * @return serialize to a JSON node
	 */
	public JsonNode toJson();
}
