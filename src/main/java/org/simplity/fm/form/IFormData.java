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

package org.simplity.fm.form;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.simplity.fm.Message;
import org.simplity.fm.datatypes.ValueType;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This is the core of the forms-management (fm) module. It handles the
 * following aspects of form management
 * <ul>
 * <li>De-serialize data being transmitted/persisted as string into proper data
 * elements and populate itself</li>
 * <li>Validate data being populated and provide error messages on any
 * validation failures</li>
 * <li>allow services to get/set values</li>
 * <li>serialize data into string that can be persisted/transmitted that can be
 * de-serialized back</li>
 * <li>form also supports data exchange with standard data carriers like JSON
 * and XML (functionality to be added on a need basis)</li>
 * </ul>
 * A form can contain values (fields) and <code>IGrid</code>s. No arbitrary
 * object structure is allowed. Only exception is that a form can simply contain
 * other form, but just one level. Such a form is called CompositeForm.
 * <br/>
 * <br/>
 * Form is expected to contain small amount of data, and hence no methods
 * provided for streaming data. It deals with strings and objects instead
 * 
 * @author simplity.org
 *
 */
public interface IFormData {
	/**
	 * unique id assigned to this form. like customerDetails. This is unique
	 * across all types of forms within a project
	 * 
	 * @return non-null unique id
	 */
	public String getFormId();

	/**
	 * 
	 * @return version of the form based on which this form data is created
	 */
	public String getVersion();

	/**
	 *
	 * @return unique key/id for the document/record for which this form is
	 *         currently having data. It would be typically formed based on the
	 *         primary key(s) of the underlying document
	 */
	public String getDocumentId();

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
	 * load from a JSON node with no validation. To be called when loading from
	 * a dependable source
	 * 
	 * @param json
	 */
	public void load(ObjectNode json);

	/**
	 * load keys from a map. input is suspect.
	 * 
	 * @param values
	 *            non-null
	 * @param errors
	 *            non-null to which any validation errors are added
	 */
	public void loadKeys(Map<String, String> values, List<Message> errors);

	/**
	 * load keys from a JSON. input is suspect.
	 * 
	 * @param json
	 *            non-null
	 * @param errors
	 *            non-null to which any validation errors are added
	 */
	public void loadKeys(ObjectNode json, List<Message> errors);

	/**
	 * load from a JSON node that is not dependable. Like input from a client
	 * 
	 * @param jsonNode
	 *            non-null
	 * @param allFieldsAreOptional
	 *            true if this is for a draft-save operation, where we validate
	 *            only the fields that the user has opted to type. MUST be
	 *            called with true value for final submit operation
	 * @param errors
	 *            non-null to which any validation errors are added
	 */
	public void validateAndLoad(ObjectNode jsonNode, boolean allFieldsAreOptional, List<Message> errors);

	/**
	 * @param writer
	 * @throws IOException
	 */
	public void serializeAsJson(Writer writer) throws IOException;

	/**
	 * 
	 * @param fieldName
	 * @return Field in this form. null if no such field
	 */
	public ValueType getValueType(String fieldName);

	/**
	 * 
	 * @param fieldName
	 * @return Field in this form. null if no such field
	 */
	public int getFieldIndex(String fieldName);

	/**
	 * 
	 * @param fieldIndex
	 * @return null if there is no such field, or the field has null value.
	 *         String/Long/Date/Boolean depending on the type. Use more specific
	 *         getXXX if you know the type
	 */
	public Object getValue(int fieldIndex);

	/**
	 * 
	 * @param fieldIndex
	 * @return value of the field as text. null if no such field, or the field
	 *         has null value.
	 *         "0"/"1" if boolean, milliseconds in case of date
	 */
	public String getStringValue(int fieldIndex);

	/**
	 * 
	 * @param fieldIndex
	 * @return value of the field as a number. 0 if it is not field, or he field
	 *         has null, or the field has non-numeric text
	 *         0/1 for boolean and milliseconds for date
	 */
	public long getLongValue(int fieldIndex);

	/**
	 * 
	 * @param fieldIndex
	 * @return value of the field as Date. null if the field is not a date
	 *         field, or it has null value
	 */
	public Date getDateValue(int fieldIndex);

	/**
	 * 
	 * @param fieldIndex
	 * @return value of the field as boolean. false if no such field, or the
	 *         field is null,or the field has empty string, 0 or false value
	 *         true otherwise
	 */
	public boolean getBoolValue(int fieldIndex);

	/**
	 * parse the value to proper type and set it
	 * 
	 * @param fieldIndex
	 *            name of the field
	 * @param value
	 *            ignored if null. parsed and set if valid
	 * @return true if value was indeed set. false if field is not defined.
	 */
	public boolean setValue(int fieldIndex, String value);

	/**
	 * 
	 * @param fieldIndex
	 *            name of the field
	 * @param value
	 * 
	 * @return true if field exists, and is of String type. false otherwise, and
	 *         the value is not set
	 */
	public boolean setStringValue(int fieldIndex, String value);

	/**
	 * 
	 * @param fieldIndex
	 *            name of the field
	 * @param value
	 * 
	 * @return true if field exists, and is of Date type. false otherwise, and
	 *         the value is not set
	 */
	public boolean setDateValue(int fieldIndex, Date value);

	/**
	 * 
	 * @param fieldIndex
	 *            name of the field
	 * @param value
	 * 
	 * @return true if field exists, and is of boolean type. false otherwise,
	 *         and the value is not set
	 */
	public boolean setBoolValue(int fieldIndex, boolean value);

	/**
	 * 
	 * @param fieldIndex
	 *            name of the field
	 * @param value
	 * 
	 * @return true if field exists, and is of integer type. false otherwise,
	 *         and the value is not set
	 */
	public boolean setLongValue(int fieldIndex, long value);

	/**
	 * insert/create this form data into the db.
	 * @return true if it is created. false in case it failed because of an an existing form with the same id/key
	 * @throws SQLException 
	 */
	public boolean insertToDb() throws SQLException;
	/**
	 * update this form data back into the db. 
	 * @return true if it is indeed updated. false in case there was no row to update
	 * @throws SQLException 
	 */
	public boolean updateTo() throws SQLException;
	/**
	 * remove this form data from the db
	 * @return true if it is indeed deleted happened. false otherwise
	 * @throws SQLException 
	 */
	public boolean deleteFromDb() throws SQLException;
	/**
	 * remove this form data from the db
	 * @return true if it is indeed deleted happened. false otherwise
	 * @throws SQLException 
	 */
}