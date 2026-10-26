package org.openhome.net.controlpoint;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * List of all UPnP devices with a given uuid (udn) on the current subnet.
 */
public class CpDeviceListUpnpUuid extends CpDeviceList
{
	private native CpDeviceListInitialised CpDeviceListCreateUpnpUuid(String aUuid);
	private native void CpDeviceListUpnpTryAdd(long aHandle, String aLocation);
	
	static
    {
        System.loadLibrary("ohNet");
        System.loadLibrary("ohNetJni");
    }
	
	/**
	 * Constructor.
	 * 
	 * @param aUuid		unique identifier for the target device.	
	 * @param aListener	delegate which will be called when a device is detected or removed.
	 * 					Clients who are interested in a newly detected device should call
	 *					<tt>CpDevice.addRef()</tt> on it and add it to some local collection
	 *					and must later call <tt>CpDevice.removeRef()</tt> to remove the
	 *					device from their local collection.
	 * 					Clients who had not previously claimed a reference to a device must
	 * 					not call <tt>CpDevice.removeRef()</tt>.
	 */
	public CpDeviceListUpnpUuid(String aUuid, ICpDeviceListListener aListener)
	{
		iListener = aListener;
		CpDeviceListInitialised init = CpDeviceListCreateUpnpUuid(aUuid);
		iHandle = init.getHandle();
		iCallback = init.getCallback();
	}

	/**
	 * Ask this list to fetch one known device description. The normal list constructor
	 * may also perform SSDP discovery; callers requiring isolation must bind to an
	 * appropriate network adapter. Destroy the list before closing the library.
	 *
	 * @param aLocation absolute HTTP URL of the device description.
	 */
	public void tryAdd(String aLocation)
	{
		if (iHandle == 0) {
			throw new IllegalStateException("Device list has been destroyed");
		}
		try {
			URI uri = new URI(aLocation);
			if (!"http".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
			    uri.getUserInfo() != null || uri.getFragment() != null) {
				throw new IllegalArgumentException("Expected an absolute HTTP device description URL");
			}
		} catch (URISyntaxException error) {
			throw new IllegalArgumentException("Invalid device description URL", error);
		}
		CpDeviceListUpnpTryAdd(iHandle, aLocation);
	}
}
