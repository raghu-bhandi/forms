package in.gov.itd.ef.data.generated;

import java.io.IOException;

import junk.IFormData;

/**
 * Interface to manage persistence of ITR data
 *
 * @author renga
 */
public interface IItrDataStore {

	/**
	 * @return number of seconds for which a lock would be held on a data after
	 *         the last operation. 0 if locking is not operational
	 */
	public int getLockValidityInSeconds();

	/**
	 * @param secs
	 *            non-zero positive number of seconds for which a lock would be
	 *            valid after the last operation. Lock is released after this.
	 */
	public void setLockValidityInSeconds(int secs);

	/**
	 * no locking mechanism required. All existing locks expire.
	 */
	public void disableLocking();

	/**
	 *
	 * @param id
	 *            non-null unique id of the data
	 * @return data for the key. null if no such key exists
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public IFormData get(String id) throws IOException;

	/**
	 *
	 * @param data
	 *            non-null
	 * @param keyForLock
	 *            if the caller had locked this for a get. null if locking is
	 *            not implemented
	 * @return new version of the persisted data. 0 if this is not saved due to
	 *         locking issues
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public int save(IFormData data, String keyForLock) throws IOException;

	/**
	 * lock this data for this process. returned key is valid for a pre-fixed
	 * amount of seconds, after which it expires
	 *
	 * @param id
	 *            non-null unique id of data
	 * @param forceIt
	 *            true if any existing lock is to be forced open. false to
	 *            respect locking arrangement
	 * @return null if it is already locked. this string is to be present for
	 *         saves. It is a good practice
	 * @throws IOException
	 *             in case of any error in persistence process
	 */
	public String lockData(String id, boolean forceIt) throws IOException;
}
