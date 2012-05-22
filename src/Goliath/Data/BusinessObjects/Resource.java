/* ========================================================
 * Resource.java
 *
 * Author:      kenmchugh
 * Created:     Dec 15, 2010, 12:02:31 AM
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Data.BusinessObjects;

import Goliath.Data.DataObjects.MimeType;
import Goliath.Data.DataObjects.ResourceValue;
import Goliath.Exceptions.Exception;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Dec 15, 2010
 * @author      kenmchugh
**/
public class Resource extends BusinessObject<Goliath.Data.DataObjects.Resource>
{
    
//    private Goliath.Constants.MimeType m_oActualReferenceType;
//    private Goliath.Constants.MimeType m_oActualContentType;
    
//    private MimeType m_oReferenceType;
//    private MimeType m_oContentType;

    private ResourceValue m_oResourceValue;

    /**
     * Creates a new instance of Resource
     */
    public Resource()
            throws InstantiationException, IllegalAccessException
    {
        super(Goliath.Data.DataObjects.Resource.class);
    }

    /**
     * Creates a new resource using toResource as the primary object
     * @param the primary data object to use for this business object
     */
    public Resource(Goliath.Data.DataObjects.Resource toResource)
    {
        super(toResource);
    }
    
    /**
     * Creates an instance of the Resource and loads it from the database, 
     * if the guid can not be found in the db then a new object is created in 
     * memory but not stored to the data store
     * @param tcGUID the guid of the resource to attempt to load
     */
    public Resource(String tcGUID)
    {
        super(Goliath.Data.DataObjects.Resource.class, tcGUID);
    }
    
    /**
     * Gets the resource value record for this resource
     * @return the resource value record
     */
    protected ResourceValue getResourceValue()
    {
        m_oResourceValue = getPrimaryObject().getResourceValue();
        if (m_oResourceValue == null)
        {
            m_oResourceValue = new Goliath.Data.DataObjects.ResourceValue();
            getPrimaryObject().setResourceValue(m_oResourceValue);
        }
        return m_oResourceValue;
    }
    
    
    // TODO: Implement reading and writing to the a stream
    
    /**
     * Gets the byte data of the object stored in this resource
     * @return the value of this resource
     */
    public byte[] getValue()
    {
        ResourceValue loValue = getResourceValue();
        return loValue != null ? loValue.getResourceContent() : null;
    }

    /**
     * Sets the byte data of the object stored in this resource
     */
    public void setValue(byte[] taValueContents)
    {
        ResourceValue loValue = getResourceValue();
        if (loValue == null)
        {
            loValue = new ResourceValue();
            getPrimaryObject().setResourceValue(loValue);
        }
        loValue.setResourceContent(taValueContents);
    }
    
    /**
     * Gets the type that is stored as the value in the byte field for this resource
     * this may not be the final value of the content, but the type that is stored
     * to direct us to the content.  If this type is the same as the content type, then
     * the assumption is that the type of the data object is this mime type
     * @return the reference type of the byte field
     */
    public Goliath.Constants.MimeType getReferenceType()
    {
        MimeType loMimeType = getResourceValue().getReferenceType();
        if (loMimeType == null)
        {
            return null;
        }
        return Goliath.Constants.MimeType.getEnumeration(Goliath.Constants.MimeType.class, loMimeType.getGUID());

//        if (m_oActualReferenceType == null)
//        {
//            if (m_oReferenceType == null)
//            {
//                ResourceValue loValue = getResourceValue();
//                if (loValue != null)
//                {
//                    m_oReferenceType = loValue.getReferenceType();
//                    if (m_oReferenceType != null)
//                    {
//                        m_oActualReferenceType = Goliath.Constants.MimeType.getEnumeration(Goliath.Constants.MimeType.class, m_oReferenceType.getGUID());
//                    }
//                }
//            }
//        }
//        return m_oActualReferenceType;
    }
    
    /**
     * Sets the reference type of the byte data.  The reference type is the type
     * that is used to load the content.
     * @param toMimeType the mime type to set the resource to
     */
    public void setReferenceType(Goliath.Constants.MimeType toMimeType)
    {
        getResourceValue().setReferenceType(MimeType.getMimeTypeFor(toMimeType));
    }

    /**
     * Gets the type that is the final content type for this resource
     * When this resource is displayed to the user, it should be displayed as this
     * content type
     * @return the reference type of the byte field
     */
    public Goliath.Constants.MimeType getContentType()
    {
        MimeType loMimeType = getResourceValue().getContentType();
        if (loMimeType == null)
        {
            return null;
        }
        return Goliath.Constants.MimeType.getEnumeration(Goliath.Constants.MimeType.class, loMimeType.getGUID());

//        if (m_oActualContentType == null)
//        {
//            if (m_oContentType == null)
//            {
//                ResourceValue loValue = getResourceValue();
//                if (loValue != null)
//                {
//                    m_oContentType = loValue.getContentType();
//                    if (m_oContentType != null)
//                    {
//                        m_oActualContentType = Goliath.Constants.MimeType.getEnumeration(Goliath.Constants.MimeType.class, m_oContentType.getGUID());
//                    }
//                }
//            }
//        }
//        return m_oActualContentType;
    }
    
    /**
     * Sets the content type of the byte data.  This is the type that is to be
     * used for the rendering of the final data
     * @param toMimeType the mime type to set the resource to
     */
    public void setContentType(Goliath.Constants.MimeType toMimeType)
    {
//        ResourceValue loValue = getResourceValue();
//        if (loValue == null)
//        {
//            loValue = new ResourceValue();
//            getPrimaryObject().setResourceValue(loValue);
//        }
        getResourceValue().setContentType(MimeType.getMimeTypeFor(toMimeType));
    }
    
    /**
     * Gets the resource value compressed indicator (whether the content is compressed)
     * @return true if the value content is compressed otherwise false
     */
    public boolean getCompressed()
    {
        ResourceValue loValue = getResourceValue();
        return (loValue != null) ? loValue.getCompressed() : false;
    }


    /**
     * Set the compressed indicator for the resource value content
     * @param toCompressed, true if the content is compressed otherwise false
     */
    public void setCompressed(boolean tlCompressed)
    {
        ResourceValue loValue = getResourceValue();
        loValue.setCompressed(tlCompressed);
    }
    
    @Override
    protected boolean onIsModified()
    {
        return getPrimaryObject().isModified() || (getResourceValue() != null && getResourceValue().isModified());
    }
    
    @Override
    protected void onSave() throws Exception
    {
        // Check if we are deleting or adding/updating this object
        if (isDeleted())
        {
            // First delete the resource
            if (getPrimaryObject().save())
            {
                // Then delete the resource value if needed
                ResourceValue loValue = getResourceValue();
                if (loValue != null)
                {
                    loValue.delete();
                    loValue.save();
                }
            }
        }
        else
        {
            // We need to make sure the resource value is saved first, if there is one
            ResourceValue loValue = getResourceValue();
            if (loValue != null)
            {
                MimeType loReferenceType = loValue.getReferenceType();
                if (loReferenceType.isNew() || loReferenceType.isModified())
                {
                    loReferenceType.save();
                    loValue.setReferenceTypeID(loReferenceType.getID());
                }

                MimeType loContentType = loValue.getContentType();
                if (!loContentType.getGUID().equals(loReferenceType.getGUID()))
                {
                    loContentType.save();
                    loValue.setContentTypeID(loContentType.getID());
                }
                else
                {
                    loValue.setContentTypeID(loReferenceType.getID());
                }

                loValue.save();
            }
            getPrimaryObject().save();
        }
    }

    @Override
    protected void onDelete()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected BusinessObject<Goliath.Data.DataObjects.Resource> onCopy()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
